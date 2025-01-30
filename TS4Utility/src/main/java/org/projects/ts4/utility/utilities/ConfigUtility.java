package org.projects.ts4.utility.utilities;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;

import static org.projects.ts4.utility.constants.ConfigConstants.SCHEMA_REGISTRY_URL_HEADER;
import static org.projects.ts4.utility.constants.ConfigConstants.SCHEMA_REGISTRY_URL_VALUE;
import static org.projects.ts4.utility.constants.ConfigConstants.AUTO_OFFSET_RESET_VALUE;
import static org.projects.ts4.utility.constants.ConfigConstants.SECURITY_CONFIG_POLICY;
import static org.projects.ts4.utility.constants.ConfigConstants.SECURITY_CONFIG_ANT_MATCHERS;

@Slf4j
public abstract class ConfigUtility {

    public static ConcurrentKafkaListenerContainerFactory<String, ?> concurrentKafkaListenerContainerFactory
            (String bootstrapServers, String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, ?> concurrentKafkaListenerContainerFactory;
        concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(new HashMap<>() {{
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            put(SCHEMA_REGISTRY_URL_HEADER, SCHEMA_REGISTRY_URL_VALUE);
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_VALUE);
            put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        }}));
        return concurrentKafkaListenerContainerFactory;
    }

    public static SecurityFilterChain securityFilterChain(final HttpSecurity http) {
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
