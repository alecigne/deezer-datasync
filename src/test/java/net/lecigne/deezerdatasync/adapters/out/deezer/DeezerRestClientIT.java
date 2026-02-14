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
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.common.SyncException;
import net.lecigne.deezerdatasync.domain.user.UserId;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
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

  private DeezerClient deezerClient;

  @BeforeEach
  void setUp() {
    deezerClient = DeezerRestClient.init(getTestConfig(wireMock.getPort()));
  }

  @Test
  void should_fetch_albums() {
    // Given
    wireMockStub("^/user/.*/albums$", "/data/albums.json");
    List<Album> expectedAlbums = Fixtures.albums();

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

    // When
    ThrowingCallable call = () -> deezerClient.getUserAlbums(UserId.of(1));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(SyncException.class)
        .hasMessageContaining("empty body");
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
