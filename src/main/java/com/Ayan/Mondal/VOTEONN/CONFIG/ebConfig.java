package com.Ayan.Mondal.VOTEONN.CONFIG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class ebConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendURL;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendURL, 
                                "http://127.0.0.1:5500", "http://localhost:5500",
                                "http://127.0.0.1:5503", "http://localhost:5503",
                                "https://votonn.netlify.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        registry.addResourceHandler("/**")
                .addResourceLocations("file:../VOTONN-UI/");
    }
}