package com.sixthsense.hexastay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Windows 경로 주의: file:/// 또는 file:C:/... 이렇게 해줘야 함
        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/profile/");

        registry.addResourceHandler("/store/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/store/");


        registry.addResourceHandler("/company/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/company/");

        registry.addResourceHandler("/roommenu/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/roommenu/");

        registry.addResourceHandler("/hotelroom/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/hotelroom/");




    }
}