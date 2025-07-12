package br.com.petfamily.canilapi.infra.exception.cors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
    private final String[] allowedOrigins;

    public CorsConfig(@Value("${cors.allowed.origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
        log.info("CORS configurado com origens permitidas: {}", Arrays.toString(this.allowedOrigins));
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // MUDANÇA PRINCIPAL: Use allowedOriginPatterns em vez de allowedOrigins.
                // Este método é mais flexível e projetado para funcionar corretamente com allowCredentials(true).
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}