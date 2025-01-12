package ts4.helper.TS4Downloader.configuration;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import ts4.helper.TS4Downloader.downloaders.CurseForgeDownloader;
import ts4.helper.TS4Downloader.downloaders.PatreonDownloader;
import ts4.helper.TS4Downloader.downloaders.SimsFindsDownloader;

import static ts4.helper.TS4Downloader.constants.ConfigConstants.PROFILE;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.REST_TEMPLATE_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.CURSE_FORGE_DOWNLOADER_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.PATREON_DOWNLOADER_BEAN;
import static ts4.helper.TS4Downloader.constants.ConfigConstants.SIMS_FINDS_DOWNLOADER_BEAN;

@Profile(PROFILE)
@Configuration
@Slf4j
public class LocalConfig {

    @Bean(name = REST_TEMPLATE_BEAN)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = CURSE_FORGE_DOWNLOADER_BEAN)
    public CurseForgeDownloader curseForgeDownloader(final OkHttpClient client) {
        return new CurseForgeDownloader(client);
    }

    @Bean(name = PATREON_DOWNLOADER_BEAN)
    public PatreonDownloader patreonDownloader(final OkHttpClient client) {
        return new PatreonDownloader(client);
    }

    @Bean(name = SIMS_FINDS_DOWNLOADER_BEAN)
    public SimsFindsDownloader simsFindsDownloader(final OkHttpClient client) {
        return new SimsFindsDownloader(client);
    }

}
