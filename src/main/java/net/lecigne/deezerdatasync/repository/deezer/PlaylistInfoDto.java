package net.lecigne.deezerdatasync.repository.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
record PlaylistInfoDto(
    long id,
    String title,
    @JsonProperty("duration")
    long durationInSeconds,
    @JsonProperty("nb_tracks")
    int nbTracks,
    long fans,
    // TODO Returned as a String -- is it UTC?!
    @JsonProperty("creation_date")
    String creationDate) {
}
