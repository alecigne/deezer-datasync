package net.lecigne.deezerdatasync.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import net.lecigne.deezerdatasync.Fixtures;
import net.lecigne.deezerdatasync.application.ports.in.DeezerDatasync;
import net.lecigne.deezerdatasync.application.ports.out.DeezerClient;
import net.lecigne.deezerdatasync.application.ports.out.SyncDestination;
import net.lecigne.deezerdatasync.domain.common.DeezerData;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.common.SyncException;
import net.lecigne.deezerdatasync.domain.user.UserId;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@DisplayName("The synchronization service")
class DeezerDatasyncServiceTest {

  private final DeezerClient deezerClient = mock(DeezerClient.class);
  private final SyncDestination syncDestination = mock(SyncDestination.class);
  private final DeezerDatasync service = new DeezerDatasyncService(deezerClient, syncDestination);
  private final ArgumentCaptor<DeezerData> dataCaptor = ArgumentCaptor.forClass(DeezerData.class);

  @AfterEach
  void tearDown() {
    Mockito.reset(deezerClient, syncDestination);
  }

  @Test
  void should_sync_user_data() {
    // Given
    var userId = UserId.of(42);
    Playlist ambientPlaylist = Fixtures.ambientPlaylist();

    var expectedData = DeezerData.builder()
        .albums(Fixtures.albums())
        .artists(Fixtures.artists())
        .playlistInfos(List.of(Fixtures.ambientPlaylistInfo()))
        .playlists(List.of(ambientPlaylist))
        .build();

    given(deezerClient.getUserAlbums(userId)).willReturn(expectedData.albums());
    given(deezerClient.getUserArtists(userId)).willReturn(expectedData.artists());
    given(deezerClient.getUserPlaylists(userId)).willReturn(expectedData.playlistInfos());
    // This method should be called only with the ID from the playlist info above
    given(deezerClient.getPlaylist(PlaylistId.of(10616324822L))).willReturn(ambientPlaylist);

    // When
    service.syncUser(userId);

    // Then
    verify(syncDestination).sync(dataCaptor.capture());
    assertThat(dataCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedData);
  }

  @Test
  void should_sync_user_data_without_playlists() {
    // Given
    var userId = UserId.of(42);
    var expectedData = DeezerData.builder()
        .albums(List.of())
        .artists(List.of())
        .playlistInfos(List.of())
        .playlists(List.of())
        .build();

    // When
    service.syncUser(userId);

    // Then
    verify(syncDestination).sync(dataCaptor.capture());
    assertThat(dataCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedData);
    verify(deezerClient, never()).getPlaylist(any(PlaylistId.class));
  }

  @Test
  void should_fail_when_retrieval_from_deezer_fails() {
    // Given
    var userId = UserId.of(42);

    var error = new SyncException("boom");
    given(deezerClient.getUserAlbums(userId)).willThrow(error);

    // "When"
    ThrowingCallable call = () -> service.syncUser(userId);

    // Then
    assertThatThrownBy(call).isSameAs(error);
    verifyNoInteractions(syncDestination);
    verify(deezerClient, never()).getUserArtists(any(UserId.class));
    verify(deezerClient, never()).getUserPlaylists(any(UserId.class));
    verify(deezerClient, never()).getPlaylist(any(PlaylistId.class));
  }

}
