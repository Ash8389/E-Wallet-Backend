package com.apigateway.API.Gateway.jwt.filter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");          // React dev server
        config.addAllowedOrigin("http://51.21.120.241");           // If you host frontend on same server
        config.addAllowedMethod("*");                               // GET, POST, PUT, DELETE, OPTIONS
        config.addAllowedHeader("*");                               // Authorization, Content-Type, etc.
        config.setAllowCredentials(true);                           // For JWT cookies if needed


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
