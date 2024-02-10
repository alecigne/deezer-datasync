package net.lecigne.deezerdatasync.repository.destinations.github;

import java.util.List;
import java.util.stream.Stream;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.Playlist;

class GitHubMapper {

  public GitHubBackup mapDataToBackup(DeezerData data) {
    List<GitHubFile> files = Stream.concat(
        Stream.of(
            GitHubFile.builder().path("albums.json").content(data.albums()).build(),
            GitHubFile.builder().path("artists.json").content(data.artists()).build(),
            GitHubFile.builder().path("playlists.json").content(data.playlistInfos()).build()),
        data.playlists().stream().map(playlist -> GitHubFile.builder()
            .path(playlistFilename(playlist))
            .content(playlist)
            .build())
    ).toList();
    return GitHubBackup.builder().gitHubFiles(files).build();
  }

  private static String playlistFilename(Playlist playlist) {
    return String.format("playlists/%s.json", playlist.getDeezerId());
  }

}
