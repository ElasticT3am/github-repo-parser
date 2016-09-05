package com.elasticthree.projectparser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class LocalRepoParser implements IRepoParser {

    private final LineIterator lineIterator;
    private File reposDir;

    public LocalRepoParser(String dateRange, File repositoryDir) throws IOException {
        String repoListFileName = "java_repos_" + dateRange.replace("\"", "").replace(" ", "").replace(":", "-");
        this.lineIterator = FileUtils.lineIterator(new File(repositoryDir, repoListFileName));
        reposDir = repositoryDir;
    }

    @Override
    public Iterator<Repository> iterator() {
        return new Iterator<Repository>() {

            public boolean hasNext() {
                return lineIterator.hasNext();
            }

            public Repository next() {
                Repository repo = new Repository(lineIterator.next());
                return repo;
            }
            public void remove() {}
        };
    }

    @Override
    public File getReposFile() {
        return null;
    }

    @Override
    public File fetchRepo(Repository repo) {
        File repoDirectory = new File(repo.getRepoDirectory());
        if (!repoDirectory.exists())
            return new File(reposDir, repo.getRepoDirectory());
        return null;
    }
}
