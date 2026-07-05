package ticket.management.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ticket.management.backend.dto.TicketAiInsight;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Priority;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GeminiTicketServiceImplTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private GeminiTicketServiceImpl aiTicketService;

    @Test
    void shouldUseFallback_whenAiDisabled() {
        ReflectionTestUtils.setField(aiTicketService, "aiEnabled", false);
        ReflectionTestUtils.setField(aiTicketService, "objectMapper", new ObjectMapper());

        Ticket ticket = new Ticket();
        ticket.setTitle("Application crash on login page");
        ticket.setDescription("Users report the app crashes immediately after clicking login.");

        TicketAiInsight insight = aiTicketService.analyze(ticket);

        assertThat(insight.getSource()).isEqualTo("FALLBACK");
        assertThat(insight.getSuggestedPriority()).isEqualTo(Priority.CRITICAL);
        assertThat(insight.getTags()).contains("login");
    }

    @Test
    void shouldUseFallback_whenApiKeyMissing() {
        ReflectionTestUtils.setField(aiTicketService, "aiEnabled", true);
        ReflectionTestUtils.setField(aiTicketService, "apiKey", "");
        ReflectionTestUtils.setField(aiTicketService, "objectMapper", new ObjectMapper());

        Ticket ticket = new Ticket();
        ticket.setTitle("Payment checkout is slow");
        ticket.setDescription("Checkout page takes 30 seconds to load, causing user drop-off.");

        TicketAiInsight insight = aiTicketService.analyze(ticket);

        assertThat(insight.getSource()).isEqualTo("FALLBACK");
        assertThat(insight.getSuggestedPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(insight.getTags()).contains("payment");
    }
}