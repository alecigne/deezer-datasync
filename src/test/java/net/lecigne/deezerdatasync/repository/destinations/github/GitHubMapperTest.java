package net.lecigne.deezerdatasync.repository.destinations.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.Playlist;
import net.lecigne.deezerdatasync.model.Playlist.PlaylistBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("The GitHub mapper")
class GitHubMapperTest {

  @Test
  void should_map_deezer_data_to_github_backup() {
    // Given
    DeezerData deezerData = Fixtures.getInput();
    GitHubBackup expectedBackup = Fixtures.getBackup();

    // When
    GitHubBackup actualBackup = new GitHubMapper().mapDataToBackup(deezerData);

    // Then
    assertThat(actualBackup).usingRecursiveComparison().isEqualTo(expectedBackup);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void should_create_clean_playlist_filenames(Playlist playlist, String path) {
    // Given
    var data = DeezerData.builder().playlists(List.of(playlist)).build();

    // When
    GitHubBackup actualBackup = new GitHubMapper().mapDataToBackup(data);

    // Then
    GitHubFile playlistFile = actualBackup.getGitHubFiles().get(3);
    assertThat(playlistFile.getPath()).isEqualTo(path);
  }

  public static Stream<Arguments> testData() {
    PlaylistBuilder<?, ?> p = Playlist.builder().deezerId(42);
    return Stream.of(
        arguments(p.title("Hip-Hop").build(), "playlists/42_hip_hop.json"),
        arguments(p.title("Funk and Soul").build(), "playlists/42_funk_and_soul.json"),
        arguments(p.title("Funk & Soul").build(), "playlists/42_funk_soul.json"),
        arguments(p.title("Downtempo / Nu Jazz").build(), "playlists/42_downtempo_nu_jazz.json"),
        arguments(p.title("Drone Zone [Study / Relax / Meditate]").build(), "playlists/42_drone_zone_study_relax_meditate.json")
    );
  }

}
