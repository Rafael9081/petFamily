package br.com.petfamily.canilapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Injeta a lista de origens do arquivo de propriedades.
    // O valor padrão ": " garante que não quebre se a propriedade não existir.
    @Value("${cors.allowed.origins:}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Verifica se a propriedade foi configurada para evitar erros.
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            // 1. Separa a string de origens em um array.
            // 2. O .map(String::trim) é uma melhoria crucial: ele remove quaisquer espaços
            //    em branco antes ou depois das vírgulas (ex: "url1, url2").
            String[] originPatterns = Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            registry.addMapping("/**") // Aplica a configuração a todos os endpoints da API
                    .allowedOriginPatterns(originPatterns) // Usa o método correto para wildcards
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                    .allowCredentials(true); // Permite o envio de cookies (importante para sessões)
        }
    }
}