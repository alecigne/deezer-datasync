package net.lecigne.deezerdatasync.adapters.out.github;

import java.time.Duration;
import java.time.Instant;
import lombok.Getter;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;

@Getter
public class GitHubPlaylistInfo {
  private final long deezerId;
  private final String title;
  private final Duration duration;
  private final int nbTracks;
  private final long fans;
  private final Instant creationTimeUtc;

  public GitHubPlaylistInfo(
      long deezerId,
      String title,
      Duration duration,
      int nbTracks,
      long fans,
      Instant creationTimeUtc) {
    this.deezerId = deezerId;
    this.title = title;
    this.duration = duration;
    this.nbTracks = nbTracks;
    this.fans = fans;
    this.creationTimeUtc = creationTimeUtc;
  }

  static GitHubPlaylistInfo fromPlaylistInfo(PlaylistInfo playlistInfo) {
    return new GitHubPlaylistInfo(
        playlistInfo.getPlaylistId().id(),
        playlistInfo.getTitle(),
        playlistInfo.getDuration(),
        playlistInfo.getNbTracks(),
        playlistInfo.getFans(),
        playlistInfo.getCreationTimeUtc());
  }

}
