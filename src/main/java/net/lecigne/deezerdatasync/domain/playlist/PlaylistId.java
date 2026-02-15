package net.lecigne.deezerdatasync.domain.playlist;

import net.lecigne.deezerdatasync.domain.common.DeezerId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PlaylistId(DeezerId deezerId) {

  public long id() {
    return deezerId.value();
  }

  public static PlaylistId of(long value) {
    return new PlaylistId(new DeezerId(value));
  }

}
