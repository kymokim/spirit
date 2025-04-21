package com.kymokim.spirit.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("Spirit")
                .description("API");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER).name("x-auth-token");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("x-auth-token"); // "x-auth-token"를 사용합니다.

        Components components = new Components().addSecuritySchemes("x-auth-token", securityScheme);


        return new OpenAPI()
                .addServersItem(new Server().url("https://dev.team-spirit.click").description("Dev Server"))
                .addServersItem(new Server().url("https://team-spirit.click").description("Prod Server"))
                .addServersItem(new Server().url("/").description("Local host"))
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(info);
    }

    @Bean
    public GroupedOpenApi all(){
        String[] pathsToMatch = {"/api/**"};
        return GroupedOpenApi.builder()
                .group("All")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi auth(){
        String[] pathsToMatch = {"/api/auth/**"};
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi archive(){
        String[] pathsToMatch = {"/api/archive/**"};
        return GroupedOpenApi.builder()
                .group("Archive")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi store(){
        String[] pathsToMatch = {"/api/store/**"};
        return GroupedOpenApi.builder()
                .group("Store")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi menu(){
        String[] pathsToMatch = {"/api/menu/**"};
        return GroupedOpenApi.builder()
                .group("Menu")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi drink(){
        String[] pathsToMatch = {"/api/drink/**"};
        return GroupedOpenApi.builder()
                .group("Drink")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi review(){
        String[] pathsToMatch = {"/api/review/**"};
        return GroupedOpenApi.builder()
                .group("Review")
                .pathsToMatch(pathsToMatch)
                .build();
    }
}