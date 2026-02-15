package net.lecigne.deezerdatasync.application.ports.out;

import net.lecigne.deezerdatasync.domain.common.DeezerData;

public interface SyncDestination {
  void sync(DeezerData deezerData);
}
