package com.demo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authz)->authz.regexMatchers("")

        )

        ;

        return null;
//        http
//                .authorizeHttpRequests((authz) -> authz
//                        .antMatchers("/api/admin/**").hasRole("ADMIN")
//                        .antMatchers("/api/user/**").hasRole("USER")
//                        .anyRequest().authenticated()
//                );
//        return http.build();
    }

}
