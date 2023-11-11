package net.lecigne.deezerdatasync.config;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DeezerDatasyncConfig {

  public static final ObjectMapper OBJECT_MAPPER; // Thread-safe if config doesn't change
  public static final String ROOT_CONFIG = "config";

  static {
    DefaultPrettyPrinter prettyPrinter = new CustomPrettyPrinter();
    OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .setSerializationInclusion(Include.NON_NULL)
        .setDefaultPrettyPrinter(prettyPrinter);
  }

  private Application application;
  private Deezer deezer;
  private GitHub github;

  @NoArgsConstructor
  @Getter
  @Setter
  public static class Application {
    private String zone;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class Deezer {
    private Profile profile;
    private String url;
    private String token;
    private int maxResults;
    private int rateLimit;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Profile {
      private String userId;
      private List<String> playlistIds;

      @Override
      public String toString() {
        return String.format("[userId = %s, playlistIds = %s]", userId, playlistIds);
      }
    }
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class GitHub {
    private String token;
    private String repo;
    private String branch;
  }

}
