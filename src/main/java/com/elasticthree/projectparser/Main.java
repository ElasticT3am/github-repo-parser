package com.elasticthree.projectparser;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.SearchRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static void main(String[] args) {

        Options options = ParserCommandLineUtils.getCommandLineOpts();
        CommandLine line = ParserCommandLineUtils.validateArgs(args, options);
        String userName = line.getOptionValue("username");
        String pass = line.getOptionValue("password");
        int year = Integer.valueOf(line.getOptionValue("year"));
        File reposDir = ParserFileUtils.createNewDir(System.getProperty("user.home") + "/.repoparser/" + year);
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (String dateRange : new ParserDateUtils(year)) {
            executorService.execute(() -> {
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put("language", "Java");
                requestParams.put("created", dateRange);
                RepoParser repoParser = null;
                try {
                    repoParser = new RepoParser(userName, pass, requestParams);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert repoParser != null;
                for (List<SearchRepository> repoList : repoParser) {
                    assert repoList != null;
                    for (SearchRepository repo : repoList) {
                        String repoZipUrl = repoParser.getRepoZipUrl(repo);
                        try {
                            Files.write(Paths.get(repoParser.getReposFile().toString()), (repoZipUrl + "\n").getBytes(), StandardOpenOption.APPEND);
                            File zipFile = new File(reposDir, repoZipUrl.replace("https://", "").replace("/", "_"));
                            FileUtils.copyURLToFile(new URL(repoZipUrl), zipFile);
                            File repositoryDir = ParserFileUtils.unzipFile(zipFile.getAbsolutePath());
                            //analyze and upload to neo4j
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }


}
