package br.com.petfamily.canilapi.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotação é a mágica! Ela diz ao Spring para retornar um status 404
// sempre que esta exceção for lançada.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        // Passa a mensagem de erro para a classe pai (RuntimeException)
        super(message);
    }
}