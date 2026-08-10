package br.com.logiflow.estoque.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="eventos_processados") public class EventoProcessado {@Id @Column(name="event_id") private UUID eventId;@Column(name="processado_em",nullable=false)private Instant processadoEm;protected EventoProcessado(){}public EventoProcessado(UUID id){eventId=id;processadoEm=Instant.now();}}
