package com.elasticthree.projectparser;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import java.io.*;
import java.util.*;

class RepoParser implements Iterable<List<SearchRepository>> {

    private final RepositoryService repositoryService;
    private final String repoListFileName;
    private final File reposFile;
    private int pageNumber;
    private Map<String, String> params;


    File getReposFile() {
        return reposFile;
    }

    RepoParser(String userName, String pass, Map<String, String> requestParams) throws IOException {
        String year = requestParams.get("created").substring(1,5);
        File reposDir = FileUtils.createNewDir(System.getProperty("user.home") + "/.repoparser/" + year);
        this.repoListFileName = "java_repos_" + requestParams.get("created").replace("\"","")
                .replace(" ","").replace(":","-");
        reposFile = new File(reposDir, this.repoListFileName);
        FileUtils.createNewFile(reposFile);
        this.pageNumber = 0;
        this.params = requestParams;
        this.repositoryService = newRepositoryService(userName, pass);
    }

    private RepositoryService newRepositoryService(String userName, String pass) {
        GitHubClient client = authenticateClient(new GitHubClient(), userName, pass);
        return new RepositoryService(client);
    }

    private GitHubClient authenticateClient(GitHubClient client, String userName, String pass) {
        return client.setCredentials(userName, pass);
    }

    private List<SearchRepository> getNextJavaReposPage(RepositoryService service) throws IOException {
        List<SearchRepository> repos = service.searchRepositories(params, this.pageNumber++);
        return repos;
    }

    public Iterator<List<SearchRepository>> iterator() {
        return new Iterator<List<SearchRepository>>() {

            public boolean hasNext() {
                return pageNumber < 10;
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
}
