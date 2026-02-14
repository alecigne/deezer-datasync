package net.lecigne.deezerdatasync.application.ports.out;

import java.util.List;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.user.UserId;

public interface DeezerClient {
  List<Album> getUserAlbums(UserId userId);
  List<Artist> getUserArtists(UserId userId);
  List<PlaylistInfo> getUserPlaylists(UserId userId);
  Playlist getPlaylist(PlaylistId playlistId);
}
