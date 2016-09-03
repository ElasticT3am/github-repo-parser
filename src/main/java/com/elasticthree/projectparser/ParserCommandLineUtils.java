package com.elasticthree.projectparser;

import org.apache.commons.cli.*;

/**
 * Created by mmilonakis on 9/3/16.
 */
public class ParserCommandLineUtils {
    static CommandLine validateArgs(String[] args, Options options) {

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

    static Options getCommandLineOpts() {
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
