package net.lecigne.deezerdatasync.repository.destinations;

import net.lecigne.deezerdatasync.model.DeezerData;

public interface SyncDestination {
  void save(DeezerData data);
}
