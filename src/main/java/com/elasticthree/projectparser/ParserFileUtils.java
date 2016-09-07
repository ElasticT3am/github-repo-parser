package com.elasticthree.projectparser;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


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
                return null;
            }
            return dest;
        }

    public static void deleteDirectory(File repositoryDir) {
        Path directory = Paths.get(repositoryDir.getAbsolutePath());
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
