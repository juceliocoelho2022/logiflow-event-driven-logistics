package br.com.logiflow.estoque.domain;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
interface ReservaJpaRepository extends JpaRepository<Reserva,UUID>{}
interface EventoProcessadoRepository extends JpaRepository<EventoProcessado,UUID>{}
