package com.senai.TCC.infraestructure.security;

import com.senai.TCC.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsuarioDetailService usuarioDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(
                AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/auth/refresh","/swagger-ui/**","/v3/api-docs/**").permitAll()

                        // Permições de requisições GET

                        .requestMatchers(HttpMethod.GET, "/carro").hasAnyRole(
                                Role.CLIENTE.name(),
                                Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/estacionamento", "/cliente").permitAll()
                        .requestMatchers(HttpMethod.GET, "/gerente", "/valor").hasAnyRole(
                                Role.DONO.name(),
                                Role.ADMIN.name())

                        // Permições de requisições POST

                        .requestMatchers(HttpMethod.POST, "/carro").hasAnyRole(
                                Role.CLIENTE.name(),
                                Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/cliente", "/dono").permitAll()
                        .requestMatchers(HttpMethod.POST, "/estacionamento/**", "/gerente", "/valor").hasAnyRole(
                                Role.DONO.name(),
                                Role.ADMIN.name())

                        // Permições de requisições PUT

                        .requestMatchers(HttpMethod.PUT, "/carro/**").hasAnyRole(
                                Role.CLIENTE.name(),
                                Role.ADMIN.name())

                        .requestMatchers(HttpMethod.PUT, "/valor/**").hasAnyRole(
                                Role.DONO.name(),
                                Role.ADMIN.name())

                        .requestMatchers(HttpMethod.PUT, "/estacionamento/**", "/reservas/**").hasAnyRole(
                                Role.DONO.name(),
                                Role.GERENTE.name(),
                                Role.ADMIN.name())

                        .anyRequest().hasRole(Role.ADMIN.name())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(usuarioDetailService);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
