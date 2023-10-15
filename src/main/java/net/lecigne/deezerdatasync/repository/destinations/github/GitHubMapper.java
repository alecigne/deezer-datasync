package net.lecigne.deezerdatasync.repository.destinations.github;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.Playlist;

class GitHubMapper {

  GitHubBackup mapDataToBackup(DeezerData deezerData) throws JsonProcessingException {
    List<GitHubFile> files = new ArrayList<>();
    String albumsJson = OBJECT_MAPPER.writeValueAsString(deezerData.albums());
    GitHubFile albums = GitHubFile.builder().path("albums.json").content(albumsJson).build();
    String artistsJson = OBJECT_MAPPER.writeValueAsString(deezerData.artists());
    GitHubFile artists = GitHubFile.builder().path("artists.json").content(artistsJson).build();
    String playlistInfosJson = OBJECT_MAPPER.writeValueAsString(deezerData.playlistInfos());
    GitHubFile playlistInfos = GitHubFile.builder().path("playlists.json").content(playlistInfosJson).build();
    List<GitHubFile> playlists = new ArrayList<>();
    for (Playlist playlist : deezerData.playlists()) {
      String json = OBJECT_MAPPER.writeValueAsString(playlist);
      playlists.add(GitHubFile.builder()
          .path(String.format("playlists/%s.json", playlist.getDeezerId()))
          .content(json)
          .build());
    }
    files.add(albums);
    files.add(artists);
    files.add(playlistInfos);
    files.addAll(playlists);
    return GitHubBackup.builder().gitHubFiles(files).build();
  }

}
