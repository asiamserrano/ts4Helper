package org.example;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import static org.example.Constants.DS_Store;

public class Zipper {

    private static Map<String, String> env = new HashMap<>() {{
        put("create", "true");
    }};
    public static void main(String[] args) throws Exception {
        File file = new File("/Users/asiaserrano/zzz");
        Set<File> files = getFiles(file);
        zipFiles(files);
    }

    public static void zipFile(File file) throws Exception {
        String full_filename = file.getName();
        String filename = full_filename.split("\\.")[0];
        String uri_filename = String.format("jar:file:%s/%s.zip", file.getParent(), filename);
        URI uri = URI.create(uri_filename);

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path externalTxtFile = Paths.get(file.getAbsolutePath());
            Path pathInZipfile = zipfs.getPath("/" + full_filename);
            Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }

    }

    private static void zipFiles(Set<File> files) throws Exception {
        for (File file: files) {
            zipFile(file);
        }
    }

    private static Set<File> getFiles(File folder) {
        Set<File> set = new HashSet<>();
        for (File file: Objects.requireNonNull(folder.listFiles())) {
            if (!file.getName().equals(DS_Store)) {
                set.add(file);
            }
        }
        return set;
    }

}
