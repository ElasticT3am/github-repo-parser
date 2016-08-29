package com.elasticthree.projectparser;

import java.io.File;
import java.io.IOException;


class FileUtils {
        static File createNewDir(String dirName) {
            File reposDir = new File(dirName);
            if (!(reposDir.exists() && reposDir.isDirectory())) {
                if (!reposDir.mkdirs()) {
                    System.out.println("Failed to create repoparser directory. Exiting...");
                    System.exit(-1);
                }
            }
            return reposDir;
        }

        static File createNewFile(File file) throws IOException {
            file.createNewFile();
            if (file.createNewFile()) {
                System.out.println("Failed to create repoListFile. Exiting...");
                System.exit(-1);
            }
            return file;
        }

}
