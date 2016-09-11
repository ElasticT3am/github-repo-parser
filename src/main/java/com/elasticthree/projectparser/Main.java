package com.elasticthree.projectparser;

import com.elasticthree.ASTCreator.ASTCreator.ASTCreator;
import com.elasticthree.ASTCreator.ASTCreator.Helpers.RecursivelyProjectJavaFiles;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("resources/log4j.properties");
        Options options = ParserCommandLineUtils.getCommandLineOpts();
        CommandLine line = ParserCommandLineUtils.validateArgs(args, options);
        String userName = line.getOptionValue("username");
        String pass = line.getOptionValue("password");
        int year = Integer.valueOf(line.getOptionValue("year"));
        boolean isDownload = line.hasOption("download");
        boolean noKeepFiles = line.hasOption("no-keep-files");
        boolean isUpload = line.hasOption("upload");
        int fromMonth = line.hasOption("from-month") ? Integer.valueOf(line.getOptionValue("from-month")) : 1;
        int toMonth = line.hasOption("to-month") ? Integer.valueOf(line.getOptionValue("to-month")) : 12;
        ParserDateUtils dateRanges = new ParserDateUtils(year, fromMonth, toMonth);


        File repoDir = ParserFileUtils.createNewDir(System.getProperty("user.home") + "/.repoparser/" + year);
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.valueOf(line.getOptionValue("threads", "1")));

        for (String dateRange : dateRanges) {
            executorService.execute(() -> {
                IRepoParser repoParser = null;
                if (isDownload) {
                    Map<String, String> requestParams = RemoteRepoParser.createApiRequest(dateRange);
                    try {
                        repoParser = new RemoteRepoParser(userName, pass, requestParams);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        repoParser = new LocalRepoParser(dateRange, repoDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                assert repoParser != null;
                for (Repository repo : repoParser) {
                    if (repo.getSize() > 200000)
                        continue;
                    File repositoryDir = repoParser.fetchRepo(repo);
                    if (repositoryDir == null)
                        continue;
                    if (isUpload) {
                        try {
                            List<String> classes = RecursivelyProjectJavaFiles
                                    .getProjectJavaFiles(repositoryDir.getAbsolutePath());
                            ASTCreator ast = new ASTCreator(repo.getUrl());
                            ast.repoASTProcedure(classes);
                        }
                        catch (Exception e) {
                            System.out.println("\n\n <<<<<< Batman: Repo could not be uploaded >>>>>>>");
                            e.printStackTrace();
                            System.out.println("Batman, the repo: " + repo.getUrl() + "Could not be uploaded" +
                                    " because the exception: << " + e.getMessage() + " >> was thrown. Deleting the repo" +
                                    "directory: " + repositoryDir);
                        }
                        finally {
                            if (noKeepFiles)
                                ParserFileUtils.deleteDirectory(repositoryDir);
                        }
                    }
                }
            });
        }
        executorService.shutdown();
    }
}
