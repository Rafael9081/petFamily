package br.com.petfamily.canilapi.infra.exception;

import br.com.petfamily.canilapi.controller.dto.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

// Esta anotação intercepta exceções de todos os @RestControllers da aplicação.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Este mtodo será chamado sempre que um EntityNotFoundException for lançado.
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException ex) {
        var error = new ErrorResponseDTO(ex.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Este método será chamado sempre que um IllegalArgumentException for lançado.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        var error = new ErrorResponseDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Pega a primeira mensagem de erro de validação para ser mais direto
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro de validação nos dados enviados.");

        var error = new ErrorResponseDTO(message, HttpStatus.BAD_REQUEST.value(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Este método captura qualquer outra exceção não tratada (ex: NullPointerException).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Loga o erro completo no console do servidor para que você possa investigar.
        log.error("Ocorreu um erro inesperado no servidor: ", ex);

        // Retorna uma mensagem genérica e segura para o cliente.
        var error = new ErrorResponseDTO("Ocorreu um erro interno no servidor.", HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}