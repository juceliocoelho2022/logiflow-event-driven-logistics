package br.com.logiflow.pedido.api;
import br.com.logiflow.pedido.application.PedidoApplicationService; import br.com.logiflow.pedido.domain.Pedido; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/pedidos") @CrossOrigin(origins="*")
public class PedidoController {
 private final PedidoApplicationService service; public PedidoController(PedidoApplicationService service){this.service=service;}
 @PostMapping public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest req){var p=service.criar(req.clienteId(),req.itens().stream().map(i->new PedidoApplicationService.NovoItem(i.produtoId(),i.quantidade(),i.precoUnitario())).toList());return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(p));}
 @GetMapping public List<PedidoResponse> listar(){return service.listar().stream().map(PedidoResponse::de).toList();}
 public record CriarPedidoRequest(@NotNull UUID clienteId,@NotEmpty List<@Valid ItemRequest> itens){}
 public record ItemRequest(@NotBlank String produtoId,@Min(1) int quantidade,@DecimalMin("0.01") BigDecimal precoUnitario){}
 public record PedidoResponse(UUID id,UUID clienteId,BigDecimal valorTotal,String status,java.time.Instant criadoEm){static PedidoResponse de(Pedido p){return new PedidoResponse(p.getId(),p.getClienteId(),p.getValorTotal(),p.getStatus().name(),p.getCriadoEm());}}
}
