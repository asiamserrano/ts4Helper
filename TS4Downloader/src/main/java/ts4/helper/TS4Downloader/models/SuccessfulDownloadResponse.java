package ts4.helper.TS4Downloader.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulDownloadResponse {

    @JsonProperty("source")
    private String source;

    @JsonProperty("download")
    private String download;

    @JsonProperty("destination")
    private String destination;

}
