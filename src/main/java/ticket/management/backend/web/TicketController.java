package ticket.management.backend.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.management.backend.dto.TicketDTO;
import ticket.management.backend.dto.TicketStatusDTO;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Status;
import ticket.management.backend.service.TicketService;

@RestController
@RequestMapping(value = "/rest/v1/api/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {
    private final TicketService ticketService;

    public TicketController(@Qualifier("TicketServiceImp") TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<String> createTicket(@RequestBody @Valid Ticket newTicket) {
        ticketService.createTicket(newTicket);
        return ResponseEntity.status(HttpStatus.CREATED).body("Ticket created successfully");
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> retrieveTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.retrieveTicket(ticketId));
    }

    @PatchMapping("/{ticketId}/info")
    public ResponseEntity<Ticket> updateTicketInfo(@PathVariable Long ticketId,
            @RequestBody @Valid TicketDTO dto) {
        return ResponseEntity.ok(ticketService.updateTicketInfo(ticketId, dto));
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateTicketStatus(@PathVariable Long ticketId,
            @RequestBody @Valid TicketStatusDTO dto) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, dto));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Ticket>> retrieveTicketsByCreator(Pageable pageable) {
        return ResponseEntity.ok(ticketService.retrieveTickets(pageable));
    }

    @GetMapping("{ticketId}/search")
    public ResponseEntity<Ticket> searchByIdAndStatus(@PathVariable Long ticketId, @RequestParam Status status) {
        return ResponseEntity.ok(ticketService.searchTicket(ticketId, status));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> removeTicket(@PathVariable Long ticketId) {
        ticketService.removeTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
