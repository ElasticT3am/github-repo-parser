package com.elasticthree.projectparser;

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

class RemoteRepoParser implements IRepoParser {

    private final RepositoryService repositoryService;
    private final String repoListFileName;
    private final File reposFile;
    private int pageNumber;
    private Map<String, String> params;
    private File repositoryDir;


    RemoteRepoParser(String userName, String pass, Map<String, String> requestParams) throws IOException {
        String year = requestParams.get("created").substring(1,5);
        this.repoListFileName = "java_repos_" + requestParams.get("created").replace("\"","")
                .replace(" ","").replace(":","-");
        reposFile = new File(System.getProperty("user.home") + "/.repoparser/" + year + "/", this.repoListFileName);
        ParserFileUtils.createNewFile(reposFile);
        this.pageNumber = 0;
        this.params = requestParams;
        this.repositoryService = newRepositoryService(userName, pass);
        repositoryDir = new File(System.getProperty("user.home") + "/.repoparser/" + year);

    }


    public File getReposFile() {
        return reposFile;
    }

    @Override
    public File fetchRepo(Repository repo) {
        File zipFile = null;
        try {
            zipFile = new File(repositoryDir, repo.getZipUrl().replace("https://", "").replace("/", "_"));
            FileUtils.copyURLToFile(new URL(repo.getZipUrl()), zipFile);
            Files.write(Paths.get(repoListFileName), (repo.getUrl() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        File repoDir = ParserFileUtils.unzipFile(zipFile.getAbsolutePath());
        if (!zipFile.delete())
            System.out.println("Could not delete zip file: " + zipFile);
        return repoDir;
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

    @Override
    public Iterator<Repository> iterator() {
        try {
            return new Iterator<Repository>() {

                Iterator<SearchRepository> currentPage = getNextResultsPage().iterator();
                public boolean hasNext() {
                    return currentPage.hasNext();
                }

                public Repository next() {
                    SearchRepository repo = currentPage.next();
                    if (!currentPage.hasNext())
                        try {
                            currentPage = getNextResultsPage().iterator();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    return new Repository(repo.getUrl()).setSize(repo.getSize());
                }
                public void remove() {}
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot fetch any more repositories from github.");
        }
    }

    static Map<String, String> createApiRequest(String dateRange) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("language", "Java");
        requestParams.put("created", dateRange);
        return requestParams;
    }
}
