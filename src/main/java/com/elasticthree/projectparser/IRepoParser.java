package com.elasticthree.projectparser;

import java.io.File;
import java.util.Iterator;

/**
 * Created by mmilonakis on 5/9/2016.
 */
public interface IRepoParser extends Iterable<Repository> {
    Iterator<Repository> iterator();

    File getReposFile();

    File fetchRepo(Repository repo);
}
