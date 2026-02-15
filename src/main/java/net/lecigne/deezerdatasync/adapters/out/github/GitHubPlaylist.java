package net.lecigne.deezerdatasync.adapters.out.github;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.track.Track;

@Getter
public class GitHubPlaylist extends GitHubPlaylistInfo {
  private final String description;
  private final List<Track> tracks;

  public GitHubPlaylist(long deezerId, String title, Duration duration, int nbTracks, long fans,
      Instant creationTimeUtc, String description, List<Track> tracks) {
    super(deezerId, title, duration, nbTracks, fans, creationTimeUtc);
    this.description = description;
    this.tracks = tracks;
  }

  public static GitHubPlaylist fromPlaylistAndTracks(Playlist playlist, List<Track> tracks) {
    return new GitHubPlaylist(
        playlist.getPlaylistId().id(),
        playlist.getTitle(),
        playlist.getDuration(),
        playlist.getNbTracks(),
        playlist.getFans(),
        playlist.getCreationTimeUtc(),
        playlist.getDescription(),
        tracks);
  }

}
