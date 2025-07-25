# Changelog

## [v0.5.3](https://github.com/alecigne/deezer-datasync/tree/v0.5.3) - 2025-07-12

Update dependencies

## [v0.5.2](https://github.com/alecigne/deezer-datasync/tree/v0.5.2) - 2025-02-05

- Use `maven-compiler-plugin`
- Update dependencies

## [v0.5.1](https://github.com/alecigne/deezer-datasync/tree/v0.5.1) - 2024-08-20

Update dependencies

## [v0.5.0](https://github.com/alecigne/deezer-datasync/tree/v0.5.0) - 2024-07-30

Do not create empty commits

## [v0.4.3](https://github.com/alecigne/deezer-datasync/tree/v0.4.3) - 2024-07-20

Update dependencies. Made the switch to Maven Failsafe 3 (and consequently, Maven Surefire 3) by
finally fixing an error involving modules.

## [v0.4.2](https://github.com/alecigne/deezer-datasync/tree/v0.4.2) - 2024-07-01

Update dependencies

## [v0.4.1](https://github.com/alecigne/deezer-datasync/tree/v0.4.1) - 2024-05-04

- Update dependencies
- Improve logging and code readability

## [v0.4.0](https://github.com/alecigne/deezer-datasync/tree/v0.4.0) - 2024-02-10

Change playlist filename format from `{id}.json` to `{id}_{title}.json`.

## [v0.3.0](https://github.com/alecigne/deezer-datasync/tree/v0.3.0) - 2024-02-03

- Add ability to sync all playlists simply by providing an empty list of playlist IDs.
- Do not store `apt` cache in Docker image.
