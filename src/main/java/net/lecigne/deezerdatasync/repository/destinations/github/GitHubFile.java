package net.lecigne.deezerdatasync.repository.destinations.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubFile {
  private String path;
  private String content;
}
