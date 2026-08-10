package br.com.logiflow.pedido.api;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler({IllegalArgumentException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) Map<String,Object> negocio(Exception e){return Map.of("timestamp",Instant.now(),"status",400,"erro",e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) Map<String,Object> validacao(MethodArgumentNotValidException e){var erros=e.getBindingResult().getFieldErrors().stream().collect(java.util.stream.Collectors.toMap(x->x.getField(),x->Optional.ofNullable(x.getDefaultMessage()).orElse("inválido"),(a,b)->a));return Map.of("timestamp",Instant.now(),"status",400,"erros",erros);}
}
