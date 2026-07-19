package com.thomaskavi.danbarber.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Erros de validação (@Valid) — devolve campo + mensagem de cada erro
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));

        String primeiraMensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(erro -> erro.getDefaultMessage())
                .orElse("Erro de validação");

        Map<String, Object> body = corpoBase(HttpStatus.BAD_REQUEST, primeiraMensagem);
        body.put("campos", erros);

        return ResponseEntity.badRequest().body(body);
    }

    // Entidade não encontrada (ex: id de barbeiro/serviço inexistente)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoBase(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // Acesso negado por falta de permissão (@PreAuthorize) — ex: barbeiro tentando
    // acessar rota exclusiva do dono
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AccessDeniedException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.FORBIDDEN,
                "Você não tem permissão para acessar este recurso");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Login ou senha incorretos
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas(BadCredentialsException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.UNAUTHORIZED, "Login ou senha inválidos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // Corpo da requisição malformado — ex: valor de enum inexistente
    // (como mandar "formaPagamento": "CREDITO" em vez de "CARTAO_CREDITO")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleCorpoInvalido(HttpMessageNotReadableException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique se todos os campos e valores de enum estão corretos");
        return ResponseEntity.badRequest().body(body);
    }

    // Parâmetro de URL com tipo/formato errado — ex: data malformada,
    // ou texto onde era esperado um número
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.BAD_REQUEST,
                "Parâmetro '" + ex.getName() + "' possui um valor ou formato inválido: " + ex.getValue());
        return ResponseEntity.badRequest().body(body);
    }

    // Parâmetro obrigatório (@RequestParam) não informado — ex: esquecer "inicio" e "fim"
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroFaltando(MissingServletRequestParameterException ex) {
        Map<String, Object> body = corpoBase(HttpStatus.BAD_REQUEST,
                "Parâmetro obrigatório não informado: " + ex.getParameterName());
        return ResponseEntity.badRequest().body(body);
    }

    // Erros de regra de negócio lançados manualmente (ex: "informe o barbeiroId")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(corpoBase(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // Qualquer outro erro não previsto — esse continua sendo o "pega-tudo" de última instância
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        // Essencial: sem isso, a exceção real nunca aparece no console
        logger.error("Erro inesperado não tratado especificamente", ex);
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
