package net.lecigne.deezerdatasync.adapters.in.cli;

import net.lecigne.deezerdatasync.application.ports.in.DeezerDatasync;
import net.lecigne.deezerdatasync.domain.user.UserId;

public class CLI {

  private final DeezerDatasync deezerDatasync;

  public CLI(DeezerDatasync deezerDatasync) {
    this.deezerDatasync = deezerDatasync;
  }

  public void run(long userId) {
    deezerDatasync.syncUser(UserId.of(userId));
  }

  public static CLI init(DeezerDatasync deezerDatasync) {
    return new CLI(deezerDatasync);
  }

}
