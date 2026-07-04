package ticket.management.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ticket.management.backend.entity.Ticket;
import ticket.management.backend.entity.enums.Status;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {


    // Search by Ticket ID and Status
    @Query("SELECT t FROM Ticket t WHERE (t.id = :ticketId) AND (t.status = :status)")
    Optional<Ticket> findByTicketIdAndStatus(@Param("ticketId") Long ticketId, @Param("status") Status status);
}
