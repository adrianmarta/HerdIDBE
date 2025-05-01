package com.example.farmerapp;

		import com.fasterxml.jackson.databind.ObjectMapper;
		import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
		import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FarmerappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmerappApplication.class, args);
	}
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> builder.modules(new JavaTimeModule());
	}
}
