package ticket.management.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ticket.management.backend.entity.enums.Status;

@Getter
@Setter
@AllArgsConstructor
public class TicketStatusDTO {

    @NotNull(message = "Status cannot be null")
    private Status status;
}
