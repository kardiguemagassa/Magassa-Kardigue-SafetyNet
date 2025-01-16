package com.openclassrooms.safetynet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Util pour la confiration de ExceptionHandler
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Désactive la correspondance des suffixes (recommandé pour les versions modernes de Spring)
        configurer.setUseSuffixPatternMatch(false);
    }

    /*
    // Supprimez cette méthode si vous voulez que Spring gère automatiquement les ressources statiques
    // Si nécessaire, vous pouvez personnaliser les gestionnaires de ressources ici.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Désactiver les mappages automatiques des ressources
        registry.setOrder(-1);
    }

     */
}
