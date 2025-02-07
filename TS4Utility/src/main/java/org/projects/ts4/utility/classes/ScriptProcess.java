package org.projects.ts4.utility.classes;

import lombok.extern.slf4j.Slf4j;
import org.projects.ts4.utility.utilities.FileUtility;

import java.io.File;

@Slf4j
public class ScriptProcess {

    public final Process process;

    public static File createTarShellScript(String userDir) {
        File file = FileUtility.createFile(userDir, "src/main/resources", "tar.sh");
        FileUtility.write(file, "#!/bin/bash");
        FileUtility.write(file, "cd \"${location}\"");
        FileUtility.write(file, "tar -xf \"${filename}\"");
        FileUtility.write(file, "rm -rf \"${filename}\"");
        return file;
    }

    public static ScriptProcess build(File script, File tar) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(script.getAbsolutePath());
            processBuilder.environment().put("location", tar.getParent());
            processBuilder.environment().put("filename", tar.getName());
            return new ScriptProcess(processBuilder.start());
        } catch (Exception e) {
            log.error("unable to start process: {}", e.getMessage());
            return null;
        }
    }

    private ScriptProcess(Process process) {
        this.process = process;
    }

}
