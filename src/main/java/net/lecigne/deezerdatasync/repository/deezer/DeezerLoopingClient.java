package net.lecigne.deezerdatasync.repository.deezer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Deezer;
import net.lecigne.deezerdatasync.utils.DeezerDatasyncUtils;
import retrofit2.Call;

@Slf4j
public class DeezerLoopingClient {

  private final Deezer deezer;
  private final DeezerApi deezerApi;
  private final DeezerMapper mapper;

  DeezerLoopingClient(Deezer deezer, DeezerApi deezerApi, DeezerMapper mapper) {
    this.deezer = deezer;
    this.deezerApi = deezerApi;
    this.mapper = mapper;
  }

  List<AlbumDto> getAlbums(String userId) throws IOException {
    return applyCallWithLimit(index -> deezerApi.getAlbums(userId, index));
  }

  List<ArtistDto> getArtists(String userId) throws IOException {
    return applyCallWithLimit(index -> deezerApi.getArtists(userId, index));
  }

  List<PlaylistInfoDto> getPlaylists(String userId) throws IOException {
    return applyCallWithLimit(index -> deezerApi.getPlaylists(userId, index));
  }

  PlaylistWithTracksDto getPlaylist(String playlistId) throws IOException {
    log.debug("Initial call to get info about playlist {}", playlistId);
    PlaylistDto individualPlaylist = deezerApi.getPlaylist(playlistId, 0).execute().body();
    List<TrackDto> tracks = applyCallWithLimit(index -> deezerApi.getPlaylist(playlistId, index));
    return mapper.mapFromIndividualPlaylistDto(individualPlaylist, tracks);
  }

  /**
   * This method calls a route once to obtain the total number of elements, then performs a loop of calls to retrieve
   * all the elements, respecting the maximum number of results per call.
   */
  private <T> List<T> applyCallWithLimit(IntFunction<Call<? extends DataContainer<T>>> call) throws IOException {
    int index = 0;
    DataContainer<T> firstResponse = call.apply(index).execute().body();
    int limit = deezer.getMaxResults();
    int total = firstResponse.getTotal();
    int remainingCalls = DeezerDatasyncUtils.computeRemainingCalls(total, limit);
    log.debug("{} elements to get, {} call(s) in total", total, remainingCalls + 1);
    List<T> allData = new ArrayList<>(firstResponse.getData());
    for (int i = 0; i < remainingCalls; i++) {
      index += limit;
      log.trace("Another call from index {}...", index);
      DataContainer<T> response = call.apply(index).execute().body();
      allData.addAll(response.getData());
    }
    return allData;
  }

  public static DeezerLoopingClient init(DeezerDatasyncConfig config) {
    var deezerApi = DeezerApi.init(config.getDeezer());
    var mapper = new DeezerMapper(config);
    return new DeezerLoopingClient(config.getDeezer(), deezerApi, mapper);
  }

}
