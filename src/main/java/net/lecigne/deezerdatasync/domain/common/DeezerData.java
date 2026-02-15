package net.lecigne.deezerdatasync.domain.common;

import java.util.List;
import lombok.Builder;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.artist.Artist;

@Builder
public record DeezerData(
    List<Album> albums,
    List<Artist> artists,
    List<PlaylistInfo> playlistInfos,
    List<Playlist> playlists) {

  public long nbAlbums() {
    return albums.size();
  }

  public long nbArtists() {
    return artists.size();
  }

  public long nbPlaylistsInfo() {
    return playlistInfos.size();
  }

  public long nbPlaylists() {
    return playlists.size();
  }

}
