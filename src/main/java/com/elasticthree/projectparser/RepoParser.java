package com.elasticthree.projectparser;

import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import java.io.*;
import java.util.*;

class RepoParser implements Iterable<SearchRepository> {

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
        this.repoListFileName = "java_repos_" + requestParams.get("created").replace("\"","")
                .replace(" ","").replace(":","-");
        reposFile = new File(System.getProperty("user.home") + "/.repoparser/" + year + "/", this.repoListFileName);
        ParserFileUtils.createNewFile(reposFile);
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

    private List<SearchRepository> getNextResultsPage() throws IOException {
        List<SearchRepository> repos = repositoryService.searchRepositories(params, this.pageNumber++);
        return repos;
    }

    String getRepoZipUrl(SearchRepository repository) {

        return repository == null ? null : repository.getUrl() + "/archive/master.zip";
    }

    public Iterator<SearchRepository> iterator() {
        try {
            return new Iterator<SearchRepository>() {

                Iterator<SearchRepository> currentPage = getNextResultsPage().iterator();

                public boolean hasNext() {
                    return currentPage.hasNext();
                }

                public SearchRepository next() {
                    SearchRepository repo = currentPage.next();
                    if (!currentPage.hasNext())
                        try {
                            currentPage = getNextResultsPage().iterator();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    return repo;
                }
                public void remove() {}
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot fetch any more repositories from github.");
        }
    }
}
