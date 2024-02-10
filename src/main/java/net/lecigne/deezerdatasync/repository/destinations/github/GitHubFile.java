package net.lecigne.deezerdatasync.repository.destinations.github;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

@Getter
public class GitHubFile {
  private final String path;
  private final String content;

  GitHubFile(String path, String content) {
    this.path = path;
    this.content = content;
  }

  public static GitHubFileBuilder builder() {
    return new GitHubFileBuilder();
  }

  public static class GitHubFileBuilder {
    private String path;
    private String content;

    GitHubFileBuilder() {
    }

    public GitHubFileBuilder path(String path) {
      this.path = path;
      return this;
    }

    public <T> GitHubFileBuilder content(T content) {
      this.content = getJson(content);
      return this;
    }

    public GitHubFileBuilder rawContent(String rawContent) {
      this.content = rawContent;
      return this;
    }

    public GitHubFile build() {
      return new GitHubFile(this.path, this.content);
    }

    private <T> String getJson(T element) {
      try {
        return OBJECT_MAPPER.writeValueAsString(element);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

  }

}
