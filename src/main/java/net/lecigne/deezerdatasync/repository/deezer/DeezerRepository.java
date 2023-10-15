package net.lecigne.deezerdatasync.repository.deezer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.DeezerProfile;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.DeezerProfile.Profile;
import net.lecigne.deezerdatasync.model.Album;
import net.lecigne.deezerdatasync.model.Artist;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.Playlist;
import net.lecigne.deezerdatasync.model.PlaylistInfo;
import net.lecigne.deezerdatasync.model.Track;
import net.lecigne.deezerdatasync.utils.DeezerDatasyncUtils;
import retrofit2.Call;

@RequiredArgsConstructor
@Slf4j
public class DeezerRepository {

  private final DeezerClient client;
  private final DeezerMapper mapper;
  private final DeezerProfile deezerProfile;

  public DeezerData fetch(Profile deezerProfile) {
    List<Album> albums;
    List<Artist> artists;
    List<PlaylistInfo> playlistInfos;
    Playlist playlist;
    try {
      albums = getAlbums(deezerProfile.getUserId());
      artists = getArtists(deezerProfile.getUserId());
      playlistInfos = getPlaylists(deezerProfile.getUserId());
      playlist = getPlaylist(deezerProfile.getPlaylistId());
    } catch (IOException e) {
      log.error("Error while fetching Deezer data", e);
      throw new RuntimeException(e);
    }
    return DeezerData.builder()
        .albums(albums)
        .artists(artists)
        .playlistInfos(playlistInfos)
        .playlists(List.of(playlist))
        .build();
  }

  private List<Album> getAlbums(String userId) throws IOException {
    return applyCallWithLimit(index -> client.getAlbums(userId, index), mapper::mapFromAlbumDtos);
  }

  private List<Artist> getArtists(String userId) throws IOException {
    return applyCallWithLimit(index -> client.getArtists(userId, index), mapper::mapFromFavoriteArtistsDtos);
  }

  private List<PlaylistInfo> getPlaylists(String userId) throws IOException {
    return applyCallWithLimit(index -> client.getPlaylists(userId, index), mapper::mapFromPlaylistDtos);
  }

  private Playlist getPlaylist(String playlistId) throws IOException {
    PlaylistDto individualPlaylist = client.getPlaylist(playlistId, 0).execute().body(); // for playlist info
    List<Track> tracks = applyCallWithLimit(index -> client.getPlaylist(playlistId, index), mapper::mapFromTrackDtos);
    return mapper.mapFromIndividualPlaylistDto(individualPlaylist, tracks);
  }

  private <T, U> List<U> applyCallWithLimit(
      IntFunction<Call<? extends DataContainer<T>>> call,
      Function<List<T>, List<U>> mapping
  ) throws IOException {
    int index = 0;
    DataContainer<T> firstResponse = call.apply(index).execute().body();
    int limit = deezerProfile.getLimit();
    int total = firstResponse.getTotal();
    int remainingCalls = DeezerDatasyncUtils.computeRemainingCalls(total, limit);
    log.debug("{} elements to get, {} other calls will be made", total, remainingCalls);
    List<T> allData = new ArrayList<>(firstResponse.getData());
    for (int i = 0; i < remainingCalls; i++) {
      index += limit;
      DataContainer<T> response = call.apply(index).execute().body();
      allData.addAll(response.getData());
    }
    return mapping.apply(allData);
  }

  public static DeezerRepository init(DeezerDatasyncConfig config) {
    var deezerClient = DeezerClient.init(config.getDeezer());
    var mapper = new DeezerMapper(config);
    return new DeezerRepository(deezerClient, mapper, config.getDeezer());
  }

}
