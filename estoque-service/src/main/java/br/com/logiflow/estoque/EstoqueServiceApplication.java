package br.com.logiflow.estoque;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EstoqueServiceApplication {

 public static void main(String[] args) {
  SpringApplication.run(EstoqueServiceApplication.class, args);
 }

 @Bean
 NewTopic pedidoTopic() {
  return TopicBuilder
          .name("logiflow.pedidos.criados.v1")
          .partitions(3)
          .replicas(1)
          .build();
 }

 @Bean
 NewTopic pedidoDlt() {
  return TopicBuilder
          .name("logiflow.pedidos.criados.v1.DLT")
          .partitions(3)
          .replicas(1)
          .build();
 }

 @Bean
 NewTopic reservaConfirmadaTopic() {
  return TopicBuilder
          .name("logiflow.reservas.confirmadas.v1")
          .partitions(3)
          .replicas(1)
          .build();
 }
}