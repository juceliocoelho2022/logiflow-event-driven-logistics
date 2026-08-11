package br.com.logiflow.estoque.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvento, UUID> {

    List<OutboxEvento>
    findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc();
}