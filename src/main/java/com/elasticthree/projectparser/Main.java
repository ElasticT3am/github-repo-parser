package com.elasticthree.projectparser;

import org.apache.commons.cli.*;
import org.eclipse.egit.github.core.SearchRepository;
import java.io.IOException;
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

        for (int month = 1; month <= 12; month++) {
            for (int day = 1; day <= 31; day++) {
                for (int hour = 0; hour < 24; hour += 8) {
                    String dateRange = DateUtils.getNext8HourRange(year, day, month, hour);

                    Map<String, String> requestParams = new HashMap<>();
                    requestParams.put("language", "Java");
                    requestParams.put("created", dateRange);
                    RepoParser repoParser = new RepoParser(userName, pass, requestParams);

                    Map<String, SearchRepository> repoMap = new HashMap<>();
                    for (List<SearchRepository> repoList : repoParser) {
                        for (SearchRepository repo : repoList) {
                            if (repoMap.containsKey(repo.getUrl())) {
                                System.out.println("Batman: This repo already exists.. Refusing to re-add it");
                                continue;
                            }
                            String repoZipUrl = repoParser.getRepoZipUrl(repo);
                            repoMap.put(repo.getUrl(), repo);
                            try {
                                Files.write(Paths.get(repoParser.getReposFile().toString()), (repoZipUrl + "\n").getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private static CommandLine validateArgs(String[] args, Options options) {

        CommandLine line = null;
        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse( options, args );

            if( line.hasOption( "year" ) ) {
                System.out.println( line.getOptionValue( "block-size" ) );
            }
            else throw new RuntimeException("Please give year");
        }
        catch( ParseException exp ) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Main", options );
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
