package net.lecigne.deezerdatasync.repository;

import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.DeezerProfile.Profile;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.repository.deezer.DeezerRepository;
import net.lecigne.deezerdatasync.repository.destinations.SyncDestination;
import net.lecigne.deezerdatasync.repository.destinations.github.GitHubSyncDestination;

@Slf4j
public class DeezerDatasyncRepository {

  private final DeezerRepository deezerRepository;
  private final SyncDestination syncDestination;

  public DeezerDatasyncRepository(DeezerRepository deezerRepository, SyncDestination syncDestination) {
    this.deezerRepository = deezerRepository;
    this.syncDestination = syncDestination;
  }

  public DeezerData fetch(Profile deezerProfile) {
    log.debug("Fetching deezer data with the following profile: {}", deezerProfile);
    return deezerRepository.fetch(deezerProfile);
  }

  public void sync(DeezerData deezerData) {
    syncDestination.save(deezerData);
  }

  public static DeezerDatasyncRepository init(DeezerDatasyncConfig config) {
    // TODO Use the strategy pattern here for handling multiple destinations
    SyncDestination gitHubRepository = GitHubSyncDestination.init(config);
    DeezerRepository deezerRepository = DeezerRepository.init(config);
    return new DeezerDatasyncRepository(deezerRepository, gitHubRepository);
  }

}
