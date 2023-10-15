package net.lecigne.deezerdatasync.repository.destinations.github;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.repository.destinations.SyncDestination;
import okhttp3.OkHttpClient;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

@AllArgsConstructor
@Slf4j
public class GitHubSyncDestination implements SyncDestination {

  private final DeezerDatasyncConfig config;
  private final GitHubMapper mapper;

  @Override
  public void save(DeezerData deezerData) {
    try {
      GitHubBackup gitHubBackup = mapper.mapDataToBackup(deezerData);
      var commitMessage = String.format("Backup %s", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
      GitHub github = GitHubBuilder.fromEnvironment()
          .withConnector(new OkHttpConnector(new OkHttpClient()))
          .withOAuthToken(config.getGithub().getToken())
          .build();
      log.debug("Rate limit: {}", github.getRateLimit().getRemaining());
      GHRepository repo = github.getRepository(config.getGithub().getRepo());
      String branch = String.format("heads/%s", config.getGithub().getBranch());
      String latestCommitSha = repo.getRef(branch).getObject().getSha();
      log.debug("Latest commit SHA: {}", latestCommitSha);
      GHCommit latestCommit = repo.getCommit(latestCommitSha);
      String baseTreeSha = latestCommit.getTree().getSha();
      GHTreeBuilder treeBuilder = repo.createTree().baseTree(baseTreeSha);
      gitHubBackup.getGitHubFiles().forEach(file -> treeBuilder.add(file.getPath(), file.getContent().getBytes(), false));
      GHTree newTree = treeBuilder.create();
      GHCommit commit = repo.createCommit()
          .message(commitMessage)
          .tree(newTree.getSha())
          .parent(latestCommitSha)
          .create();
      log.debug("New Commit SHA: {}", commit.getSHA1());
      repo.getRef(branch).updateTo(commit.getSHA1(), false);
    } catch (IOException e) {
      log.error("Error while saving to GitHub", e);
      throw new RuntimeException(e);
    }
  }

  public static GitHubSyncDestination init(DeezerDatasyncConfig config) {
    return new GitHubSyncDestination(config, new GitHubMapper());
  }

}
