package com.senai.TCC.infraestructure.handler;

import com.senai.TCC.model.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdNaoCadastradoException.class)
    public ResponseEntity<?> handleIdNaoEncontrado(IdNaoCadastradoException ex) {
        return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> erros.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(400).body(erros);
    }

    @ExceptionHandler(AvaliacaoInvalidaException.class)
    public ResponseEntity<?> handeMultiplasAvaliacoesIguaisException(AvaliacaoInvalidaException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TipoDeUsuarioInvalidoException.class)
    public ResponseEntity<?> handleTipoDeUsuarioInvalidoException(TipoDeUsuarioInvalidoException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(ComentarioMuitoLongoException.class)
    public ResponseEntity<?> handleComentarioMuitoLongoException(ComentarioMuitoLongoException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TempoLimiteDeAvaliacaoExpedidoException.class)
    public ResponseEntity<?> handleTempoLimiteDeAvaliacaoExpedido(TempoLimiteDeAvaliacaoExpedidoException ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }
}
