package net.lecigne.deezerdatasync.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.typesafe.config.ConfigFactory;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig.DeezerConfig;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The configuration loader")
class DeezerDatasyncConfigLoaderTest {

  @Test
  void should_use_defaults_when_optional_deezer_values_are_missing() {
    // Given
    String rawConfig = """
        config {
          application {
            userId = 42
            zone = Europe/Paris
          }
          deezer {
            url = "https://api.deezer.com"
            token = "token"
          }
          github {
            token = "token"
            repo = "owner/repo"
            branch = "main"
          }
        }
        """;

    // When
    DeezerDatasyncConfig cfg = DeezerDatasyncConfigLoader.fromTypeSafeConfig(ConfigFactory.parseString(rawConfig));

    // Then
    assertThat(cfg.getDeezerConfig())
        .extracting(DeezerConfig::getMaxResults, DeezerConfig::getRateLimit)
        .containsExactly(200, 5);
  }

  @Test
  void should_keep_deezer_limits_when_optional_values_are_out_of_range() {
    // Given
    String rawConfig = """
        config {
          application {
            userId = 42
            zone = Europe/Paris
          }
          deezer {
            url = "https://api.deezer.com"
            token = "token"
            maxResults = 20
            rateLimit = 50
          }
          github {
            token = "token"
            repo = "owner/repo"
            branch = "main"
          }
        }
        """;

    // When
    DeezerDatasyncConfig cfg = DeezerDatasyncConfigLoader.fromTypeSafeConfig(ConfigFactory.parseString(rawConfig));

    // Then
    assertThat(cfg.getDeezerConfig())
        .extracting(DeezerConfig::getMaxResults, DeezerConfig::getRateLimit)
        .containsExactly(100, 10);
  }

  @Test
  void should_fail_when_required_configuration_is_missing() {
    // Given
    String rawConfig = """
        config {
          application {
            userId = 42
            zone = Europe/Paris
          }
          deezer {
            url = "https://api.deezer.com"
          }
          github {
            token = "token"
            repo = "owner/repo"
          }
        }
        """;

    // "When"
    ThrowingCallable call = () -> DeezerDatasyncConfigLoader.fromTypeSafeConfig(ConfigFactory.parseString(rawConfig));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("config.deezer.token")
        .hasMessageContaining("config.github.branch");
  }

  @Test
  void should_fail_when_configuration_values_are_blank() {
    // Given
    String rawConfig = """
        config {
          application {
            userId = ""
            zone = ""
          }
          deezer {
            url = ""
            token = ""
          }
          github {
            token = ""
            repo = ""
            branch = ""
          }
        }
        """;

    // "When"
    ThrowingCallable call = () -> DeezerDatasyncConfigLoader.fromTypeSafeConfig(ConfigFactory.parseString(rawConfig));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User ID must be a valid number")
        .hasMessageContaining("Timezone is invalid")
        .hasMessageContaining("Invalid value for Deezer URL")
        .hasMessageContaining("Deezer token must not be blank")
        .hasMessageContaining("GitHub token must not be blank")
        .hasMessageContaining("GitHub repo should be in the 'owner/repo' format")
        .hasMessageContaining("Repository branch must not be blank");
  }

  @Test
  void should_fail_when_specific_configuration_values_are_invalid() {
    // Given
    String rawConfig = """
        config {
          application {
            userId = "hello world"
            zone = "Nope/Nope"
          }
          deezer {
            url = "not-a-url"
            token = "token-is-ok"
          }
          github {
            token = "token-is-ok"
            repo = "not-a-repo"
            branch = "branch-is-ok"
          }
        }
        """;

    // "When"
    ThrowingCallable call = () -> DeezerDatasyncConfigLoader.fromTypeSafeConfig(ConfigFactory.parseString(rawConfig));

    // Then
    assertThatThrownBy(call)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("User ID must be a valid number")
        .hasMessageContaining("Timezone is invalid")
        .hasMessageContaining("Invalid value for Deezer URL")
        .hasMessageContaining("GitHub repo should be in the 'owner/repo' format");
  }

}
