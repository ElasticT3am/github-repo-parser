package com.elasticthree.projectparser;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;



class ParserFileUtils {
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

        static File unzipFile(String source) {
            File dest = null;
            try {
                ZipFile zip = new ZipFile(source);
                dest = createNewDir(source.replace(".zip", ""));
                zip.extractAll(dest.getAbsolutePath());
            } catch (ZipException e) {
                e.printStackTrace();
            }
            return dest;
        }
}
