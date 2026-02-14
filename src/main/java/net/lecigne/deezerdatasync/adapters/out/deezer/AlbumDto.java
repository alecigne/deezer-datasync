package net.lecigne.deezerdatasync.adapters.out.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

record AlbumDto(
    long id,
    String title,
    ArtistLightDto artist,
    @JsonProperty("time_add")
    Instant timeAdd) {
}
