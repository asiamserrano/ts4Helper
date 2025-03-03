package org.projects.ts4.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@SpringBootApplication
@Profile(PROFILE)
public class TS4ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TS4ConsumerApplication.class, args);
    }

}
