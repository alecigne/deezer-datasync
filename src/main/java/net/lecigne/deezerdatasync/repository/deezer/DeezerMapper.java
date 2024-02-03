package net.lecigne.deezerdatasync.repository.deezer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.model.Album;
import net.lecigne.deezerdatasync.model.Artist;
import net.lecigne.deezerdatasync.model.Playlist;
import net.lecigne.deezerdatasync.model.PlaylistInfo;
import net.lecigne.deezerdatasync.model.Track;

@RequiredArgsConstructor
class DeezerMapper {

  private final DeezerDatasyncConfig config;

  List<Album> mapFromAlbumDtos(List<AlbumDto> albumDtos) {
    return albumDtos.stream().map(this::mapFromAlbumDto).toList();
  }

  private Album mapFromAlbumDto(AlbumDto albumDto) {
    return Album.builder()
        .deezerId(albumDto.id())
        .artist(albumDto.artist().name())
        .title(albumDto.title())
        .creationTimeUtc(albumDto.timeAdd())
        .build();
  }

  List<Artist> mapFromArtistsDtos(List<ArtistDto> favoriteArtistDtos) {
    return favoriteArtistDtos.stream().map(this::mapArtistDto).toList();
  }

  private Artist mapArtistDto(ArtistDto favoriteArtistDto) {
    return Artist.builder()
        .deezerId(favoriteArtistDto.id())
        .name(favoriteArtistDto.name())
        .creationTimeUtc(favoriteArtistDto.timeAdd())
        .build();
  }

  List<PlaylistInfo> mapFromPlaylistDtos(List<PlaylistInfoDto> playlistInfoDtos) {
    return playlistInfoDtos.stream().map(this::mapFromPlaylistDto).toList();
  }

  PlaylistInfo mapFromPlaylistDto(PlaylistInfoDto playlistInfoDto) {
    return PlaylistInfo.builder()
        .deezerId(playlistInfoDto.id())
        .title(playlistInfoDto.title())
        .duration(Duration.ofSeconds(playlistInfoDto.durationInSeconds()))
        .nbTracks(playlistInfoDto.nbTracks())
        .fans(playlistInfoDto.fans())
        .creationTimeUtc(getInstant(playlistInfoDto.creationDate()))
        .build();
  }

  PlaylistWithTracksDto mapFromIndividualPlaylistDto(PlaylistDto playlistDto, List<TrackDto> tracks) {
    return PlaylistWithTracksDto.builder()
        .id(playlistDto.id())
        .title(playlistDto.title())
        .description(playlistDto.description())
        .durationInSeconds(playlistDto.durationInSeconds())
        .nbTracks(playlistDto.nbTracks())
        .fans(playlistDto.fans())
        .creationDate(playlistDto.creationDate())
        .tracks(tracks)
        .build();
  }

  Playlist mapFromPlaylistWithTracksDto(PlaylistWithTracksDto playlistWithTracksDto) {
    return Playlist.builder()
        .deezerId(playlistWithTracksDto.id())
        .title(playlistWithTracksDto.title())
        .description(playlistWithTracksDto.description())
        .duration(Duration.ofSeconds(playlistWithTracksDto.durationInSeconds()))
        .nbTracks(playlistWithTracksDto.nbTracks())
        .fans(playlistWithTracksDto.fans())
        .creationTimeUtc(getInstant(playlistWithTracksDto.creationDate()))
        .tracks(mapFromTrackDtos(playlistWithTracksDto.tracks()))
        .build();
  }

  private Instant getInstant(String date) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.parse(date, dateTimeFormatter)
        .atZone(ZoneId.of(config.getApplication().getZone()))
        .toInstant();
  }

  public List<Track> mapFromTrackDtos(List<TrackDto> trackDto) {
    return trackDto.stream().map(this::mapFromTrackDto).toList();
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

}
