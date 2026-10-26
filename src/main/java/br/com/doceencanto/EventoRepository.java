package br.com.doceencanto;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Evento e where e.id = :id")
    Optional<Evento> bloquear(@Param("id") Long id);
}