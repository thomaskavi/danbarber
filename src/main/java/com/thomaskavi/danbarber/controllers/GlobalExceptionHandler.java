package com.thomaskavi.danbarber.controllers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erros de validação (@Valid) — devolve campo + mensagem de cada erro
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );

        Map<String, Object> body = corpoBase(HttpStatus.BAD_REQUEST, "Erro de validação");
        body.put("campos", erros);

        return ResponseEntity.badRequest().body(body);
    }

    // Entidade não encontrada (ex: id de barbeiro/serviço inexistente)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(EntityNotFoundException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Qualquer outro erro não previsto
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        Map<String, Object> body = corpoBase(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado");
        return ResponseEntity.internalServerError().body(body);
    }

    private Map<String, Object> corpoBase(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", mensagem);
        return body;
    }
}
