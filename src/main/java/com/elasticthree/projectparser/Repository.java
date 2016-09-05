package com.elasticthree.projectparser;

/**
 * Created by mmilonakis on 5/9/2016.
 */
public class Repository {
    private final String url;
    private final String repoZipUrl;
    private final String repoDirectory;
    private int size = -1;

    public Repository(String url) {
        this.url = url.replace("/archive/master.zip", "");
        this.repoZipUrl = url;
        this.repoDirectory =  url.replace(".zip", "").replace("https://", "").replace("/", "_");
    }

    Repository setSize(int size) {
        this.size = size;
        return this;
    }

    int getSize() {
        return size;
    }

    String getUrl() {
        return url;
    }

    String getZipUrl() {
        return repoZipUrl;
    }

    String getRepoDirectory() {
        return repoDirectory;
    }
}
