package br.com.petfamily.canilapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    // 1. Crie uma instância estática e final do logger para esta classe.
    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        // 2. Use o logger. A sintaxe {} é mais eficiente que a concatenação de strings.
        String dbUrl = System.getenv("DATABASE_URL");
        log.debug("DATABASE_URL lida do ambiente: {}", (dbUrl != null ? dbUrl : "null"));

        SpringApplication.run(BackendApplication.class, args);

        log.info("Aplicação CanilAPI iniciada com sucesso!");
    }
}