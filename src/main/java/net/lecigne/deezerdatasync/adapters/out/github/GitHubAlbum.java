package net.lecigne.deezerdatasync.adapters.out.github;

import java.time.Instant;
import lombok.Getter;
import net.lecigne.deezerdatasync.domain.album.Album;

@Getter
public class GitHubAlbum {
  long deezerId;
  String artist;
  String title;
  Instant creationTimeUtc;

  public GitHubAlbum(long deezerId, String artist, String title, Instant creationTimeUtc) {
    this.deezerId = deezerId;
    this.artist = artist;
    this.title = title;
    this.creationTimeUtc = creationTimeUtc;
  }

  public static GitHubAlbum fromAlbum(Album album) {
    return new GitHubAlbum(
        album.albumId().id(),
        album.artist(),
        album.title(),
        album.creationTimeUtc());
  }

}
