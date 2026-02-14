package net.lecigne.deezerdatasync.domain.common;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record DeezerId(long value) {
  public DeezerId {
    if (value <= 0) throw new IllegalArgumentException("Deezer ID must be a valid number");
  }
}
