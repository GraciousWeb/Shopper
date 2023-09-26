package com.example.podb.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityFilterChainConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilterConfiguration filterConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.POST, "/api/v1/admins/createAdmin").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/admins/updateProduct/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/admins/deleteProduct/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/admins/addProduct").hasAuthority("ADMIN")

                )
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/v1/users/**", "/error").permitAll()
                                .requestMatchers("/api/v1/users/verify/**").permitAll()
                                .requestMatchers("/users/**").permitAll()
                )

                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(GET, "/api/v1/product/viewProduct/*").permitAll()
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(GET, "/api/v1/product/getAllProducts").permitAll()
                )

                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(filterConfiguration, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}

