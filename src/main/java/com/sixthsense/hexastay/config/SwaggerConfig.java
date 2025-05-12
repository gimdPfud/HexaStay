package com.sixthsense.hexastay.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**************************************************
 * 클래스명 : SwaggerConfig
 * 기능   : SpringDoc OpenAPI (Swagger UI) 설정을 담당하는 클래스입니다.
 * API 문서를 자동으로 생성하고 웹 UI를 통해 확인할 수 있도록 하며,
 * API 그룹 정의 및 문서의 기본 정보(제목, 버전, 설명 등)를 커스터마이징합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-29
 * 수정일 : 2025-05-09
 * 주요 설정/Bean : publicApi (GroupedOpenApi), customOpenAPI (OpenAPI)
 **************************************************/

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springdoc-public")
                .pathsToMatch("/**")
                .build();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Everywhere API")
                        .version("v1")
                        .description("읽는곳곳 API 명세서"));
    }
}
