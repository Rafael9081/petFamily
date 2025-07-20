package br.com.petfamily.canilapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // By adding ":", we provide an empty string as a default value.
    // If 'cors.allowed.origins' is not found in the properties file,
    // 'allowedOrigins' will be an empty string, preventing the crash.
    @Value("${cors.allowed.origins:}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // This check is now even more important. If allowedOrigins is blank,
        // no CORS configuration will be applied, which is perfect for tests.
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                    .allowCredentials(true);
            }
    }

}