package hu.davidder.translation.api.core.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import hu.davidder.translation.api.core.enums.Headers;

@Configuration
@PropertySource("classpath:security.properties")
public class SecurityConfig {

	@Value("${security.public.endpoints:#{new String[0]}}")
	private String[] publicEndpoints;
	
	@Bean
	public SecurityFilterChain configure(final HttpSecurity http) throws Exception  {
		http
			.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
			.csrf(csrfConfigurer -> csrfConfigurer.disable())
			.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> {
				authorize
						.requestMatchers(HttpMethod.GET,publicEndpoints).permitAll()
						.requestMatchers(HttpMethod.POST,publicEndpoints).permitAll()
//						.requestMatchers(HttpMethod.PUT,publicEndpoints).permitAll()
//						.requestMatchers(HttpMethod.PATCH,publicEndpoints).permitAll()
					.anyRequest().authenticated();
				});
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	private CorsConfigurationSource corsConfigurationSource() {
		final var corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(List.of("*"));
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		corsConfiguration.setAllowedHeaders(List.of("Authorization", "Origin", "Content-Type", "Accept"));
		corsConfiguration.setExposedHeaders(Headers.asList());

		final var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return corsConfigurationSource;
	}

	
}
