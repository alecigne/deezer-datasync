package net.lecigne.deezerdatasync.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Deezer.Profile;
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
    var infoMessage = "Got {} albums, {} artists, {} playlists info, {} playlists";
    log.info(infoMessage, data.nbAlbums(), data.nbArtists(), data.nbPlaylistsInfo(), data.nbPlaylists());
    log.info("Saving data...");
    repository.sync(data);
    log.info("Done!");
  }

  public static DeezerDatasyncBusiness init(DeezerDatasyncConfig config) {
    DeezerDatasyncRepository repository = DeezerDatasyncRepository.init(config);
    return new DeezerDatasyncBusiness(repository, config.getDeezer().getProfile());
  }

}
