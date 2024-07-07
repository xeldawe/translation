package hu.davidder.translations.core.config;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import hu.davidder.translations.core.enums.Headers;
import hu.davidder.translations.ratelimit.filter.RateLimitFilter;

@Configuration
@PropertySource("classpath:security.properties")
public class SecurityConfig {

	@Value("${security.private.endpoints:#{new String[0]}}")
	private String[] privateEndpoints;
	
	@Lazy
	@Autowired
	@Order(1)
	private RateLimitFilter rateLimitFilter;
	
	@Bean
	@Lazy
	public SecurityFilterChain configure(final HttpSecurity http) throws Exception  {
		http
			.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
			.csrf(csrfConfigurer -> csrfConfigurer.disable())
			.sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> {
				authorize
//				.anyRequest().permitAll();
						.requestMatchers(HttpMethod.GET,privateEndpoints).authenticated()
//						.requestMatchers(HttpMethod.POST,publicEndpoints).permitAll()
//						.requestMatchers(HttpMethod.PUT,publicEndpoints).permitAll()
//						.requestMatchers(HttpMethod.PATCH,publicEndpoints).permitAll()
					.anyRequest().permitAll();
				});
		   http.addFilterBefore(
				   rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	@Lazy
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
