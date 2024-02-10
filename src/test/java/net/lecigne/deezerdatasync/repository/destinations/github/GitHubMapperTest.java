package net.lecigne.deezerdatasync.repository.destinations.github;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.model.DeezerData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

}
