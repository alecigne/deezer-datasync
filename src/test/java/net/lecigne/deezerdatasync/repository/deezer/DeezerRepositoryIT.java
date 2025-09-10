package net.lecigne.deezerdatasync.repository.deezer;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.ROOT_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.model.DeezerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@DisplayName("The Deezer repository")
@Slf4j
class DeezerRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMock = WireMockExtension.newInstance()
      .options(WireMockConfiguration.options().dynamicPort())
      .build();

  private DeezerDatasyncConfig config;
  private DeezerRepository deezerRepository;

  @BeforeEach
  void setUp() {
    config = getTestConfig(wireMock.getPort());
    DeezerLoopingClient client = DeezerLoopingClient.init(config);
    deezerRepository = new DeezerRepository(client, new DeezerMapper(config));
  }

  @Test
  void should_fetch_data_correctly() {
    // Given
    registerWireMockStubs();
    DeezerData expectedData = Fixtures.getTestData();

    // When
    DeezerData actualData = deezerRepository.fetch(config.getDeezer().getProfile());

    // Then
    assertThat(actualData).usingRecursiveComparison().isEqualTo(expectedData);
  }

  private static void registerWireMockStubs() {
    Map<String, String> urlToJson = Map.of(
        "^/user/.*/albums$", "/data/albums.json",
        "^/user/.*/artists$", "/data/artists.json",
        "^/user/.*/playlists$", "/data/playlists.json",
        "^/playlist/.*", "/data/playlist.json");

    urlToJson.forEach((url, jsonPath) ->
        wireMock.stubFor(WireMock.get(WireMock.urlPathMatching(url))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getFileAsJson(jsonPath))))
    );
  }

  private static String getFileAsJson(String path) {
    try (InputStream resourceAsStream = DeezerRepositoryIT.class.getResourceAsStream(path)) {
      assert resourceAsStream != null;
      return new String(resourceAsStream.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private DeezerDatasyncConfig getTestConfig(int port) {
    Config typeSafeConfig = ConfigFactory.load("test");
    DeezerDatasyncConfig cfg = ConfigBeanFactory.create(typeSafeConfig.getConfig(ROOT_CONFIG), DeezerDatasyncConfig.class);
    cfg.getDeezer().setUrl(String.format("http://localhost:%s", port));
    return cfg;
  }

}
