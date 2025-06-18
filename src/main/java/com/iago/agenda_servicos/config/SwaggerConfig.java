package com.iago.agenda_servicos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger para documentação da API.
 * Define as informações da API e agrupa os endpoints por perfil de usuário.
 */
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI apiInfo() {
                return new OpenAPI()
                        .info(new Info()
                                .title("AgendaServiços API - Gestão de Barbearias & Salões")
                                .description("Sistema completo de agendamento, gestão de profissionais, comissões e relatórios.")
                                .version("1.0.0"))
                        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                        .components(new Components().addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Informe o token JWT no formato: Bearer <token>")
                        ));
        }

        @Bean
        public GroupedOpenApi authApi() {
                return GroupedOpenApi.builder()
                        .group("1. Autenticação & Registro")
                        .pathsToMatch("/auth/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi clienteApi() {
                return GroupedOpenApi.builder()
                        .group("2. Cliente")
                        .pathsToMatch("/api/cliente/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi profissionalApi() {
                return GroupedOpenApi.builder()
                        .group("3. Profissional")
                        .pathsToMatch("/api/profissional/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                        .group("4. Admin (Owner & Recepcionista)")
                        .pathsToMatch("/api/admin/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi rootApi() {
                return GroupedOpenApi.builder()
                        .group("5. Super Admin")
                        .pathsToMatch("/api/root/**")
                        .build();
        }
}
