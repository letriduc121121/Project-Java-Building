package com.devon.building.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.modelmapper.ModelMapper;

@Configuration
public class WebConfiguration implements WebMvcConfigurer{
 
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public org.springframework.web.servlet.LocaleResolver localeResolver() {
        org.springframework.web.servlet.i18n.CookieLocaleResolver localeResolver = new org.springframework.web.servlet.i18n.CookieLocaleResolver();
        localeResolver.setDefaultLocale(new java.util.Locale("vi"));
        return localeResolver;
    }
}
