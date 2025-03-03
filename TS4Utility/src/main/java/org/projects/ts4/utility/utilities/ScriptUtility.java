//package org.projects.ts4.utility.utilities;
//
//import org.projects.ts4.utility.constants.ConfigConstants;
//
//import java.io.File;
//
//public abstract class ScriptUtility {
//
//    public static File createTarShellScript(String userDir) {
//        File file = FileUtility.createFile(userDir, "src/main/resources", "tar.sh");
//        FileUtility.write(file, "#!/bin/bash");
//        FileUtility.write(file, "cd \"${location}\"");
//        FileUtility.write(file, "tar -xf \"${filename}\"");
//        FileUtility.write(file, "rm -rf \"${filename}\"");
//        return file;
//    }
//
////    public static ProcessBuilder createProcessBuilder(File script, File tar) {
////        ProcessBuilder processBuilder = new ProcessBuilder(script.getAbsolutePath());
////        processBuilder.environment().put("location", tar.getParent());
////        processBuilder.environment().put("filename", tar.getName());
////        return processBuilder;
////    }
//
//}
