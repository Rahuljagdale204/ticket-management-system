package ticket.management.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ticket.management.backend.dto.TicketDTO;
import ticket.management.backend.dto.TicketStatusDTO;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Status;

import java.util.List;

public interface TicketService {

    Ticket createTicket(Ticket newTicket);
    Ticket updateTicketInfo(Long ticketId, TicketDTO dto);
    Ticket updateTicketStatus(Long ticketId, TicketStatusDTO dto);
    Ticket retrieveTicket(Long ticketId);
    void removeTicket(Long ticketId);
    Page<Ticket> retrieveTickets(Pageable pageable);
    Ticket searchTicket(Long ticketId, Status status);
}
