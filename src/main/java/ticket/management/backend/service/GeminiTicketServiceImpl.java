package ticket.management.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ticket.management.backend.dto.TicketAiInsight;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Category;
import ticket.management.backend.entity.enums.Priority;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiTicketServiceImpl implements AiTicketService {

    private final WebClient geminiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.model}")
    private String model;

    @Value("${ai.gemini.enabled:true}")
    private boolean aiEnabled;

    @Value("${ai.gemini.timeout-seconds:15}")
    private long timeoutSeconds;

    @Override
    public TicketAiInsight analyze(Ticket ticket) {
        if (!aiEnabled || apiKey == null || apiKey.isBlank()) {
            log.info("AI disabled or no API key configured, using fallback for ticket {}", ticket.getId());
            return fallback(ticket);
        }

        try {
            String prompt = buildPrompt(ticket);
            Map<String, Object> requestBody = buildRequestBody(prompt);

            String path = "/" + model + ":generateContent?key=" + apiKey;

            Map<String, Object> response = geminiWebClient.post()
                    .uri(path)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            String rawText = extractText(response);
            TicketAiInsight insight = parseInsight(rawText);
            insight.setSource("AI");
            return insight;

        } catch (Exception ex) {
            log.warn("Gemini API call failed for ticket {}: {}. Falling back to rule-based insight.",
                    ticket.getId(), ex.getMessage());
            return fallback(ticket);
        }
    }

    private String buildPrompt(Ticket ticket) {
        return """
            You are a support-ticket triage assistant. Analyze the ticket below and
            respond with ONLY valid JSON, no markdown formatting, no code fences,
            no commentary before or after. Always suggest the Priority & Category value
            for each time. Match exactly this schema:

            {
              "summary": "one concise sentence summarizing the issue",
              "suggestedPriority": "LOW or MEDIUM or HIGH or CRITICAL",
              "suggestedCategory": "BUG or FEATURE_REQUEST or SUPPORT or NETWORK or HARDWARE or SOFTWARE or OTHER",
              "tags": ["short-tag-1", "short-tag-2", "short-tag-3"],
              "suggestedResolution": "one or two sentence actionable suggestion for resolving this ticket"
            }

            Ticket Title: %s
            Ticket Description: %s
            """.formatted(ticket.getTitle(), ticket.getDescription());
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 400,
                        "responseMimeType", "application/json"
                )
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("Empty response from Gemini API");
        }
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("No candidates returned by Gemini API");
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    private TicketAiInsight parseInsight(String json) throws Exception {
        String cleaned = json.replaceAll("```json|```", "").trim();
        JsonNode node = objectMapper.readTree(cleaned);

        List<String> tags = new ArrayList<>();
        if (node.has("tags") && node.get("tags").isArray()) {
            node.get("tags").forEach(t -> tags.add(t.asText()));
        }

        return TicketAiInsight.builder()
                .summary(node.path("summary").asText(null))
                .suggestedPriority(parseEnumSafe(Priority.class, node.path("suggestedPriority").asText(null)))
                .suggestedCategory(parseEnumSafe(Category.class, node.path("suggestedCategory").asText(null)))
                .tags(tags)
                .suggestedResolution(node.path("suggestedResolution").asText(null))
                .build();
    }

    private <T extends Enum<T>> T parseEnumSafe(Class<T> type, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Could not map '{}' to enum {}", value, type.getSimpleName());
            return null;
        }
    }

    // Rule-based fallback - keeps the feature functional with no API key or if Gemini is down
    private TicketAiInsight fallback(Ticket ticket) {
        String text = ((ticket.getTitle() == null ? "" : ticket.getTitle()) + " "
                + (ticket.getDescription() == null ? "" : ticket.getDescription())).toLowerCase();

        Priority priority = Priority.LOW;
        if (text.contains("crash") || text.contains("down") || text.contains("urgent") || text.contains("outage")) {
            priority = Priority.CRITICAL;
        } else if (text.contains("error") || text.contains("bug") || text.contains("fail")) {
            priority = Priority.HIGH;
        } else if (text.contains("slow") || text.contains("delay") || text.contains("issue")) {
            priority = Priority.MEDIUM;
        }

        List<String> keywordBank = Arrays.asList(
                "login", "password", "payment", "checkout", "ui", "api",
                "performance", "crash", "database", "network"
        );
        List<String> tags = keywordBank.stream()
                .filter(text::contains)
                .collect(Collectors.toList());
        if (tags.isEmpty()) {
            tags = List.of("general");
        }

        return TicketAiInsight.builder()
                .summary(ticket.getTitle())
                .suggestedPriority(priority)
                .suggestedCategory(ticket.getCategory())
                .tags(tags)
                .suggestedResolution("AI service unavailable - please review this ticket manually.")
                .source("FALLBACK")
                .build();
    }
}
