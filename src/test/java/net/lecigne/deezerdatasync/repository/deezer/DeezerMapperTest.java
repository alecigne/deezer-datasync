package net.lecigne.deezerdatasync.repository.deezer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Application;
import net.lecigne.deezerdatasync.model.PlaylistInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Deezer mapper")
class DeezerMapperTest {

  private static final DeezerMapper MAPPER = new DeezerMapper(getConfig());

  // TODO
  @Test
  void should_map_playlist_dtos() {
    // Given
    List<PlaylistInfoDto> playlistInfoDtos = List.of(
        PlaylistInfoDto.builder().creationDate("2022-11-01 07:52:55").build()
    );
    Instant expectedInstant = Instant.parse("2022-10-31T22:52:55Z");

    // When
    List<PlaylistInfo> playlistInfos = MAPPER.mapFromPlaylistDtos(playlistInfoDtos);

    // Then
    assertThat(playlistInfos)
        .extracting(PlaylistInfo::getCreationTimeUtc)
        .element(0)
        .isEqualTo(expectedInstant);
  }

  private static DeezerDatasyncConfig getConfig() {
    var config = new DeezerDatasyncConfig();
    var applicationConfig = new Application();
    applicationConfig.setZone("Asia/Tokyo");
    config.setApplication(applicationConfig);
    return config;
  }

}
