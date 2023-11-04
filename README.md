# `deezer-datasync` :musical_note::floppy_disk:

[![Multi-Arch Build and Push](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml)

`deezer-datasync` synchronizes data from Deezer:

- Favorite albums
- Favorite artists
- List of playlists
- A single favorite playlist (will evolve in a future version)

As of the current version, only GitHub is supported as a backend; the application will commit JSON
files in this arborescence:

```
your-git-repo/
├── playlists/
│   ├── id1.json
│   ├── id2.json
│   ├── [...]
├── albums.json
├── artists.json
└── playlists.json
```

# Disclaimer

**This project is mainly developed for my personal use and as such, is experimental.** However I'll
be glad to help if you encounter any [issue](https://github.com/alecigne/deezer-datasync/issues).

Please note that:

> The number of requests per second is limited to 50 requests / 5
> seconds. ([Deezer](https://developers.deezer.com/api))

In its current version, the application is not rate-limited so you might reach Deezer's limitation
if not careful. For example:

- 700 albums
- 150 artists
- 50 playlists
- 1 playlist with 500 tracks

...at 200 items/request will result in 4 + 1 + 1 + 3 = 9 requests. See config file below to increase
the number of elements per request.

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
      playlistId = a-playlist-of-your-choice
    }
    url = "https://api.deezer.com"
    token = your-deezer-token
    // Maximum number of results per request. 100 is a hard minimum to limit the number of requests.
    limit = 200
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
docker run -it -v /absolute/path/to/application.conf:/application.conf alecigne/deezer-datasync
```

(The image will be downloaded automatically.)

## Option 2: From a Jar

Download a Jar in the [releases](https://github.com/alecigne/deezer-datasync/releases) section, then
run it using Java 17:

``` shell
java -jar -Dconfig.file=/path/to/application.conf deezer-datasync.jar
```

## Option 3: Build from source

You probably know what you're doing anyway :slightly_smiling_face:
