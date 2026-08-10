package br.com.logiflow.pedido.outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OutboxRepository extends JpaRepository<OutboxEvento, UUID> { List<OutboxEvento> findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc(); }
