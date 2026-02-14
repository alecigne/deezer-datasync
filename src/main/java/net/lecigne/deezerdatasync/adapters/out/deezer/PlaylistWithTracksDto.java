package net.lecigne.deezerdatasync.adapters.out.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
record PlaylistWithTracksDto(
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
    List<TrackDto> tracks
) {
}
