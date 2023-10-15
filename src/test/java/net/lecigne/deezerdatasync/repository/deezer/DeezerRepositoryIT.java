package net.lecigne.deezerdatasync.repository.deezer;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.ROOT_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.model.DeezerData;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Deezer repository")
@Slf4j
class DeezerRepositoryIT {

  static MockWebServer mockWebServer;

  private DeezerDatasyncConfig config;
  private DeezerRepository deezerRepository;

  @BeforeAll
  static void beforeAll() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockWebServer.shutdown();
  }

  @BeforeEach
  void setUp() {
    config = getTestConfig(mockWebServer.getPort());
    DeezerClient client = DeezerClient.init(config.getDeezer());
    deezerRepository = new DeezerRepository(client, new DeezerMapper(config), config.getDeezer());
  }

  @Test
  void should_fetch_data_correctly() {
    // Given
    mockWebServer.setDispatcher(getDispatcher());
    DeezerData expectedData = Fixtures.getTestData();

    // When
    DeezerData actualData = deezerRepository.fetch(config.getDeezer().getProfile());

    // Then
    assertThat(actualData).usingRecursiveComparison().isEqualTo(expectedData);
  }

  private static Dispatcher getDispatcher() {
    Map<String, String> pathToJsonFileMap = Map.of(
        "/albums", "/data/albums.json",
        "/artists", "/data/artists.json",
        "/playlists", "/data/playlists.json",
        "/playlist/", "/data/playlist.json"
    );
    return new Dispatcher() {
      @NotNull
      @Override
      public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
        String path = Optional.ofNullable(recordedRequest.getPath()).orElse("");
        log.debug("Received request on path: {}", path);
        return pathToJsonFileMap.entrySet().stream()
            .filter(entry -> path.contains(entry.getKey()))
            .findFirst()
            .map(entry -> new MockResponse().setResponseCode(200).setBody(getFileAsJson(entry.getValue())))
            .orElse(new MockResponse().setResponseCode(404));
      }
    };
  }

  private static String getFileAsJson(String path) {
    try (InputStream resourceAsStream = DeezerRepositoryIT.class.getResourceAsStream(path)) {
      assert resourceAsStream != null;
      return new String(resourceAsStream.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private DeezerDatasyncConfig getTestConfig(int mockWebServerPort) {
    Config typeSafeConfig = ConfigFactory.load("test");
    DeezerDatasyncConfig config = ConfigBeanFactory.create(typeSafeConfig.getConfig(ROOT_CONFIG), DeezerDatasyncConfig.class);
    config.getDeezer().setUrl(String.format("http://localhost:%s", mockWebServerPort));
    return config;
  }

}
