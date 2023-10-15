package net.lecigne.deezerdatasync.model;

import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class Playlist extends PlaylistInfo {
  private final String description;
  private final List<Track> tracks;
}
