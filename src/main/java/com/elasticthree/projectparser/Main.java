package com.elasticthree.projectparser;

import org.eclipse.egit.github.core.SearchRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mike on 8/28/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length < 3) {
            System.out.println("Usage: java Main <github_username> <github_pass> <year>");
            System.exit(-1);
        }

        int year = Integer.valueOf(args[2]);
        for (int month = 1; month <= 12; month++) {
            for (int day = 1; day <= 31; day++) {
                for (int hour = 0; hour < 24; hour += 8) {
                    String dateRange = DateUtils.getNext8HourRange(year, day, month, hour);

                    Map<String, String> requestParams = new HashMap<>();
                    requestParams.put("language", "Java");
                    requestParams.put("created", dateRange.toString());
                    RepoParser repoParser = new RepoParser(args[0], args[1], requestParams);

                    Map<String, SearchRepository> repoMap = new HashMap<>();
                    for (List<SearchRepository> repoList : repoParser) {
                        for (SearchRepository repo : repoList) {
                            if (repoMap.containsKey(repo.getUrl())) {
                                System.out.println("Batman: This repo already exists.. Refusing to re-add it");
                                continue;
                            }
                            String repoZipUrl = repo.getUrl() + "/archive/master.zip";
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
}
