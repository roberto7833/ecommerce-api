package com.matutadesign.ecommerce_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        String caminhoMidias;

        if (os.contains("win")) {
            caminhoMidias = "file:///C:/Users/rober/IdeaProjects/midias/imagens-boutique/";
        } else {
            caminhoMidias = "file:/tmp/imagens-boutique/";
        }

        // Mapeia todas as requisições que começam com /midias/** para buscar no disco físico
        registry.addResourceHandler("/midias/**")
                .addResourceLocations(caminhoMidias);
    }
}