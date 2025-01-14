package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.NON_DOWNLOADED_LINKS_FILE_BEAN;

@Profile(PROFILE)
@Configuration
@Slf4j
public class LocalConfig {

    @Value("${spring.application.non.downloaded.links.filename}")
    private String nonDownloadedLinksFilename;

    @Bean(name = NON_DOWNLOADED_LINKS_FILE_BEAN)
    public File nonDownloadedLinksFile() {
        return new File(nonDownloadedLinksFilename);
    }

}
