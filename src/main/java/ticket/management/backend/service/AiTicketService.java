package ticket.management.backend.service;

import ticket.management.backend.dto.TicketAiInsight;
import ticket.management.backend.entity.Ticket;

public interface AiTicketService {
    TicketAiInsight analyze(Ticket ticket);
}
