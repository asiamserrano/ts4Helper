package ts4.helper.TS4Downloader.utilities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ts4.helper.TS4Downloader.constants.StringConstants.FORWARD_SLASH;
import static ts4.helper.TS4Downloader.enums.ExtensionEnum.ZIP;
import static ts4.helper.TS4Downloader.enums.ExtensionEnum.PACKAGE;

import static ts4.helper.TS4Downloader.constants.StringConstants.EMPTY;

@Slf4j
public class UnzipUtility {

    private static final byte[] buffer = new byte[1024];

    public static void main(String[] args) throws Exception {
        String directory = "/Users/asiaserrano/ChromeDownloads";
//        File file = new File(directory, "Archive.zip");
        File file = new File(directory);
        unzip(file);
    }

    public static void unzip(File file) {
        try {
            Set<File> set = new HashSet<>();
            if (file.isDirectory()) {
                log.info("unzipping directory: {}", file);
                for (File f: Objects.requireNonNull(file.listFiles())) {
                    if (isZipFile(f)) unzip(f);
                }
            } else if (isZipFile(file)) {
                log.info("unzipping zip file: {}", file);
                String destDirName = file.getName().replace(ZIP.extension, EMPTY);
                File destDir = new File(file.getParent(), destDirName);
                String fileZip = file.getAbsolutePath();
                ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
                unzip(zis, destDir, set);
            } else {
                log.error("cannot unzip file: {}", file);
            }
            unzip(set);
        } catch (Exception e) {
            log.error("unzip failed: {}", file, e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isZipFile(File file) {
        return file.getName().contains(ZIP.extension);
    }

    private static void unzip(Set<File> directories) {
        if (!directories.isEmpty()) {
            Set<File> set = new HashSet<>();
            for (File directory: directories) {
                unzip(directory);
            }
            unzip(set);
        }
    }

    private static void unzip(ZipInputStream zis, File destDir, Set<File> set) throws IOException {
        ZipEntry zipEntry;
        while (true) {
            zipEntry = zis.getNextEntry();
            if (zipEntry == null) {
                break;
            } else {
                String[] parts = zipEntry.getName().split(FORWARD_SLASH);
                String zipEntryName = parts[parts.length - 1];
                File newFile = new File(destDir, zipEntryName);
                boolean isZipEntryDirectory = zipEntry.isDirectory();

                if (zipEntryName.contains(PACKAGE.extension)) {
                    parse(zis, newFile, isZipEntryDirectory);
                } else if (zipEntryName.contains(ZIP.extension)) {
                    if (!zipEntryName.startsWith("._")) {
                        set.add(destDir);
                        parse(zis, newFile, isZipEntryDirectory);
                    }
                }
            }
        }
        zis.closeEntry();
        zis.close();
    }

    private static void parse(ZipInputStream zis, File newFile, boolean isZipEntryDirectory) throws IOException {
        if (isZipEntryDirectory) {
            if (!newFile.isDirectory() && !FileUtility.createDirectory(newFile)) {
                throw new IOException("Failed to create directory " + newFile);
            }
        } else {
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !FileUtility.createDirectory(parent)) {
                throw new IOException("Failed to create directory " + parent);
            }
            // write file content
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }
    }

}
