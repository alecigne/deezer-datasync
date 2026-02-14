package net.lecigne.deezerdatasync.adapters.out.github;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.regex.Pattern;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;

@Label("The GitHub mapper")
public class GitHubMapperPropertyTest {

  @Property(tries = 10000)
  void should_create_clean_playlist_filenames_from_edge_cases(@ForAll @StringLength(min = 1, max = 100) String title) {
    // Given
    var playlist = Playlist.builder().playlistId(PlaylistId.of(42)).title(title).build();
    var data = DeezerData.builder()
        .albums(List.of())
        .playlistInfos(List.of())
        .playlists(List.of(playlist)).build();

    // When
    GitHubBackup actualBackup = new GitHubMapper().mapDataToBackup(data);

    // Then
    GitHubFile playlistFile = actualBackup.getGitHubFiles().get(3);
    String actualPath = playlistFile.getPath();

    boolean isLowercase = actualPath.equals(actualPath.toLowerCase());
    boolean matchesPattern = Pattern.matches("playlists/42_[a-z0-9_]*\\.json", actualPath);
    boolean noConsecutiveUnderscores = !Pattern.matches(".*__.*", actualPath);
    assertThat(isLowercase)
        .as(String.format("%s should be lowercase", actualPath))
        .isTrue();
    assertThat(matchesPattern)
        .as(String.format("%s should contain only allowed characters", actualPath))
        .isTrue();
    assertThat(noConsecutiveUnderscores)
        .as(String.format("%s should not contained repeated underscores", actualPath))
        .isTrue();
  }

}
