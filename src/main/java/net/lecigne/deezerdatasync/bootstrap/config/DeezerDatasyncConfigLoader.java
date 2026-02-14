package net.lecigne.deezerdatasync.bootstrap.config;

import com.google.common.primitives.Ints;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.net.URI;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class DeezerDatasyncConfigLoader {

  private static final String ROOT_CONFIG = "config";

  private static final List<String> REQUIRED_PATHS = List.of(
      "application.userId",
      "application.zone",
      "deezer.url",
      "deezer.token",
      "github.token",
      "github.repo",
      "github.branch");
  private static final Pattern REPO_PATTERN = Pattern.compile("^[^/\\s]+/[^/\\s]+$");

  // Results per request
  private static final String MAX_RESULTS_PROPERTY = "deezer.maxResults";
  private static final int DEFAULT_RESULTS = 200;
  private static final int MIN_RESULTS = 100;

  // Rate limiting
  private static final String RATE_LIMIT_PROPERTY = "deezer.rateLimit";
  private static final int DEFAULT_RATE = 5;
  private static final int MIN_RATE = 1;
  private static final int MAX_RATE = 10;

  private DeezerDatasyncConfigLoader() {
  }

  public static DeezerDatasyncConfig load() {
    return fromTypeSafeConfig(ConfigFactory.load());
  }

  public static DeezerDatasyncConfig load(String resourceBasename) {
    return fromTypeSafeConfig(ConfigFactory.load(resourceBasename));
  }

  static DeezerDatasyncConfig fromTypeSafeConfig(Config typeSafeConfig) {
    Config normalized = typeSafeConfig.getConfig(ROOT_CONFIG);
    validateRequiredPaths(normalized);
    normalized = handleMaxResults(normalized);
    normalized = handleRateLimit(normalized);
    DeezerDatasyncConfig config = ConfigBeanFactory.create(normalized, DeezerDatasyncConfig.class);
    validateValues(config);
    return config;
  }

  /**
   * Fill optional "maxResults" property with default value, or normalize it if already provided.
   */
  private static Config handleMaxResults(Config normalized) {
    if (!normalized.hasPath(MAX_RESULTS_PROPERTY)) {
      normalized = normalized.withValue(MAX_RESULTS_PROPERTY, ConfigValueFactory.fromAnyRef(DEFAULT_RESULTS));
    } else {
      int providedMaxResults = normalized.getInt(MAX_RESULTS_PROPERTY);
      int constrainedMaxResults = Math.max(providedMaxResults, MIN_RESULTS);
      normalized = normalized.withValue(MAX_RESULTS_PROPERTY, ConfigValueFactory.fromAnyRef(constrainedMaxResults));
    }
    return normalized;
  }

  /**
   * Fill optional "rateLimit" with default value, or normalize it if already provided.
   */
  private static Config handleRateLimit(Config normalized) {
    if (!normalized.hasPath(RATE_LIMIT_PROPERTY)) {
      normalized = normalized.withValue(RATE_LIMIT_PROPERTY, ConfigValueFactory.fromAnyRef(DEFAULT_RATE));
    } else {
      int providedRateLimit = normalized.getInt(RATE_LIMIT_PROPERTY);
      int constrainedRateLimit = Ints.constrainToRange(providedRateLimit, MIN_RATE, MAX_RATE);
      normalized = normalized.withValue(RATE_LIMIT_PROPERTY, ConfigValueFactory.fromAnyRef(constrainedRateLimit));
    }
    return normalized;
  }

  private static void validateRequiredPaths(Config config) {
    List<String> errors = new ArrayList<>();
    for (String path : REQUIRED_PATHS) {
      if (!config.hasPath(path)) {
        errors.add("Missing required configuration: " + ROOT_CONFIG + "." + path);
      }
    }
    throwIfErrors(errors);
  }

  private static void validateValues(DeezerDatasyncConfig config) {
    List<String> list = Stream.of(
        validateUserId(config.getAppConfig().getUserId()),
        validateZone(config.getAppConfig().getZone()),
        validateDeezerUrl(config.getDeezerConfig().getUrl()),
        validateNotBlank(config.getDeezerConfig().getToken(), "Deezer token"),
        validateNotBlank(config.getGithubConfig().getToken(), "GitHub token"),
        validateRepo(config.getGithubConfig().getRepo()),
        validateNotBlank(config.getGithubConfig().getBranch(), "Repository branch")
    ).flatMap(Optional::stream).toList();
    throwIfErrors(list);
  }

  /**
   * User ID must be a valid number.
   */
  private static Optional<String> validateUserId(String userId) {
    var err = "User ID must be a valid number";
    try {
      long l = Long.parseLong(userId);
      if (l <= 0) return Optional.of(err);
    } catch (NumberFormatException e) {
      return Optional.of(err);
    }
    return Optional.empty();
  }

  /**
   * Zone must be a valid time zone ID.
   */
  private static Optional<String> validateZone(String value) {
    try {
      //noinspection ResultOfMethodCallIgnored
      ZoneId.of(value);
    } catch (DateTimeException e) {
      return Optional.of("Timezone is invalid");
    }
    return Optional.empty();
  }

  private static Optional<String> validateDeezerUrl(String value) {
    var err = "Invalid value for Deezer URL";
    try {
      URI uri = URI.create(value);
      return (uri.getScheme() == null || uri.getHost() == null)
          ? Optional.of(err)
          : Optional.empty();
    } catch (IllegalArgumentException e) {
      return Optional.of(err);
    }
  }

  private static Optional<String> validateRepo(String value) {
    if (!REPO_PATTERN.matcher(value).matches()) {
      return Optional.of("GitHub repo should be in the 'owner/repo' format");
    }
    return Optional.empty();
  }

  private static Optional<String> validateNotBlank(String value, String parameter) {
    if (value == null || value.isBlank()) {
      var err = String.format("%s must not be blank", parameter);
      return Optional.of(err);
    }
    return Optional.empty();
  }

  private static void throwIfErrors(List<String> errors) {
    if (errors.isEmpty()) return;
    String err = "Invalid configuration:\n - " + String.join("\n - ", errors);
    throw new IllegalArgumentException(err);
  }

}
