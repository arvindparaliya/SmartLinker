package com.smartlinker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //for upload contact image
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //for uploaded profile photos
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")  
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());            
    }
}
