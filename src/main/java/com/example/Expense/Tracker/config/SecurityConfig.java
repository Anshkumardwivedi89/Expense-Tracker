package com.example.Expense.Tracker.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;  // Only for testing

@Configuration
public class SecurityConfig {

    // CUSTOM USER (IN MEMORY)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("ansh")
                .password("12345") // For testing only
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    // SECURITY FILTER CHAIN
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) // Disable CSRF for Postman/API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()  // No auth needed
                        .requestMatchers("/main", "/expense/**").hasRole("USER") // Only USER can access
                        .anyRequest().authenticated()
                )
                .httpBasic(); // Enable Basic Auth

        return http.build();
    }
}
