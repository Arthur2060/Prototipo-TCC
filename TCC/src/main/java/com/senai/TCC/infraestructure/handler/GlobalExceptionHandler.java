package com.senai.TCC.infraestructure.handler;

import com.senai.TCC.model.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdNaoCadastrado.class)
    public ResponseEntity<?> handleIdNaoEncontrado(IdNaoCadastrado ex) {
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

    @ExceptionHandler(AvaliacaoInvalida.class)
    public ResponseEntity<?> handeMultiplasAvaliacoesIguaisException(AvaliacaoInvalida ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TipoDeUsuarioInvalido.class)
    public ResponseEntity<?> handleTipoDeUsuarioInvalidoException(TipoDeUsuarioInvalido ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(ComentarioMuitoLongo.class)
    public ResponseEntity<?> handleComentarioMuitoLongoException(ComentarioMuitoLongo ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(TempoLimiteDeAvaliacaoExpedido.class)
    public ResponseEntity<?> handleTempoLimiteDeAvaliacaoExpedido(TempoLimiteDeAvaliacaoExpedido ex) {
        return ResponseEntity.status(400).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<?> handleCredencialInvalidasException(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(401).body(Map.of("erro",ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("erro", "Request method '" + ex.getMethod() + "' is not supported"));
    }
}
