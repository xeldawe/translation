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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hu.davidder.translations.ratelimit.filter.RateLimitFilter;

/**
 * The SecurityConfig class provides security configuration for the application,
 * including CORS settings, CSRF protection, session management, and request authorization.
 */
@Configuration
@PropertySource("classpath:security.properties")
public class SecurityConfig {

    @Value("${security.private.endpoints:#{new String[0]}}")
    private String[] privateEndpoints;

    @Lazy
    @Autowired
    @Order(1)
    private RateLimitFilter rateLimitFilter;

    /**
     * Configures the security filter chain.
     * 
     * @param http The HttpSecurity instance.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    @Lazy
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        http
        .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize ->
                authorize.requestMatchers(HttpMethod.GET, privateEndpoints).authenticated()
                         .anyRequest().permitAll()
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Provides a PasswordEncoder bean.
     * 
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
