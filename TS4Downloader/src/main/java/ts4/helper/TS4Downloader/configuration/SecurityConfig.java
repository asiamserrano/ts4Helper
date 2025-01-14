package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.SECURITY_CONFIG_POLICY;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.SECURITY_CONFIG_ANT_MATCHERS;

import org.springframework.security.config.http.SessionCreationPolicy;


@EnableMethodSecurity
@EnableWebSecurity
@Profile(PROFILE)
@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        try {
            return http.headers(headers -> headers
                    .contentSecurityPolicy(policy -> policy.policyDirectives(SECURITY_CONFIG_POLICY))
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                    .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(requests -> requests.requestMatchers(SECURITY_CONFIG_ANT_MATCHERS)
                            .permitAll().anyRequest().authenticated())
                    .build();
        } catch (Exception ex) {
            log.error("Failed to create security filter chain bean: {}", ex.getMessage());
            return null;
        }
    }

}
