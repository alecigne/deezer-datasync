package net.lecigne.deezerdatasync.adapters.out.deezer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.application.ports.out.DeezerClient;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfigLoader;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.common.SyncException;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.track.Track;
import net.lecigne.deezerdatasync.domain.user.UserId;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@DisplayName("The Deezer client")
@Slf4j
class DeezerRestClientIT {

  @RegisterExtension
  static WireMockExtension wireMock = WireMockExtension.newInstance()
      .options(WireMockConfiguration.options().dynamicPort())
      .build();

  @Test
  void should_fetch_albums() {
    // Given
    wireMockStub("^/user/.*/albums$", "/data/albums.json");
    List<Album> expectedAlbums = Fixtures.albums();
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // When
    List<Album> albums = deezerClient.getUserAlbums(UserId.of(1));

    // Then
    assertThat(albums).usingRecursiveComparison().isEqualTo(expectedAlbums);
  }

  @Test
  void should_fetch_artists() {
    // Given
    wireMockStub("^/user/.*/artists$", "/data/artists.json");
    List<Artist> expectedArtists = Fixtures.artists();
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // When
    List<Artist> artists = deezerClient.getUserArtists(UserId.of(1));

    // Then
    assertThat(artists).usingRecursiveComparison().isEqualTo(expectedArtists);
  }

  @Test
  void should_fetch_playlist_infos() {
    // Given
    wireMockStub("^/user/.*/playlists$", "/data/playlists.json");
    List<PlaylistInfo> expectedPlaylistInfos = Fixtures.playlistInfos();
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // When
    List<PlaylistInfo> playlistInfos = deezerClient.getUserPlaylists(UserId.of(1));

    // Then
    assertThat(playlistInfos).usingRecursiveComparison().isEqualTo(expectedPlaylistInfos);
  }

  @Test
  void should_fetch_a_playlist() {
    // Given
    wireMockStub("^/playlist/.*", "/data/playlist.json");
    Playlist expectedPlaylist = Fixtures.ambientPlaylist();
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // When
    Playlist playlist = deezerClient.getPlaylist(PlaylistId.of(1));

    // Then
    assertThat(playlist).usingRecursiveComparison().isEqualTo(expectedPlaylist);
  }

  @Test
  void should_fail_when_deezer_returns_http_error() {
    // Given
    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/user/.*/albums$"))
        .atPriority(1)
        .willReturn(WireMock.aResponse().withStatus(500)));
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // "When"
    ThrowingCallable call = () -> deezerClient.getUserAlbums(UserId.of(1));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(SyncException.class)
        .hasMessageContaining("HTTP 500");
  }

  @Test
  void should_fail_when_deezer_returns_empty_body() {
    // Given
    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/user/.*/albums$"))
        .atPriority(1)
        .willReturn(WireMock.aResponse()
            .withStatus(204)));
    DeezerRestClient deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));

    // When
    ThrowingCallable call = () -> deezerClient.getUserAlbums(UserId.of(1));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(SyncException.class)
        .hasMessageContaining("empty body");
  }

  @Test
  void should_fetch_albums_across_multiple_pages() {
    // Given
    DeezerDatasyncConfig cfg = getTestConfig(wireMock.getPort());
    cfg.getDeezerConfig().setMaxResults(2); // force pagination
    DeezerClient pagedClient = DeezerRestClient.init(cfg);

    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/user/.*/albums$"))
        .withQueryParam("index", WireMock.equalTo("0"))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                  "data": [
                    { "id": 1, "title": "A1", "time_add": 1700000000, "artist": { "id": 11, "name": "X" } },
                    { "id": 2, "title": "A2", "time_add": 1700000001, "artist": { "id": 12, "name": "Y" } }
                  ],
                  "total": 3
                }
                """)));

    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/user/.*/albums$"))
        .withQueryParam("index", WireMock.equalTo("2"))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                  "data": [
                    { "id": 3, "title": "A3", "time_add": 1700000002, "artist": { "id": 13, "name": "Z" } }
                  ],
                  "total": 3
                }
                """)));

    // When
    List<Album> albums = pagedClient.getUserAlbums(UserId.of(1));

    // Then
    assertThat(albums).extracting(album -> album.albumId().id()).containsExactly(1L, 2L, 3L);
    wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathMatching("^/user/.*/albums$"))
        .withQueryParam("index", WireMock.equalTo("0")));
    wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathMatching("^/user/.*/albums$"))
        .withQueryParam("index", WireMock.equalTo("2")));
    wireMock.verify(2, WireMock.getRequestedFor(WireMock.urlPathMatching("^/user/.*/albums$")));
  }

  @Test
  void should_fetch_a_playlist_across_multiple_pages() {
    // Given
    DeezerDatasyncConfig cfg = getTestConfig(wireMock.getPort());
    cfg.getDeezerConfig().setMaxResults(2); // force pagination
    DeezerClient pagedClient = DeezerRestClient.init(cfg);

    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/playlist/123$"))
        .withQueryParam("index", WireMock.equalTo("0"))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                  "id": 123,
                  "title": "Paged playlist",
                  "description": "Test description",
                  "duration": 180,
                  "nb_tracks": 3,
                  "fans": 1,
                  "creation_date": "2024-01-01 12:00:00",
                  "tracks": {
                    "data": [
                      { "id": 1001, "title": "T1", "time_add": 1700000000, "artist": { "name": "A1" }, "album": { "title": "AL1" } },
                      { "id": 1002, "title": "T2", "time_add": 1700000001, "artist": { "name": "A2" }, "album": { "title": "AL2" } }
                    ],
                    "total": 3
                  }
                }
                """)));

    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching("^/playlist/123$"))
        .withQueryParam("index", WireMock.equalTo("2"))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                  "id": 123,
                  "title": "Paged playlist",
                  "description": "Test description",
                  "duration": 180,
                  "nb_tracks": 3,
                  "fans": 1,
                  "creation_date": "2024-01-01 12:00:00",
                  "tracks": {
                    "data": [
                      { "id": 1003, "title": "T3", "time_add": 1700000002, "artist": { "name": "A3" }, "album": { "title": "AL3" } }
                    ],
                    "total": 3
                  }
                }
                """)));

    // When
    Playlist playlist = pagedClient.getPlaylist(PlaylistId.of(123));

    // Then
    assertThat(playlist.getPlaylistId().id()).isEqualTo(123L);
    assertThat(playlist.getTracks())
        .extracting(Track::deezerId)
        .containsExactly(1001L, 1002L, 1003L);
    wireMock.verify(2, WireMock.getRequestedFor(WireMock.urlPathMatching("^/playlist/123$"))
        .withQueryParam("index", WireMock.equalTo("0")));
    wireMock.verify(1, WireMock.getRequestedFor(WireMock.urlPathMatching("^/playlist/123$"))
        .withQueryParam("index", WireMock.equalTo("2")));
    wireMock.verify(3, WireMock.getRequestedFor(WireMock.urlPathMatching("^/playlist/123$")));
  }

  private static void wireMockStub(String urlRegex, String jsonPath) {
    wireMock.stubFor(WireMock.get(WireMock.urlPathMatching(urlRegex))
        .willReturn(WireMock.aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(getFileAsJson(jsonPath))));
  }

  private static String getFileAsJson(String path) {
    try (InputStream resourceAsStream = DeezerRestClientIT.class.getResourceAsStream(path)) {
      assert resourceAsStream != null;
      return new String(resourceAsStream.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private DeezerDatasyncConfig getTestConfig(int port) {
    DeezerDatasyncConfig cfg = DeezerDatasyncConfigLoader.load("test");
    cfg.getDeezerConfig().setUrl(String.format("http://localhost:%s", port));
    return cfg;
  }

}
