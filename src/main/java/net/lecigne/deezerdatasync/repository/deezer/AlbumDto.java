package net.lecigne.deezerdatasync.repository.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

record AlbumDto(
    long id,
    String title,
    ArtistLightDto artist,
    @JsonProperty("time_add")
    Instant timeAdd) {
}
