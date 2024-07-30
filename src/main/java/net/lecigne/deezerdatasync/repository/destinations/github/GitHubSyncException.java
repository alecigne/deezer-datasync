package net.lecigne.deezerdatasync.repository.destinations.github;

public class GitHubSyncException extends RuntimeException {
  public GitHubSyncException(String message, Throwable cause) {
    super(message, cause);
  }
}
