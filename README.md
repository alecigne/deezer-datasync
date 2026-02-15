# `deezer-datasync` :musical_note::floppy_disk:

[![Build and publish Docker image](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/alecigne/deezer-datasync/actions/workflows/docker-publish.yml)

`deezer-datasync` synchronizes user data from Deezer:

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

Using GitHub as a backend can seem peculiar, but it fits my needs very well: keeping a full history
of my profile and easily check daily diffs, especially to make sure Deezer isn't altering my data.

See the [changelog][changelog].

# Disclaimer

**This project is mainly developed for my personal use and as such, is experimental.** However I'll
be glad to help if you encounter any [issue][issues].

# Usage

## Common steps

### Deezer token

You'll need a Deezer token to use this application. You can follow Deezer's instructions
[here][oauth1]. I have also written a documentation [here][oauth2].

#### Warning (2025-02-05)

It seems that Deezer has disabled the creation of 'apps':

> We're not accepting new application creation at this time. Please check again later.

This means you can't create the token that this project needs to run. If you have a valid API token
somewhere, it should work. At least my instance still runs for now. Not for long?

There's nothing I can do about that except encourage you to give your money to another service.
Hopefully with a logo that won't make you want to tear your eyes out of their sockets with a rusty
spoon.

### Configuration file

To use the application, you need to prepare a configuration file in [HOCON][hocon] format. An
example file is provided in the source code, see [application.conf][appconf].

## Option 1: Run with Docker

A multiplatform image (AMD64 and ARMv7 -- for execution on a Raspberry Pi 3) is available
on [Dockerhub][dockerhub]:

``` shell
# Pull the latest version of the image
docker pull alecigne/deezer-datasync:latest

# Run the container with the latest image (check that your config file is compatible!)
docker run -it -v /absolute/path/to/application.conf:/application.conf alecigne/deezer-datasync
```

Only the last stable version is available on Dockerhub (`master` branch).

## Option 2: From a Jar

Download a Jar in the [releases][releases] section, then run it using Java 17:

``` shell
java -jar -Dconfig.file=/path/to/application.conf deezer-datasync.jar
```

Only the stable versions are available as releases (`master` branch).

## Option 3: Build from source

You probably know what you're doing anyway :slightly_smiling_face:

# Notes

Playlist filenames (`{id}_{title}.json`) are generated from "clean" playlist titles:

* The title is converted to lowercase.
* Any group of non-alphanumeric characters (size 1 or more) is converted to an underscore.
* Any trailing underscore is removed from the title.

You can see basic cases in [issue #28][issue28]. This issue was the basis for the parameterized test
in [`GitHubMapperTest.java`][mapper-test]. There is also a [property-based test][pbt] in
[`GitHubMapperPropertyTest.java`][property-test].

[changelog]:
CHANGELOG.md

[issues]:
https://github.com/alecigne/deezer-datasync/issues

[oauth1]:
https://developers.deezer.com/api/oauth

[oauth2]:
https://lecigne.net/notes/deezer-token.html

[hocon]:
https://github.com/lightbend/config/blob/main/HOCON.md

[dockerhub]:
https://hub.docker.com/r/alecigne/deezer-datasync

[releases]:
https://github.com/alecigne/deezer-datasync/releases

[issue28]:
https://github.com/alecigne/deezer-datasync/issues/28

[mapper-test]:
https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperTest.java

[pbt]:
https://en.wikipedia.org/wiki/Software_testing#Property_testing

[property-test]:
https://github.com/alecigne/deezer-datasync/blob/master/src/test/java/net/lecigne/deezerdatasync/repository/destinations/github/GitHubMapperPropertyTest.java

[appconf]:
./src/main/resources/application.conf
