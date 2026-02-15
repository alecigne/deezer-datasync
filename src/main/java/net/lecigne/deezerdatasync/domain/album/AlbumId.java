package net.lecigne.deezerdatasync.domain.album;

import net.lecigne.deezerdatasync.domain.common.DeezerId;

public record AlbumId(DeezerId deezerId) {

  public long id() {
    return deezerId.value();
  }

  public static AlbumId of(long value) {
    return new AlbumId(new DeezerId(value));
  }

}
