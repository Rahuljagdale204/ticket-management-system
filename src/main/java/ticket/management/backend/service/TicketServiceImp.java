package ticket.management.backend.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ticket.management.backend.dto.TicketDTO;
import ticket.management.backend.dto.TicketStatusDTO;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Status;
import ticket.management.backend.repository.TicketRepository;
import ticket.management.backend.util.exceptions.InvalidInputException;
import ticket.management.backend.util.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.Optional;

import static ticket.management.backend.util.EntityUtils.updateIfNotNull;

@Slf4j
@Service
@Qualifier("TicketServiceImp")
public class TicketServiceImp implements TicketService {
    private final TicketRepository ticketRepository;

    public TicketServiceImp(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket createTicket(Ticket newTicket){
        log.info("Creating a new ticket");
        newTicket.setStatus(Status.NEW);
        newTicket.setCreationDate(Instant.now());
        newTicket.setDeleted(false);
        Ticket createdTicket = ticketRepository.save(newTicket);
        log.info("Ticket created successfully : {}", createdTicket.getId());
        return createdTicket;
    }

    public static Ticket unwrapTicket(Long ticketId, Optional<Ticket> optionalTicket) {
        return optionalTicket.orElseThrow(() -> new ResourceNotFoundException(ticketId, Ticket.class));
    }

    @Transactional
    @Override
    public Ticket updateTicketInfo(Long ticketId, TicketDTO dto) {
        log.info("Updating a ticket");
        Ticket retrievedTicket = unwrapTicket(ticketId, ticketRepository.findById(ticketId));
        if(retrievedTicket ==null) {
            log.warn("Ticket not found for Id : {}", ticketId);
            throw new ResourceNotFoundException(ticketId, Ticket.class);
        }

        updateIfNotNull(retrievedTicket::setTitle, dto.getTitle());
        updateIfNotNull(retrievedTicket::setDescription, dto.getDescription());
        updateIfNotNull(retrievedTicket::setCategory, dto.getCategory());
        updateIfNotNull(retrievedTicket::setPriority, dto.getPriority());
        Ticket updatedTicket = ticketRepository.save(retrievedTicket);
        log.info("Ticket updated successfully : {}", updatedTicket.getId());
        return updatedTicket;
    }
    private boolean isStatusChanged(Status oldStatus, Status newStatus) {
        return newStatus != null && !oldStatus.equals(newStatus);
    }

    @Override
    public Ticket updateTicketStatus(Long ticketId, TicketStatusDTO dto) {
        log.info("Updating a ticket status");
        Ticket retrievedTicket = unwrapTicket(ticketId, ticketRepository.findById(ticketId));
        if(retrievedTicket ==null) {
            log.warn("Unable to Update Status - Ticket not found for Id : {}", ticketId);
            throw new ResourceNotFoundException(ticketId, Ticket.class);
        }

        Status oldStatus = retrievedTicket.getStatus();

        updateIfNotNull(retrievedTicket::setStatus, dto.getStatus());

        Ticket savedTicket = ticketRepository.save(retrievedTicket);

        if (isStatusChanged(oldStatus, dto.getStatus())) {
            log.info("Ticket status updated successfully : {}", savedTicket.getId());
        }
        return savedTicket;
    }

    @Override
    public Ticket retrieveTicket(Long ticketId) {
        log.info("Retrieving a ticket : {}", ticketId);
        return ticketRepository.findById(ticketId)
                .orElseThrow(() ->new ResourceNotFoundException("Ticket not found with id : " + ticketId));
    }

    @Override
    public void removeTicket(Long ticketId) {
        log.info("Removing a ticket : {}", ticketId);
        Ticket retrievedTicket = unwrapTicket(ticketId, ticketRepository.findById(ticketId));
        if(retrievedTicket ==null) {
            log.warn("Unable to Remove - Ticket not found for Id : {}", ticketId);
            throw new ResourceNotFoundException(ticketId, Ticket.class);
        }

        retrievedTicket.setDeleted(true);
        ticketRepository.deleteById(ticketId);
        log.info("Ticket removed successfully : {}", ticketId);
    }

    @Override
    public Page<Ticket> retrieveTickets(Pageable pageable) {
        log.info("Retrieving all tickets");
        return ticketRepository.findAll(pageable);
    }

    @Override
    public Ticket searchTicket(Long ticketId, Status status) {
        if(status == null) {
            throw new InvalidInputException("Invalid Status");
        }

        log.info("Request to Search a ticket for Id: {}", ticketId);
        return unwrapTicket(ticketId, ticketRepository.findById(ticketId));
    }
}
