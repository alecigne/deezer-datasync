package net.lecigne.deezerdatasync.domain.playlist;

import java.time.Duration;
import java.time.Instant;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Playlist info, without tracks.
 */
@SuperBuilder
@Getter
public class PlaylistInfo {
  private final PlaylistId playlistId;
  private final String title;
  private final Duration duration;
  private final int nbTracks;
  private final long fans;
  // TODO Returned as a String in the DTO -- is it UTC?!
  private final Instant creationTimeUtc;
}
