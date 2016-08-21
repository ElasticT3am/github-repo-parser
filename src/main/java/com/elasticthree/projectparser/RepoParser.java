package com.elasticthree.projectparser;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class RepoParser implements Iterable<List<SearchRepository>> {

    private final RepositoryService repositoryService;
    private final File pageNumberFile;
    private int pageNumber;

    public RepoParser() throws IOException {
        this.pageNumberFile = getPageFile();
        this.pageNumber = getPageNumberFromFile();
        this.repositoryService = newRepositoryService();
    }

    public RepoParser(String userName, String pass) throws IOException {
        this.pageNumberFile = getPageFile();
        this.pageNumber = getPageNumberFromFile();
        this.repositoryService = newRepositoryService(userName, pass);
    }

    private File getPageFile() {

        File pageFile = new File(getClass().getClassLoader().getResource("repo_result_page.txt").getFile());
        if (!pageFile.exists()) {
            System.out.println("Non existing PageNumber file. Exiting...");
            System.exit(-1);
        }
        return pageFile;
    }

    private int getPageNumberFromFile() throws IOException {
        FileReader pageFile = new FileReader(pageNumberFile);
        Scanner scanner = new Scanner(pageFile);
        int pageNumber = scanner.nextInt();
        scanner.close();
        return pageNumber;
    }

    private RepositoryService newRepositoryService(String userName, String pass) {
        GitHubClient client = authenticateClient(new GitHubClient(), userName, pass);
        return new RepositoryService(client);
    }

    private RepositoryService newRepositoryService() {
        GitHubClient client = new GitHubClient();
        return new RepositoryService(client);
    }

    private GitHubClient authenticateClient(GitHubClient client, String userName, String pass) {
        return client.setCredentials(userName, pass);
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    private void writePageNumberToFile(int i) throws IOException {
        FileWriter wr = new FileWriter(pageNumberFile);
        wr.write(String.valueOf(i));
        wr.close();
    }

    private List<SearchRepository> getNextJavaReposPage(RepositoryService service) throws IOException {
        List<SearchRepository> repos = service.searchRepositories("language:Java", pageNumber);
        writePageNumberToFile(++this.pageNumber);
        return repos;
    }

    public Iterator<List<SearchRepository>> iterator() {
        return new Iterator<List<SearchRepository>>() {
            public boolean hasNext() {
                return true;
            }

            public List<SearchRepository> next() {
                try {
                    return getNextJavaReposPage(repositoryService);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            public void remove() {}
        };
    }

    public static void main(String[] args) throws IOException {

        RepoParser repoParser;
        if (args.length == 2)
            repoParser = new RepoParser();
        else
            repoParser = new RepoParser(args[1], args[2]);

        int pageNumber = repoParser.getPageNumber();
        System.out.println("Resuming from page: " + pageNumber);

        int times = 0;
        for (List<SearchRepository> repoList : repoParser) {
            for (SearchRepository repo : repoList) {
                System.out.println("Repo name:  " + repo.getName() + " , Language: "
                        + repo.getLanguage() + ", Url: " + repo.getUrl());
            }
            if (++times > 3)
                return;
        }
    }
}
