package net.lecigne.deezerdatasync.repository.destinations.github;

import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import net.lecigne.deezerdatasync.model.DeezerData;

@Getter
@Builder
public class GitHubBackup {
  private List<GitHubFile> gitHubFiles;
}
