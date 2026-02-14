package net.lecigne.deezerdatasync.domain.user;

import net.lecigne.deezerdatasync.domain.common.DeezerId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserId(DeezerId deezerId) {

  public long id() {
    return deezerId.value();
  }

  public static UserId of(long value) {
    return new UserId(new DeezerId(value));
  }

}
