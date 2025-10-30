package com.senai.TCC.infraestructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inpark")
                        .summary("Nome em processo...")
                        .description("Este é o protótipo de projeto para TCC da escola e faculdade SENAI \"Anchieta\" Vila Mariana, " +
                                "este projeto tem como objetivo criar um sistema de reserva e gerenciamento de vagas de estacionamento, " +
                                "acreditamos que isso tem uma boa capacidade de ganhar espaço no mercado facilitando o dia-a-dia dos usuarios do" +
                                "sistema, se baseando principalmente em sucesso passados como Uber e Ifood"
                        )
                        .contact(new Contact()
                                .name("Grupo 1 - TCC")
                                .email("")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer authentication",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                ));
    }
}
