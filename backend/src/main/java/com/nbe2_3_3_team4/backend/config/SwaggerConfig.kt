package com.nbe2_3_3_team4.backend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SwaggerConfig : WebMvcConfigurer {
    @Bean
    fun ttukttakParkingAPI(): OpenAPI {
        val jwtScheme = "JWT"
        val securityRequirement = SecurityRequirement().addList(jwtScheme)

        val components =
                Components()
                        .addSecuritySchemes(
                                jwtScheme,
                                SecurityScheme()
                                        .name(jwtScheme)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT"))
        return OpenAPI()
                .info(getinfo())
                .addSecurityItem(securityRequirement)
                .components(components)
    }

    private fun getinfo(): Info {
        return Info()
                .title("뚝딱파킹 API")
                .description("NBE2_3_2_TEAM4_뚝딱파킹 API 명세")
                .version("0.0.1")
    }
}