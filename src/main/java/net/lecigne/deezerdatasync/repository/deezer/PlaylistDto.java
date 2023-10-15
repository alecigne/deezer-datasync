package net.lecigne.deezerdatasync.repository.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

record PlaylistDto(
    long id,
    String title,
    String description,
    @JsonProperty("duration")
    long durationInSeconds,
    @JsonProperty("nb_tracks")
    int nbTracks,
    long fans,
    @JsonProperty("creation_date")
    String creationDate,
    DeezerWrapper<TrackDto> tracks
) implements DataContainer<TrackDto> {

  @Override
  public List<TrackDto> getData() {
    return tracks.getData();
  }

  @Override
  public int getTotal() {
    return nbTracks;
  }

}
