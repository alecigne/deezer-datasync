package net.lecigne.deezerdatasync.adapters.out.deezer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.domain.album.Album;
import net.lecigne.deezerdatasync.domain.album.AlbumId;
import net.lecigne.deezerdatasync.domain.artist.Artist;
import net.lecigne.deezerdatasync.domain.playlist.Playlist;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistId;
import net.lecigne.deezerdatasync.domain.playlist.PlaylistInfo;
import net.lecigne.deezerdatasync.domain.track.Track;

@RequiredArgsConstructor
class DeezerMapper {

  private final DeezerDatasyncConfig config;

  Album mapDtoToAlbum(AlbumDto albumDto) {
    return Album.builder()
        .albumId(AlbumId.of(albumDto.id()))
        .artist(albumDto.artist().name())
        .title(albumDto.title())
        .creationTimeUtc(albumDto.timeAdd())
        .build();
  }

  Artist mapDtoToArtist(ArtistDto artistDto) {
    return Artist.builder()
        .deezerId(artistDto.id())
        .name(artistDto.name())
        .creationTimeUtc(artistDto.timeAdd())
        .build();
  }

  PlaylistInfo mapDtoToPlaylistInfo(PlaylistInfoDto playlistInfoDto) {
    return PlaylistInfo.builder()
        .playlistId(PlaylistId.of(playlistInfoDto.id()))
        .title(playlistInfoDto.title())
        .duration(Duration.ofSeconds(playlistInfoDto.durationInSeconds()))
        .nbTracks(playlistInfoDto.nbTracks())
        .fans(playlistInfoDto.fans())
        .creationTimeUtc(getInstant(playlistInfoDto.creationDate()))
        .build();
  }

  Playlist mapDtosToPlaylist(PlaylistDto playlistDto, List<TrackDto> trackDtos) {
    return Playlist.builder()
        .playlistId(PlaylistId.of(playlistDto.id()))
        .title(playlistDto.title())
        .description(playlistDto.description())
        .duration(Duration.ofSeconds(playlistDto.durationInSeconds()))
        .nbTracks(playlistDto.nbTracks())
        .fans(playlistDto.fans())
        .creationTimeUtc(getInstant(playlistDto.creationDate()))
        .tracks(trackDtos.stream().map(this::mapFromTrackDto).toList())
        .build();
  }

  private Track mapFromTrackDto(TrackDto trackDto) {
    return Track.builder()
        .deezerId(trackDto.id())
        .artist(trackDto.artist().name())
        .title(trackDto.title())
        .album(trackDto.album().title())
        .creationTimeUtc(trackDto.timeAdd())
        .build();
  }

  private Instant getInstant(String date) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(date, dateTimeFormatter)
        .atZone(ZoneId.of(config.getAppConfig().getZone()))
        .toInstant();
  }


}
