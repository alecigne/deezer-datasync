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
      GHRepository repo = getRepo();
      String latestCommitSha = getLatestCommitSha(repo);
      log.debug("Latest commit SHA: {}", latestCommitSha);
      GHTree newTree = createNewTree(repo, deezerData, latestCommitSha);
      GHCommit commit = createCommit(repo, newTree, latestCommitSha);
      log.debug("New Commit SHA: {}", commit.getSHA1());
      updateBranch(repo, commit);
    } catch (IOException e) {
      log.error("Error while saving to GitHub", e);
      throw new RuntimeException(e);
    }
  }

  private GHRepository getRepo() throws IOException {
    GitHub github = GitHubBuilder.fromEnvironment()
        .withConnector(new OkHttpConnector(new OkHttpClient()))
        .withOAuthToken(config.getGithub().getToken())
        .build();
    return github.getRepository(config.getGithub().getRepo());
  }

  private String getLatestCommitSha(GHRepository repo) throws IOException {
    String branch = String.format("heads/%s", config.getGithub().getBranch());
    return repo.getRef(branch).getObject().getSha();
  }

  private GHTree createNewTree(GHRepository repo, DeezerData deezerData, String latestCommitSha) throws IOException {
    GHCommit latestCommit = repo.getCommit(latestCommitSha);
    GHTreeBuilder treeBuilder = repo.createTree().baseTree(latestCommit.getTree().getSha());
    GitHubBackup gitHubBackup = mapper.mapDataToBackup(deezerData);
    gitHubBackup.getGitHubFiles().forEach(file -> treeBuilder.add(file.getPath(), file.getContent().getBytes(), false));
    return treeBuilder.create();
  }

  private GHCommit createCommit(GHRepository repo, GHTree newTree, String latestCommitSha) throws IOException {
    String commitMessage = String.format("Backup %s", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    return repo.createCommit()
        .message(commitMessage)
        .tree(newTree.getSha())
        .parent(latestCommitSha)
        .create();
  }

  private void updateBranch(GHRepository repo, GHCommit commit) throws IOException {
    String branch = String.format("heads/%s", config.getGithub().getBranch());
    repo.getRef(branch).updateTo(commit.getSHA1(), false);
  }

  public static GitHubSyncDestination init(DeezerDatasyncConfig config) {
    return new GitHubSyncDestination(config, new GitHubMapper());
  }

}
