package net.lecigne.deezerdatasync.domain.playlist;

import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.lecigne.deezerdatasync.domain.track.Track;

@SuperBuilder
@Getter
public class Playlist extends PlaylistInfo {
  private final String description;
  private final List<Track> tracks;
}
