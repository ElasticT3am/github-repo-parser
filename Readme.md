# GitHub repo Parser

This project's purpose is to provide a framework for downloading Github repositories, analyzing them using [ast-creator](https://github.com/ElasticThree/ast-generator), and uploading them to a [neo4j](https://neo4j.com/) graph store.

### Background

We utilize [Github's search api](https://developer.github.com/v3/search/), that allows us to query the github's repository database (which btw is powered by [elasticsearch](https://www.elastic.co/products/elasticsearch)).

Github API is aiming to provide us the means for retrieving a particular item we 're looking for - it's not intended to be used as an exhaustive search engine. What that means is that we can't ask queries like "Fetch me all java repos" directly. We 'd get just the first 1000 results back. We therefore need to find another way to get our results back.

 We also need to keep in mind that GitHub API enforces some limitation regarding the queries asked - per hour. Unauthenticated users can make up to 10 requests per minute, where authenticated users can make 30 requests per hour. That should not be a problem, as we can get 100 results per request. The bottleneck would be our network connection, and of course, storage.
 

### Implementation

We have used [egit-github](https://github.com/eclipse/egit-github) which is a java implementation of the GitHub api.

In order to bypass the number-of-results-returned restriction we have used 8-hour date-range queries iteratively. An example of such a query would be "Fetch me all java repos that were created between 2008-01-01-00:00:00 .. 2008-01-01-07:59:59". This way, we can get up to 1000 repositories per 8-hour, which should be more than enough.

As already said, in order for github parser to function properly [ast-creator](https://github.com/ElasticThree/ast-generator) needs to be installed on our local repository (both projects use [apache maven](https://maven.apache.org/) for dependency management).

The repositories are downloaded in a `~/.repoparser` directory, which is created at the `$HOME` directory of the current user.

We download the repositories in zip format, in order to avoid the overhead of downloading the git metadata - we just need the source code, not the commit history. We accomplish this by appending a `/archive/master.zip` to each repository URL fetched. The pattern is `<<url>>/archive/<<branch_name>>.zip`. Due to some limitations on the egit github library, we cannot get the default branch of some repo easily, so we "hardcode" the master branch on our requests. This of course has the limitation that a repo could have its default branch named something else (not master). This would make it impossible for us to download. The vast majority of the github projects (and we re talking for more than 95%) use the convention that the default branch is named master, so this would probably not by much of an issue.  

Our application has a Main demonstrating its intended usage. It accepts the following command line arguments:

```
usage: Main
 -d,--download              downlad the repositories 
    --from-month <MONTH>    start parsing from month MONTH
 -n,--no-keep-files         delete the repo directory after
                            downloading-extracting-processing
    --password <PASSWORD>   github PASSWORD
    --threads <THREADS>     Number of threads to be used
    --to-month <MONTH>      start parsing until month MONTH
    --upload                Whether to upload to a neo4j server or not
    --username <USERNAME>   github USERNAME
    --year <YEAR>           parse repositories create at YEAR
```

### Example usages

###### Download all 2008 repos, with 8 threads:
```
mvn -Dexec.args="--year 2008 --username 'USERNAME' --password 'PASSWORD' --download --threads 8"
```

###### Download all 2009 repos, with 16 threads, upload to neo4j, and delete files after downloading:
```
mvn -Dexec.args="--year 2009 --username 'USERNAME' --password 'PASSWORD' --download --threads 16 --upload --no-keep-files"
```

###### Download 2010 repos starting from June, with 32 threads, upload to neo4j, and delete files after downloading:
```
mvn -Dexec.args="--year 2010 --from-month 6 --username 'USERNAME' --password 'PASSWORD' --download --threads 32 --upload --no-keep-files"
```

###### Download 2011 repos from January to May (inclusive), with 4 threads, upload to neo4j, and delete files after downloading:
```
mvn -Dexec.args="--year 2011 --from-month 1 --to-month 5 --username 'USERNAME' --password 'PASSWORD' --download --threads 4 --upload --no-keep-files"
```
 
### Notes

* Be prepared for huge disk I/O. For best performance use tmpfs (a ram - mapped directory). To accomplish this, you can add a line like this in `/etc/fstab/`), and run `sudo mount -a`:
```
#/etc/fstab 
 tmpfs       /mnt/ramdir tmpfs   nodev,nosuid,noexec,nodiratime,size=8192M   0 0
```
Then make a symlink to this ramdir on `~/.repoparser`
  
```bash
ln -s /mnt/ramdir $HOME/.repoparser 
```

* Consider using the --no-keep-files option for deleting each repo after downloading-processing-uploading to neo4j. To get an idea, at year 2008,  580 projects were created, resulting in approx 20 GB space. Our experiment's dataset consists of every java repository created between 2008-2012. This was approximately 2 Terra Bytes of data that correspond to the repo zips downloaded from github, and then resulting to a 30-Gbyte neo4j index. This includes approx 50k repos and half a billion Java lines of code.
