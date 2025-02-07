package org.projects.ts4.downloader.classes;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.projects.ts4.avro.WebsiteModel;
import org.projects.ts4.utility.classes.ResponseFiles;
import org.projects.ts4.utility.classes.ScriptProcess;
import org.projects.ts4.utility.enums.ExtensionEnum;
import org.projects.ts4.utility.enums.ResponseEnum;
import org.projects.ts4.utility.utilities.FileUtility;
import org.projects.ts4.utility.utilities.URLUtility;

import java.io.File;
import java.net.URL;

@Slf4j
public class WebsiteLogger {

    private final ResponseFiles responseFiles;
    private final WebsiteModel websiteModel;
    private final File destination;
    private final URL source;

    public WebsiteLogger(WebsiteModel websiteModel, ResponseFiles responseFiles) {
        this.responseFiles = responseFiles;
        this.websiteModel = websiteModel;
        this.destination = FileUtility.createFile(websiteModel);
        this.source = URLUtility.createURL(websiteModel);
    }

    public void download(File tarShellScript) {
        try {
            FileUtils.copyURLToFile(source, destination);
            log.info("downloaded url {} to {}", source, destination);
            switch (ExtensionEnum.valueOf(destination)) {
                case ExtensionEnum.ZIP -> unzip();
                case ExtensionEnum.RAR -> untar(tarShellScript);
                default -> { }
            }
        } catch (Exception e) {
            log.error("unable to download url {} to {}", source, destination, e);
            write(ResponseEnum.ERROR);
        }
    }

    private void unzip() {
        log.info("Unzipping file {}", destination);
        try (ZipFile zipFile = new ZipFile(destination)) {
            zipFile.extractAll(destination.getParent());
            if (destination.delete()) {
                log.info("file deleted: {}", destination);
                // TODO: fix this as only files that are zips are logged
                write(ResponseEnum.DOWNLOADED);
            } else {
                log.error("unable to delete file: {}", destination);
                write(ResponseEnum.ERROR);
            }
        } catch (Exception e) {
            log.error("unable to unzip file {}: {}", destination, e.getMessage());
            write(ResponseEnum.ERROR);
        }
    }

    private void untar(File tarShellScript) {
        log.info("Untarring file {}", destination);
        Process process = ScriptProcess.build(tarShellScript, destination).process;
        synchronized (process) {
            try {
                process.waitFor();
            } catch (Exception e) {
                log.error("unable to untar file {}: {}", destination, e.getMessage());
                write(ResponseEnum.ERROR);
            }
        }
    }

    private void write(ResponseEnum responseEnum) {
        responseFiles.write(responseEnum, websiteModel);
    }

}
