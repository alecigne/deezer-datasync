package net.lecigne.deezerdatasync.application.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.application.ports.in.DeezerDatasync;
import net.lecigne.deezerdatasync.application.ports.out.DeezerClient;
import net.lecigne.deezerdatasync.application.ports.out.SyncDestination;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.user.UserId;

@RequiredArgsConstructor
@Slf4j
public class DeezerDatasyncService implements DeezerDatasync {

  private final DeezerClient deezerClient;
  private final SyncDestination syncDestination;

  @Override
  public void syncUser(UserId userId) {
    log.info("Retrieving data from Deezer for user {}", userId.id());
    List<Album> albums = deezerClient.getUserAlbums(userId);
    List<Artist> artists = deezerClient.getUserArtists(userId);
    List<PlaylistInfo> playlistInfos = deezerClient.getUserPlaylists(userId);
    List<Playlist> playlists = playlistInfos.stream().map(PlaylistInfo::getPlaylistId).map(deezerClient::getPlaylist).toList();
    var data = DeezerData.builder()
        .albums(albums)
        .artists(artists)
        .playlistInfos(playlistInfos)
        .playlists(playlists)
        .build();
    syncDestination.sync(data);
    var msg = "Saved {} albums, {} artists, {} playlists info, {} playlists";
    log.info(msg, data.nbAlbums(), data.nbArtists(), data.nbPlaylistsInfo(), data.nbPlaylists());
  }

  public static DeezerDatasync init(DeezerClient deezerRepo, SyncDestination syncDestination) {
    return new DeezerDatasyncService(deezerRepo, syncDestination);
  }

}
