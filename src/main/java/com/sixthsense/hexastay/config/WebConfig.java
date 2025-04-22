package com.sixthsense.hexastay.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final String LANG_PARAM_NM = "lang"; // 언어를 나타내는 파라미터 이름 (URL 파라미터로 사용)

    // 기본 언어 설정을 위한 LocaleResolver Bean 정의
    @Bean
    public LocaleResolver localeResolver() {
        // CookieLocaleResolver 객체를 생성하여 쿠키에 언어를 저장하도록 설정
        CookieLocaleResolver localeResolver = new CookieLocaleResolver(LANG_PARAM_NM);

        // 기본 로케일을 한국어로 설정
        localeResolver.setDefaultLocale(Locale.KOREAN);

        // 쿠키가 HTTP 요청에만 접근 가능하도록 설정 (보안 강화)
        localeResolver.setCookieHttpOnly(true);

        return localeResolver; // 설정한 localeResolver 객체 반환
    }

    // 브라우저에서 전달된 lang 파라미터를 통해 언어를 변경할 수 있도록 하는 인터셉터 정의
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        // LocaleChangeInterceptor 객체를 생성
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();

        // 언어 변경을 위한 파라미터 이름을 설정
        interceptor.setParamName(LANG_PARAM_NM);

        // 유효하지 않은 언어 파라미터는 무시하도록 설정
        interceptor.setIgnoreInvalidLocale(true);

        return interceptor; // 설정한 interceptor 객체 반환
    }



    // 인터셉터를 등록하고, 특정 URL 패턴에 대해 적용될지 설정
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // localeChangeInterceptor를 등록하여 인터셉터가 작동하도록 함
        registry.addInterceptor(localeChangeInterceptor())
                // css, js, 이미지, 폰트 파일 경로는 인터셉터 적용에서 제외
                .excludePathPatterns("/css/**", "/js/**", "images/**", "/fonts/**")
                // 모든 다른 경로에 대해서 인터셉터 적용
                .addPathPatterns("/**"); // 모든 맵핑명에 적용
    }

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

        registry.addResourceHandler("/qr/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/qr/");

        registry.addResourceHandler("/erd/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/erd/");

        // 다국어 설정








    }
}