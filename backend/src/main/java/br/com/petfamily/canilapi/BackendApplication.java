package br.com.petfamily.canilapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        String dbUrl = System.getenv("DATABASE_URL");
        System.out.println("DEBUG: DATABASE_URL recebida: " + (dbUrl != null ? dbUrl : "null ou vazia"));
        SpringApplication.run(BackendApplication.class, args);
    }

}
