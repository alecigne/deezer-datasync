package net.lecigne.deezerdatasync.bootstrap.config;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class DeezerDatasyncConfig {

  public static final ObjectMapper OBJECT_MAPPER; // Thread-safe if config doesn't change

  static {
    DefaultPrettyPrinter prettyPrinter = new CustomPrettyPrinter();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .setDefaultPropertyInclusion(Include.NON_NULL)
        .setDefaultPrettyPrinter(prettyPrinter);
  }

  private AppConfig appConfig;
  private DeezerConfig deezerConfig;
  private GitHubConfig githubConfig;

  @NoArgsConstructor
  @Getter
  @Setter
  public static class AppConfig {
    private String userId;
    private String zone;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class DeezerConfig {
    private String url;
    private String token;
    private int maxResults;
    private int rateLimit;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class GitHubConfig {
    private String token;
    private String repo;
    private String branch;
  }

  // Specific getters and setters for Typesafe Config

  public AppConfig getApplication() {
    return appConfig;
  }

  public void setApplication(AppConfig application) {
    this.appConfig = application;
  }

  public DeezerConfig getDeezer() {
    return deezerConfig;
  }

  public void setDeezer(DeezerConfig deezer) {
    this.deezerConfig = deezer;
  }

  public GitHubConfig getGithub() {
    return githubConfig;
  }

  public void setGithub(GitHubConfig github) {
    this.githubConfig = github;
  }

}
