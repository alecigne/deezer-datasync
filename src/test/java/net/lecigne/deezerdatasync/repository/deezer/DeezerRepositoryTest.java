package net.lecigne.deezerdatasync.repository.deezer;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.List;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Application;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.Deezer.Profile;
import net.lecigne.deezerdatasync.model.DeezerData;
import net.lecigne.deezerdatasync.model.PlaylistInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("The Deezer repository")
class DeezerRepositoryTest {

  @Test
  void should_backup_all_found_playlists_when_no_ids_in_conf() throws IOException {
    // Given
    var config = getConfig();
    var mapper = new DeezerMapper(config);

    // No playlists to backup in configuration
    var profile = new Profile();
    profile.setPlaylistIds(emptyList());

    // The client will return two playlist infos that will be used for the backup
    var client = Mockito.mock(DeezerLoopingClient.class);
    given(client.getAlbums(anyString())).willReturn(emptyList());
    given(client.getArtists(anyString())).willReturn(emptyList());

    var rootInfo = PlaylistInfoDto.builder().creationDate("2024-01-01 01:00:00");
    List<PlaylistInfoDto> playlists = List.of(rootInfo.id(1).build(), rootInfo.id(2).build());
    given(client.getPlaylists(any())).willReturn(playlists);

    var rootPlaylist = PlaylistWithTracksDto.builder().creationDate("2024-01-01 01:00:00").tracks(emptyList());
    var playlist1 = rootPlaylist.title("Playlist 1").build();
    var playlist2 = rootPlaylist.title("Playlist 2").build();
    given(client.getPlaylist("1")).willReturn(playlist1);
    given(client.getPlaylist("2")).willReturn(playlist2);

    var deezerRepository = new DeezerRepository(client, mapper);

    // When
    DeezerData data = deezerRepository.fetch(profile);

    // Then
    assertThat(data.playlists())
        .hasSize(2)
        .extracting(PlaylistInfo::getTitle)
        .containsExactlyInAnyOrder(playlist1.title(), playlist2.title());
  }

  @NotNull
  private static DeezerDatasyncConfig getConfig() {
    var config = new DeezerDatasyncConfig();
    var application = new Application();
    application.setZone("Europe/Paris");
    config.setApplication(application);
    return config;
  }

}
