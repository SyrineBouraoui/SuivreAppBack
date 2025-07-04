package com.example.suivreapp.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.suivreapp.service.CustomUserDetailsService;
import com.example.suivreapp.service.UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;  // This will inject the filter with its dependencies

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService; // Use the CustomUserDetailsService
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService())
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCrypt password encoder
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors().and() 
        .csrf(csrf -> csrf.disable())
            .authorizeRequests()
                .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/reset-password","/api/auth/forgot-password", "/api/users", "/api/users/**",  "/api/users/{id}" ,"/api/doctors/**", "/api/doctors/{id}" , "/api/patients/{patientId}/impersonate", "/api/patients/**", "/api/alerts/send-sms", "/api/messages/**", "/api/sensor/data", "/api/heartrate/data/**","/api/chatbot/ask", "/api/email/contact/send-email" , "/api/heartrate/data" , "/api/sensor/data/**").permitAll()
                .requestMatchers("/api/**").authenticated() 
                .requestMatchers("/api/messages/send").authenticated() // Make sure the endpoint is accessible

                .requestMatchers("/doctors/**").hasRole("DOCTOR")  // Adjust role if needed
                .requestMatchers("/api/patients/*/impersonate").hasRole("DOCTOR") // Only doctors can impersonate

                .requestMatchers("/api/patients/doctor/**").hasRole("DOCTOR")  // Only doctors can access /auth/doctors
                .requestMatchers("/auth/patients").hasRole("PATIENT")  // Only patients can access /auth/patients
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Use autowired jwtAuthFilter
        return http.build();
    }

    @Bean
    public org.springframework.web.servlet.config.annotation.WebMvcConfigurer corsConfigurer() {
        return new org.springframework.web.servlet.config.annotation.WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200");
            }
        };
    }
}
