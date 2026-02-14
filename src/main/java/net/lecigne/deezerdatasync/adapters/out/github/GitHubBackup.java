package net.lecigne.deezerdatasync.adapters.out.github;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GitHubBackup {
  private List<GitHubFile> gitHubFiles;
}
