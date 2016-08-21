package com.elasticthree.projectparser;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

/**
 * Created by mike on 8/21/16.
 */
public class RepoManager {

    public Git cloneRepo(String repoUrl, String path) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(new File(path))
                .call();
        return  git;
    }

    public void deleteRepoDirectory(Git gitRepo) {
        gitRepo.getRepository().getDirectory().delete();
    }

}
