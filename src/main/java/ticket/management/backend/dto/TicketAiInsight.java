package ticket.management.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ticket.management.backend.entity.enums.Category;
import ticket.management.backend.entity.enums.Priority;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAiInsight {
    private String summary;
    private Priority suggestedPriority;
    private Category suggestedCategory;
    private List<String> tags;
    private String suggestedResolution;
    private String source;
}
