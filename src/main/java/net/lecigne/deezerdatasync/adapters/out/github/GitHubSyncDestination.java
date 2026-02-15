package net.lecigne.deezerdatasync.adapters.out.github;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.application.ports.out.SyncDestination;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.common.SyncException;
import okhttp3.OkHttpClient;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

@Slf4j
public class GitHubSyncDestination implements SyncDestination {

  private final DeezerDatasyncConfig config;
  private final GitHubMapper mapper;

  GitHubSyncDestination(DeezerDatasyncConfig config, GitHubMapper mapper) {
    this.config = config;
    this.mapper = mapper;
  }

  @Override
  public void sync(DeezerData deezerData) {
    try {
      GHRepository repo = getRepo();
      String latestCommitSha = getLatestCommitSha(repo);
      log.debug("Latest commit SHA: {}", latestCommitSha);
      GHTree newTree = createNewTree(repo, deezerData, latestCommitSha);
      if (isSameTree(newTree, repo.getCommit(latestCommitSha).getTree())) {
        log.debug("No changes detected, skipping commit.");
        return;
      }
      GHCommit commit = createCommit(repo, newTree, latestCommitSha);
      log.debug("New Commit SHA: {}", commit.getSHA1());
      updateBranch(repo, commit);
    } catch (IOException e) {
      var err = "Error while saving to GitHub";
      log.error(err, e);
      throw new SyncException(err, e);
    }
  }

  private GHRepository getRepo() throws IOException {
    GitHub github = GitHubBuilder.fromEnvironment()
        .withConnector(new OkHttpConnector(new OkHttpClient()))
        .withOAuthToken(config.getGithubConfig().getToken())
        .build();
    return github.getRepository(config.getGithubConfig().getRepo());
  }

  private String getLatestCommitSha(GHRepository repo) throws IOException {
    String branch = String.format("heads/%s", config.getGithubConfig().getBranch());
    return repo.getRef(branch).getObject().getSha();
  }

  private GHTree createNewTree(GHRepository repo, DeezerData deezerData, String latestCommitSha) throws IOException {
    GHCommit latestCommit = repo.getCommit(latestCommitSha);
    GHTreeBuilder treeBuilder = repo.createTree().baseTree(latestCommit.getTree().getSha());
    GitHubBackup gitHubBackup = mapper.mapDataToBackup(deezerData);
    gitHubBackup.getGitHubFiles().forEach(file -> treeBuilder.add(file.getPath(), file.getContent().getBytes(), false));
    return treeBuilder.create();
  }

  private boolean isSameTree(GHTree newTree, GHTree oldTree) {
    return newTree.getSha().equals(oldTree.getSha());
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
    String branch = String.format("heads/%s", config.getGithubConfig().getBranch());
    repo.getRef(branch).updateTo(commit.getSHA1(), false);
  }

  public static GitHubSyncDestination init(DeezerDatasyncConfig config) {
    return new GitHubSyncDestination(config, new GitHubMapper());
  }

}
