package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.NON_DOWNLOADED_LINKS_FILE_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.EXECUTOR_SERVICE_BEAN;

@Profile(PROFILE)
@Configuration
@Slf4j
public class LocalConfig {

    @Value("${spring.application.non.downloaded.links.filename}")
    private String nonDownloadedLinksFilename;

    @Value("${spring.application.thread.pool.size}")
    private int threadPoolSize;

    @Bean(name = NON_DOWNLOADED_LINKS_FILE_BEAN)
    public File nonDownloadedLinksFile() {
        return new File(nonDownloadedLinksFilename);
    }

    @Bean(name = EXECUTOR_SERVICE_BEAN)
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

}
