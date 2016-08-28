package com.elasticthree.projectparser;

import java.io.File;
import java.io.IOException;

/**
 * Created by mike on 8/28/16.
 */
public class FileUtils {
        public static File createNewDir(String dirName) {
            File reposDir = new File(dirName);
            if (!(reposDir.exists() && reposDir.isDirectory())) {
                if (!reposDir.mkdirs()) {
                    System.out.println("Failed to create repoparser directory. Exiting...");
                    System.exit(-1);
                }
            }
            return reposDir;
        }

        public static File createNewFile(File file) throws IOException {
            file.createNewFile();
            if (file.createNewFile()) {
                System.out.println("Failed to create repoListFile. Exiting...");
                System.exit(-1);
            }
            return file;
        }

}
