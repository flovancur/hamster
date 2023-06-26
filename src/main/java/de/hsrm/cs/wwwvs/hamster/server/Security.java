package de.hsrm.cs.wwwvs.hamster.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Security {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.build();
    }
}
