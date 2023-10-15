package net.lecigne.deezerdatasync.model;

import java.util.List;
import lombok.Builder;

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

  public long nbPlaylists() {
    return playlistInfos.size();
  }

}
