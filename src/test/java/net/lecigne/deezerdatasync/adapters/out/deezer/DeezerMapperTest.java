package net.lecigne.deezerdatasync.adapters.out.deezer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig.AppConfig;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.album.AlbumId;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.track.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Deezer mapper")
class DeezerMapperTest {

  private static final DeezerMapper MAPPER = new DeezerMapper(getConfig());

  @Test
  void should_map_album_dto() {
    // Given
    var albumDto = new AlbumDto(
        42L,
        "Temple Of The Melting Dawn",
        new ArtistLightDto("Steve Roach"),
        Instant.parse("2023-10-21T04:52:14Z")
    );
    var expectedAlbum = Album.builder()
        .albumId(AlbumId.of(42L))
        .artist("Steve Roach")
        .title("Temple Of The Melting Dawn")
        .creationTimeUtc(Instant.parse("2023-10-21T04:52:14Z"))
        .build();

    // When
    Album actualAlbum = MAPPER.mapDtoToAlbum(albumDto);

    // Then
    assertThat(actualAlbum).isEqualTo(expectedAlbum);
  }

  @Test
  void should_map_artist_dto() {
    // Given
    var artistDto = new ArtistDto(
        84L,
        "Susumu Yokota",
        Instant.parse("2023-09-23T06:45:48Z")
    );
    var expectedArtist = Artist.builder()
        .deezerId(84L)
        .name("Susumu Yokota")
        .creationTimeUtc(Instant.parse("2023-09-23T06:45:48Z"))
        .build();

    // When
    Artist actualArtist = MAPPER.mapDtoToArtist(artistDto);

    // Then
    assertThat(actualArtist).isEqualTo(expectedArtist);
  }

  @Test
  void should_map_playlist_info_dto() {
    // Given
    var playlistInfoDto = PlaylistInfoDto.builder()
        .id(1327063625L)
        .title("Acid House / Acid Techno")
        .durationInSeconds(3807)
        .nbTracks(11)
        .fans(1)
        .creationDate("2022-11-01 07:52:55")
        .build();
    var expectedPlaylistInfo = PlaylistInfo.builder()
        .playlistId(PlaylistId.of(1327063625L))
        .title("Acid House / Acid Techno")
        .duration(Duration.ofSeconds(3807))
        .nbTracks(11)
        .fans(1)
        .creationTimeUtc(Instant.parse("2022-10-31T22:52:55Z"))
        .build();

    // When
    PlaylistInfo actualPlaylistInfo = MAPPER.mapDtoToPlaylistInfo(playlistInfoDto);

    // Then
    assertThat(actualPlaylistInfo).usingRecursiveComparison().isEqualTo(expectedPlaylistInfo);
  }

  @Test
  void should_map_playlist_and_track_dtos() {
    // Given
    var playlistDto = PlaylistDto.builder()
        .id(10616324822L)
        .title("Ambient")
        .description("My all-time ambient favorites")
        .durationInSeconds(296472)
        .nbTracks(2)
        .fans(1)
        .creationDate("2022-08-14 14:14:58")
        .build();
    List<TrackDto> trackDtos = List.of(
        new TrackDto(
            1324663722L,
            "Insouciant",
            Instant.parse("2022-08-14T12:16:03Z"),
            new ArtistLightDto("Innesti"),
            new AlbumLightDto("Filament and Place")
        ),
        new TrackDto(
            1308346072L,
            "Regenerative Shifts",
            Instant.parse("2022-08-14T12:16:06Z"),
            new ArtistLightDto("Igneous Flame"),
            new AlbumLightDto("Lapiz")
        )
    );
    var expectedPlaylist = Playlist.builder()
        .playlistId(PlaylistId.of(10616324822L))
        .title("Ambient")
        .description("My all-time ambient favorites")
        .duration(Duration.ofSeconds(296472))
        .nbTracks(2)
        .fans(1)
        .creationTimeUtc(Instant.parse("2022-08-14T05:14:58Z"))
        .tracks(List.of(
            Track.builder()
                .deezerId(1324663722L)
                .artist("Innesti")
                .title("Insouciant")
                .album("Filament and Place")
                .creationTimeUtc(Instant.parse("2022-08-14T12:16:03Z"))
                .build(),
            Track.builder()
                .deezerId(1308346072L)
                .artist("Igneous Flame")
                .title("Regenerative Shifts")
                .album("Lapiz")
                .creationTimeUtc(Instant.parse("2022-08-14T12:16:06Z"))
                .build()
        ))
        .build();

    // When
    Playlist actualPlaylist = MAPPER.mapDtosToPlaylist(playlistDto, trackDtos);

    // Then
    assertThat(actualPlaylist).usingRecursiveComparison().isEqualTo(expectedPlaylist);
  }

  private static DeezerDatasyncConfig getConfig() {
    var config = new DeezerDatasyncConfig();
    var applicationConfig = new AppConfig();
    applicationConfig.setZone("Asia/Tokyo");
    config.setApplication(applicationConfig);
    return config;
  }

}
