package com.learning.emsmybatisliquibase.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Employee Management System API",
                description = "EMS API",
                summary = "API functionalities for EMS",
                termsOfService = "T&C",
                contact = @Contact(
                        name = "Venkata Krishna Repalle",
                        email = "rvkrishna13052001@gmail.com"
                ),
                license = @License(
                        name = "abcd"
                ),
                version = "v3"

        ),
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:8082"
                ),
                @Server(
                        description = "Test",
                        url = "http://localhost:8082"
                ),
                @Server(
                        description = "Production",
                        url = "https://emssopra.azurewebsites.net"

                )
        },
        security = @SecurityRequirement(
                name = "JWT"
        )
)

@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

}