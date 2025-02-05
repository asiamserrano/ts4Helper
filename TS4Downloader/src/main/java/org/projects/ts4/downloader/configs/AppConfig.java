package org.projects.ts4.downloader.configs;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.projects.ts4.utility.constants.ConfigConstants.PROFILE;

@Configuration
@Profile(PROFILE)
public class AppConfig {

    @Value("${spring.thread.pool.size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }


}
