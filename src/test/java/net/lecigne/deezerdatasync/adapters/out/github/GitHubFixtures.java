package net.lecigne.deezerdatasync.adapters.out.github;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.album.AlbumId;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.track.Track;

public final class GitHubFixtures {

  private GitHubFixtures() {}

  public static DeezerData data() {
    Instant instant = LocalDateTime.of(2023, 10, 1, 0, 0, 0).atZone(ZoneId.of("Europe/Paris")).toInstant();
    return DeezerData.builder()
        .albums(List.of(Album.builder()
            .albumId(AlbumId.of(1L))
            .artist("artist")
            .title("title")
            .creationTimeUtc(instant)
            .build()))
        .artists(List.of(Artist.builder()
            .deezerId(1L)
            .name("name")
            .creationTimeUtc(instant)
            .build()))
        .playlistInfos(List.of(PlaylistInfo.builder()
            .playlistId(PlaylistId.of(10))
            .title("title")
            .duration(Duration.ofSeconds(296472))
            .nbTracks(10)
            .fans(1)
            .creationTimeUtc(instant)
            .build()))
        .playlists(List.of(Playlist.builder()
            .playlistId(PlaylistId.of(10))
            .title("title")
            .description("description")
            .duration(Duration.ofSeconds(86400))
            .nbTracks(10)
            .fans(1)
            .creationTimeUtc(instant)
            .tracks(List.of(Track.builder()
                .deezerId(50L)
                .artist("artist")
                .title("title")
                .album("album")
                .creationTimeUtc(instant)
                .build()))
            .build()))
        .build();
  }

  public static GitHubBackup backup() {
    return GitHubBackup.builder()
        .gitHubFiles(List.of(
            GitHubFile.builder()
                .path("albums.json")
                .rawContent("[\n"
                    + "  {\n"
                    + "    \"deezerId\": 1,\n"
                    + "    \"artist\": \"artist\",\n"
                    + "    \"title\": \"title\",\n"
                    + "    \"creationTimeUtc\": \"2023-09-30T22:00:00Z\"\n"
                    + "  }\n"
                    + "]")
                .build(),
            GitHubFile.builder()
                .path("artists.json")
                .rawContent("[\n"
                    + "  {\n"
                    + "    \"deezerId\": 1,\n"
                    + "    \"name\": \"name\",\n"
                    + "    \"creationTimeUtc\": \"2023-09-30T22:00:00Z\"\n"
                    + "  }\n"
                    + "]")
                .build(),
            GitHubFile.builder()
                .path("playlists.json")
                .rawContent("[\n"
                    + "  {\n"
                    + "    \"deezerId\": 10,\n"
                    + "    \"title\": \"title\",\n"
                    + "    \"duration\": \"PT82H21M12S\",\n"
                    + "    \"nbTracks\": 10,\n"
                    + "    \"fans\": 1,\n"
                    + "    \"creationTimeUtc\": \"2023-09-30T22:00:00Z\"\n"
                    + "  }\n"
                    + "]")
                .build(),
            GitHubFile.builder()
                .path("playlists/10_title.json")
                .rawContent("{\n"
                    + "  \"deezerId\": 10,\n"
                    + "  \"title\": \"title\",\n"
                    + "  \"duration\": \"PT24H\",\n"
                    + "  \"nbTracks\": 10,\n"
                    + "  \"fans\": 1,\n"
                    + "  \"creationTimeUtc\": \"2023-09-30T22:00:00Z\",\n"
                    + "  \"description\": \"description\",\n"
                    + "  \"tracks\": [\n"
                    + "    {\n"
                    + "      \"deezerId\": 50,\n"
                    + "      \"artist\": \"artist\",\n"
                    + "      \"title\": \"title\",\n"
                    + "      \"album\": \"album\",\n"
                    + "      \"creationTimeUtc\": \"2023-09-30T22:00:00Z\"\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}")
                .build()))
        .build();
  }

}
