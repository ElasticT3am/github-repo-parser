package com.elasticthree.projectparser;

/**
 * Created by mmilonakis on 5/9/2016.
 */
public class Repository {
    private final String url;
    private final String repoZipUrl;
    private final String repoDirectory;
    private int size;

    public Repository(String url) {
        this.url = url;
        this.repoZipUrl = url + "/archive/master.zip";
        this.repoDirectory = url.replace("https://", "").replace("/", "_").replace(".zip", "");
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
