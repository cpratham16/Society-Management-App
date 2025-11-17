package com.society.management.config;

import com.society.management.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  // ✅ FIXED: Use AbstractHttpConfigurer
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS - NO AUTHENTICATION REQUIRED
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/verify-otp",
                                "/api/auth/admin/initiate",
                                "/api/auth/admin/complete",
                                "/api/auth/resend-otp",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/refresh",
                                "/api/contact",           // ✅ Contact form
                                "/swagger-ui/**",           // Swagger UI static resources
                                "/swagger-ui.html",         // Swagger UI HTML page
                                "/v3/api-docs/**",          // OpenAPI docs
                                "/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/events/**",           // ✅ Homepage events listings
                                "/api/advertisements/**",   // ✅ Homepage services/ads
                                "/api/management/**",       // ✅ Management cards
                                "/api/photos/**"            // ✅ Gallery images
                        ).permitAll()

                        // ADMIN ONLY ENDPOINTS
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ALL OTHER ENDPOINTS REQUIRE AUTHENTICATION
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Allow requests from React frontend (both dev servers)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",      // React dev server
                "http://localhost:5000",      // CRA alternate port
                "http://localhost:5173",      // Vite dev server
                "http://127.0.0.1:3000",      // Alternative localhost
                "http://127.0.0.1:5000",
                "http://127.0.0.1:5173"       // Alternative localhost
        ));

        // ✅ Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // ✅ Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // ✅ Expose Authorization header to frontend
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"  // For pagination if needed
        ));

        // ✅ Set max age for preflight requests (24 hours)
        configuration.setMaxAge(86400L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
