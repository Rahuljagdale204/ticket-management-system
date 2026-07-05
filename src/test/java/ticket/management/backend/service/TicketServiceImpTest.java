package ticket.management.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import ticket.management.backend.dto.TicketDTO;
import ticket.management.backend.dto.TicketStatusDTO;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Priority;
import ticket.management.backend.entity.enums.Status;
import ticket.management.backend.repository.TicketRepository;
import ticket.management.backend.util.exceptions.InvalidInputException;
import ticket.management.backend.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImpTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImp ticketService;

    private Ticket ticket;

    @BeforeEach
    void setUp() {

        ticket = new Ticket();

        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setStatus(Status.NEW);
        ticket.setPriority(Priority.HIGH);
    }

    @Test
    void createTicket_shouldCreateSuccessfully() {

        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(ticket);

        Ticket result = ticketService.createTicket(ticket);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void createTicket_shouldThrowException() {

        when(ticketRepository.save(any(Ticket.class)))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> ticketService.createTicket(ticket));
    }

    @Test
    void updateTicketInfo_shouldUpdateSuccessfully() {

        TicketDTO dto = new TicketDTO();
        dto.setTitle("Updated");

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(any()))
                .thenReturn(ticket);

        Ticket result =
                ticketService.updateTicketInfo(1L, dto);

        assertNotNull(result);

        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(any());
    }

    @Test
    void updateTicketInfo_shouldThrowNotFound() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.updateTicketInfo(
                        1L,
                        new TicketDTO()));
    }

    @Test
    void updateTicketStatus_shouldUpdateSuccessfully() {

        TicketStatusDTO dto = new TicketStatusDTO();
        dto.setStatus(Status.CLOSED);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(any()))
                .thenReturn(ticket);

        Ticket result =
                ticketService.updateTicketStatus(1L, dto);

        assertNotNull(result);

        verify(ticketRepository).save(any());
    }

    @Test
    void updateTicketStatus_shouldThrowNotFound() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.updateTicketStatus(
                        1L,
                        new TicketStatusDTO()));
    }

    @Test
    void retrieveTicket_shouldReturnTicket() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        Ticket result =
                ticketService.retrieveTicket(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void retrieveTicket_shouldThrowNotFound() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.retrieveTicket(1L));
    }

    @Test
    void removeTicket_shouldDeleteSuccessfully() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        doNothing()
                .when(ticketRepository)
                .deleteById(1L);

        ticketService.removeTicket(1L);

        verify(ticketRepository)
                .deleteById(1L);
    }

    @Test
    void searchTicket_shouldReturnTicket() {

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        Ticket result =
                ticketService.searchTicket(
                        1L,
                        Status.NEW);

        assertNotNull(result);
    }

    @Test
    void searchTicket_shouldThrowInvalidStatus() {

        assertThrows(
                InvalidInputException.class,
                () -> ticketService.searchTicket(
                        1L,
                        null));
    }
}