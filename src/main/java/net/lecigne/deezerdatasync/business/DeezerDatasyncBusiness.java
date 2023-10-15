package net.lecigne.deezerdatasync.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.DeezerProfile.Profile;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.repository.DeezerDatasyncRepository;

@RequiredArgsConstructor
@Slf4j
public class DeezerDatasyncBusiness {

  private final DeezerDatasyncRepository repository;
  private final Profile deezerProfile;

  public void sync() {
    log.info("Retrieving data from Deezer...");
    DeezerData data = repository.fetch(deezerProfile);
    log.info("Got {} albums, {} artists, {} playlists", data.nbAlbums(), data.nbArtists(), data.nbPlaylists());
    log.info("Saving data...");
    repository.sync(data);
    log.info("Done!");
  }

  public static DeezerDatasyncBusiness init(DeezerDatasyncConfig config) {
    DeezerDatasyncRepository repository = DeezerDatasyncRepository.init(config);
    return new DeezerDatasyncBusiness(repository, config.getDeezer().getProfile());
  }

}
