package net.lecigne.deezerdatasync.adapters.out.github;

import java.util.List;
import java.util.stream.Stream;
import net.lecigne.deezerdatasync.domain.common.DeezerData;

class GitHubMapper {

  GitHubBackup mapDataToBackup(DeezerData data) {
    List<GitHubFile> files = Stream.concat(
        Stream.of(
            GitHubFile.builder().path("albums.json").content(data.albums().stream().map(GitHubAlbum::fromAlbum).toList()).build(),
            GitHubFile.builder().path("artists.json").content(data.artists()).build(),
            GitHubFile.builder().path("playlists.json").content(data.playlistInfos().stream().map(GitHubPlaylistInfo::fromPlaylistInfo).toList()).build()),
        data.playlists().stream()
            .map(playlist -> GitHubPlaylist.fromPlaylistAndTracks(playlist, playlist.getTracks()))
            .map(ghPlaylist -> GitHubFile.builder()
                .path(playlistFilename(ghPlaylist))
                .content(ghPlaylist)
                .build())
    ).toList();
    return GitHubBackup.builder().gitHubFiles(files).build();
  }

  private static String playlistFilename(GitHubPlaylist playlist) {
    var title = playlist.getTitle().toLowerCase().trim();
    title = title.replaceAll("[^a-z0-9]+", "_").replaceAll("_+$", "");
    return String.format("playlists/%s_%s.json", playlist.getDeezerId(), title)
        .replaceAll("_+", "_");
  }


}
