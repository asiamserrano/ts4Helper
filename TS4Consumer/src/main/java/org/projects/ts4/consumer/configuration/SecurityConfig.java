package org.projects.ts4.consumer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.utility.utilities.ConfigUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@EnableMethodSecurity
@EnableWebSecurity
@Profile(PROFILE)
@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        return ConfigUtility.securityFilterChain(http);
    }

}
