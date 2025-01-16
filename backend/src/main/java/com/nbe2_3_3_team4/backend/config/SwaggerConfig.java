package com.nbe2_3_3_team4.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI ttukttakParkingAPI() {
		String jwtScheme = "JWT";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);

		Components components =
			new Components()
				.addSecuritySchemes(
					jwtScheme,
					new SecurityScheme()
						.name(jwtScheme)
						.type(SecurityScheme.Type.HTTP)
						.scheme("Bearer")
						.bearerFormat("JWT"));
		return new OpenAPI()
			.info(getinfo())
			.addSecurityItem(securityRequirement)
			.components(components);
	}

	private Info getinfo() {
		return new Info()
			.title("뚝딱파킹 API")
			.description("NBE2_3_2_TEAM4_뚝딱파킹 API 명세")
			.version("0.0.1");
	}

}