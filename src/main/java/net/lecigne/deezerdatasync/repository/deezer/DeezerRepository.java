package net.lecigne.deezerdatasync.repository.deezer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Deezer.Profile;
import net.lecigne.deezerdatasync.model.Album;
import net.lecigne.deezerdatasync.model.Artist;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.Playlist;
import net.lecigne.deezerdatasync.model.PlaylistInfo;
import net.lecigne.deezerdatasync.utils.Extensions;

@RequiredArgsConstructor
@Slf4j
@ExtensionMethod(Extensions.class)
public class DeezerRepository {

  private final DeezerLoopingClient client;
  private final DeezerMapper mapper;

  public DeezerData fetch(Profile deezerProfile) {
    List<Album> albums;
    List<Artist> artists;
    List<PlaylistInfo> playlistInfos;
    List<Playlist> playlists = new ArrayList<>();
    try {
      log.debug("\n=== Albums ===");
      albums = getAlbums(deezerProfile.getUserId());
      log.debug("\n=== Artists ===");
      artists = getArtists(deezerProfile.getUserId());
      log.debug("\n=== Playlist infos ===");
      playlistInfos = getPlaylistInfos(deezerProfile.getUserId());
      log.debug("\n=== Playlists ===");
      List<String> ids = deezerProfile
          .getPlaylistIds()
          .ifEmpty(playlistInfos.stream().map(p -> String.valueOf(p.getDeezerId())).toList());
      for (String playlistId : ids) {
        Playlist playlist = getPlaylist(playlistId);
        playlists.add(playlist);
      }
    } catch (IOException e) {
      log.error("Error while fetching Deezer data", e);
      throw new RuntimeException(e);
    }
    return DeezerData.builder()
        .albums(albums)
        .artists(artists)
        .playlistInfos(playlistInfos)
        .playlists(playlists)
        .build();
  }

  private List<Album> getAlbums(String userId) throws IOException {
    List<AlbumDto> albums = client.getAlbums(userId);
    return mapper.mapFromAlbumDtos(albums);
  }

  private List<Artist> getArtists(String userId) throws IOException {
    List<ArtistDto> artists = client.getArtists(userId);
    return mapper.mapFromArtistsDtos(artists);
  }

  private List<PlaylistInfo> getPlaylistInfos(String userId) throws IOException {
    List<PlaylistInfoDto> playlists = client.getPlaylists(userId);
    return mapper.mapFromPlaylistDtos(playlists);
  }

  private Playlist getPlaylist(String playlistId) throws IOException {
    PlaylistWithTracksDto playlist = client.getPlaylist(playlistId);
    return mapper.mapFromPlaylistWithTracksDto(playlist);
  }

  public static DeezerRepository init(DeezerDatasyncConfig config) {
    var deezerLoopingClient = DeezerLoopingClient.init(config);
    var mapper = new DeezerMapper(config);
    return new DeezerRepository(deezerLoopingClient, mapper);
  }

}
