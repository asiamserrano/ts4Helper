package org.example;

import java.util.Set;
import java.util.HashSet;
//import java.util.List;
//import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.example.ExtensionEnum.ZIP;

public class Unzipper {

    private static final byte[] buffer = new byte[1024];
//    public static final String ZIP_EXTENSION = ".zip";
    public static final String PACKAGE_EXTENSION = ".package";
    private static Set<File> set;

    public static void main(String[] args) throws Exception {
        String directory = "/Users/asiaserrano/ChromeDownloads";
        File file = new File(directory, "Archive.zip");
//        File file = new File(directory);
        unzip(file);
    }

    public static void unzip(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f: Objects.requireNonNull(file.listFiles())) {
                if (isZipFile(f)) unzip(f);
            }
            unzip(set);
        } else if (isZipFile(file)) {
            String destDirName = file.getName().replace(ZIP.extension, "");
            File destDir = new File(file.getParent(), destDirName);
            String fileZip = file.getAbsolutePath();
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));

            if (set == null) {
                set = new HashSet<>();
                unzip(zis, destDir);
                unzip(set);
            } else {
                unzip(zis, destDir);
            }
        } else {
            System.out.println("cannot unzip file: " + file);
        }
    }
    private static boolean isZipFile(File file) {
        return file.getName().contains(ZIP.extension);
    }
    private static void unzip(Set<File> directories) throws IOException {
        if (!directories.isEmpty()) {
            set = new HashSet<>();
            for (File directory: directories) {
                unzip(directory);
            }
            unzip(set);
        }
    }
    private static void unzip(ZipInputStream zis, File destDir) throws IOException {
        ZipEntry zipEntry;
        while (true) {
            zipEntry = zis.getNextEntry();
            if (zipEntry == null) {
                break;
            } else {
                String[] parts = zipEntry.getName().split("/");
                String zipEntryName = parts[parts.length - 1];
                File newFile = new File(destDir, zipEntryName);
                boolean isZipEntryDirectory = zipEntry.isDirectory();

                if (zipEntryName.contains(PACKAGE_EXTENSION)) {
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
            if (!newFile.isDirectory() && !newFile.mkdirs()) {
                throw new IOException("Failed to create directory " + newFile);
            }
        } else {
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
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