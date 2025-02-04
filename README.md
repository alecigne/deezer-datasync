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

See the [changelog][1].

# Disclaimer

**This project is mainly developed for my personal use and as such, is experimental.** However I'll
be glad to help if you encounter any [issue][2].

# Usage

## Common steps

### Deezer token

You'll need a Deezer token to use this application. You can follow Deezer's instructions [here][3].
I have also written a documentation [here][4].

#### Warning (2025-02-05)

It seems that Deezer has disabled the creation of 'apps':

> We're not accepting new application creation at this time. Please check again later.

This means you can't create the token that this project needs to run. If you have a valid API token
somewhere, it should work. At least my instance still runs for now. Not for long?

There's nothing I can do about that except encourage you to give your money to another service.
Hopefully with a logo that won't make you want to tear your eyes out of their sockets with a rusty
spoon.

### Configuration file

Prepare a configuration file in [HOCON][5] format:

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
    repo = your-repo // in the format owner/repo
    branch = your-branch
  }
}
```

## Option 1: Run with Docker

A multiplatform image (AMD64 and ARMv7 -- for execution on a Raspberry Pi 3) is available
on [Dockerhub][6]:

``` shell
# Pull the latest version of the image
docker pull alecigne/deezer-datasync:latest

# Run the container with the latest image (check that your config file is compatible!)
docker run -it -v /absolute/path/to/application.conf:/application.conf alecigne/deezer-datasync
```

## Option 2: From a Jar

Download a Jar in the [releases][7] section, then run it using Java 17:

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

You can see basic cases in [issue #28][8]. This issue was the basis for the parameterized test in
[`GitHubMapperTest.java`][9]. There is also a [property-based test][10] in
[`GitHubMapperPropertyTest.java`][11].

[1]: CHANGELOG.md

[2]: https://github.com/alecigne/deezer-datasync/issues

[3]: https://developers.deezer.com/api/oauth

[4]: https://lecigne.net/notes/deezer-token.html

[5]: https://github.com/lightbend/config/blob/main/HOCON.md

[6]: https://hub.docker.com/r/alecigne/deezer-datasync

[7]: https://github.com/alecigne/deezer-datasync/releases

[8]: https://github.com/alecigne/deezer-datasync/issues/28

[9]: https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperTest.java

[10]: https://en.wikipedia.org/wiki/Software_testing#Property_testing

[11]: https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperPropertyTest.java
