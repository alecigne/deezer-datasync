package net.lecigne.deezerdatasync.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  private final long deezerId;
  private final String title;
  private final Duration duration;
  private final int nbTracks;
  private final long fans;
  // TODO Returned as a String in the DTO -- is it UTC?!
  private final Instant creationTimeUtc;
}
