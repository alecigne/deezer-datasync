# `deezer-datasync` :musical_note::floppy_disk:

[![Build and publish Docker image](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml)

`deezer-datasync` synchronizes data from Deezer:

- Favorite albums
- Favorite artists
- List of playlists
- Individual playlists with their associated tracks

As of the current version, only GitHub is supported as a backend; the application will commit JSON
files in this arborescence:

```
your-git-repo/
├── playlists/
│   ├── {id}_{title}.json
│   ├── {id}_{title}.json
│   ├── [...]
├── albums.json
├── artists.json
└── playlists.json
```

See the [changelog](CHANGELOG.md).

# Disclaimer

**This project is mainly developed for my personal use and as such, is experimental.** However I'll
be glad to help if you encounter any [issue](https://github.com/alecigne/deezer-datasync/issues).

# Usage

## Common steps

### Deezer token

You'll need a Deezer token to use this application. You can follow Deezer's
instructions [here](https://developers.deezer.com/api/oauth). I have also written a documentation
[here](https://lecigne.net/notes/deezer-token.html).

### Configuration file

Prepare a configuration file in [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md)
format:

``` hocon
config {
  application {
    zone = "your-zone" // e.g Europe/Paris"
  }
  deezer {
    profile {
      userId = your-user-id
      // Playlists to backup -- leave empty to backup *all* your playlists
      playlistIds = [id1, id2]
    }
    url = "https://api.deezer.com"
    token = your-deezer-token
    // Maximum number of results per request. 100 is a hard minimum to limit the number of requests.
    maxResults = 200
    // Number of calls per second to the API. 10 is a hard maximum to avoid Deezer's limit (50/5 seconds).
    rateLimit = 5
  }
  github {
    token = your-github-token
    repo = your-repo
    branch = your-branch
  }
}
```

## Option 1: Run with Docker

A multiplatform image (AMD64 and ARMv7 -- for execution on a Raspberry Pi 3) is available on
[Dockerhub](https://hub.docker.com/r/alecigne/deezer-datasync):

``` shell
# Pull the latest version of the image
docker pull alecigne/deezer-datasync:latest

# Run the container with the latest image (check that your config file is compatible!)
docker run -it -v /absolute/path/to/application.conf:/application.conf alecigne/deezer-datasync
```

## Option 2: From a Jar

Download a Jar in the [releases](https://github.com/alecigne/deezer-datasync/releases) section, then
run it using Java 17:

``` shell
java -jar -Dconfig.file=/path/to/application.conf deezer-datasync.jar
```

## Option 3: Build from source

You probably know what you're doing anyway :slightly_smiling_face:

# Notes

Playlist filenames (`{id}_{title}.json`) are generated from "clean" playlist titles:

* The title is converted to lowercase.
* Any group of non-alphanumeric characters (size 1 or more) is converted to an underscore.
* Any trailing underscore is removed from the title.

You can see basic cases in [issue #28](https://github.com/alecigne/deezer-datasync/issues/28). This
issue was the basis for the parameterized test
in [`GitHubMapperTest.java`](https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperTest.java).
There is also
a [property-based test](https://en.wikipedia.org/wiki/Software_testing#Property_testing)
in [`GitHubMapperPropertyTest.java`](https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperPropertyTest.java).
