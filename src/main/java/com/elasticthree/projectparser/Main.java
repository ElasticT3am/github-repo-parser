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


public class Main {

    public static void main(String[] args) throws IOException {

        Options options = getCommandLineOpts();
        CommandLine line = validateArgs(args, options);
        String userName = line.getOptionValue("username");
        String pass = line.getOptionValue("password");
        int year = Integer.valueOf(line.getOptionValue("year"));
        File reposDir = ParserFileUtils.createNewDir(System.getProperty("user.home") + "/.repoparser/" + year);

        for (String dateRange : new ParserDateUtils(year)) {

            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("language", "Java");
            requestParams.put("created", dateRange);
            RepoParser repoParser = new RepoParser(userName, pass, requestParams);

            for (List<SearchRepository> repoList : repoParser) {
                for (SearchRepository repo : repoList) {
                    String repoZipUrl = repoParser.getRepoZipUrl(repo);
                    try {
                        Files.write(Paths.get(repoParser.getReposFile().toString()), (repoZipUrl + "\n").getBytes(), StandardOpenOption.APPEND);
                        FileUtils.copyURLToFile(new URL(repoZipUrl), new File(reposDir, repoZipUrl.replace("https://", "").replace("/", "_")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static CommandLine validateArgs(String[] args, Options options) {

        CommandLine line = null;
        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse(options, args);

            if (line.hasOption("year")) {
                System.out.println(line.getOptionValue("block-size"));
            } else throw new RuntimeException("Please give year");
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options);
            System.exit(-1);
        }
        return line;
    }

    private static Options getCommandLineOpts() {
        Options options = new Options();

        options.addOption("d", "download", false,
                "also downlad the repositories in zip-compressed format (Not Implemented yet)");
        options.addOption(Option.builder().longOpt("year")
                .hasArg()
                .desc("parse repositories create at YEAR")
                .argName("YEAR")
                .required()
                .build());
        options.addOption(Option.builder().longOpt("username")
                .hasArg()
                .desc("github USERNAME")
                .argName("USERNAME")
                .required()
                .build());
        options.addOption(Option.builder().longOpt("password")
                .hasArg()
                .desc("github PASSWORD")
                .argName("PASSWORD")
                .required()
                .build());
        return options;
    }
}
