package net.lecigne.deezerdatasync.adapters.out.deezer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.application.ports.out.DeezerClient;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig.DeezerConfig;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.common.SyncException;
import net.lecigne.deezerdatasync.domain.user.UserId;
import net.lecigne.deezerdatasync.utils.DeezerDatasyncUtils;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
public class DeezerRestClient implements DeezerClient {

  private final DeezerConfig deezerConfig;
  private final DeezerApi deezerApi;
  private final DeezerMapper mapper;

  DeezerRestClient(DeezerConfig deezerConfig, DeezerApi deezerApi, DeezerMapper mapper) {
    this.deezerConfig = deezerConfig;
    this.deezerApi = deezerApi;
    this.mapper = mapper;
  }

  @Override
  public List<Album> getUserAlbums(UserId userId) {
    IntFunction<Call<? extends DataContainer<AlbumDto>>> callFn = index -> deezerApi.getAlbums(userId.id(), index);
    return executeCallLoop(callFn, "Albums").stream().map(mapper::mapDtoToAlbum).toList();
  }

  @Override
  public List<Artist> getUserArtists(UserId userId) {
    IntFunction<Call<? extends DataContainer<ArtistDto>>> callFn = index -> deezerApi.getArtists(userId.id(), index);
    return executeCallLoop(callFn, "Artists").stream().map(mapper::mapDtoToArtist).toList();
  }

  @Override
  public List<PlaylistInfo> getUserPlaylists(UserId userId) {
    IntFunction<Call<? extends DataContainer<PlaylistInfoDto>>> callFn = index -> deezerApi.getPlaylists(userId.id(), index);
    return executeCallLoop(callFn, "Playlists").stream().map(mapper::mapDtoToPlaylistInfo).toList();
  }

  @Override
  public Playlist getPlaylist(PlaylistId playlistId) {
    // A call to playlist info is NOT wrapped in a container. Only its tracks are!
    PlaylistDto firstPage = executeCall(deezerApi.getPlaylist(playlistId.id(), 0));
    IntFunction<Call<? extends DataContainer<TrackDto>>> callFn = index -> deezerApi.getPlaylist(playlistId.id(), index);
    List<TrackDto> tracks = executeCallLoop(callFn, String.format("Playlist %s", playlistId.id()));
    return mapper.mapDtosToPlaylist(firstPage, tracks);
  }

  /**
   * This method calls a route once to get the total number of elements, then performs a loop of
   * calls to retrieve all the elements, respecting the maximum number of results per call.
   * <p>
   * Everything is rate-limited at the client level.
   */
  private <T> List<T> executeCallLoop(IntFunction<Call<? extends DataContainer<T>>> callFn, String description) {
    int limit = deezerConfig.getMaxResults();
    if (limit <= 0) {
      throw new SyncException("Invalid Deezer max-results configuration: " + limit);
    }
    int offset = 0;
    DataContainer<T> firstResponse = executeCall(callFn.apply(offset));
    int total = firstResponse.getTotal();
    int remainingCalls = DeezerDatasyncUtils.computeRemainingCalls(total, limit);
    log.debug("{} -> {} elements to get, {} call(s) in total", description, total, remainingCalls + 1);
    List<T> allData = new ArrayList<>(safeData(firstResponse, offset));
    for (int i = 0; i < remainingCalls; i++) {
      offset += limit;
      log.trace("Another call from index {}...", offset);
      DataContainer<T> response = executeCall(callFn.apply(offset));
      allData.addAll(safeData(response, offset));
    }
    return allData;
  }

  private static <T> List<T> safeData(DataContainer<T> container, int index) {
    List<T> data = container.getData();
    if (data == null) {
      log.warn("Received null data list from Deezer for index {}, defaulting to empty list", index);
      return Collections.emptyList();
    }
    return data;
  }

  private static <T> T executeCall(Call<? extends T> call) {
    Response<? extends T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new SyncException("Deezer API call failed", e);
    }
    if (!response.isSuccessful()) {
      throw new SyncException(String.format("Deezer API call failed with HTTP %d (%s)", response.code(), response.message()));
    }
    T body = response.body();
    if (body == null) throw new SyncException("Deezer API call returned an empty body");
    return body;
  }

  public static DeezerRestClient init(DeezerDatasyncConfig config) {
    var deezerApi = DeezerApi.init(config.getDeezerConfig());
    var mapper = new DeezerMapper(config);
    return new DeezerRestClient(config.getDeezerConfig(), deezerApi, mapper);
  }

}
