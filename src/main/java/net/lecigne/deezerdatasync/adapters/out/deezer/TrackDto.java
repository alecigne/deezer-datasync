package net.lecigne.deezerdatasync.adapters.out.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

record TrackDto(
    long id,
    String title,
    @JsonProperty("time_add")
    Instant timeAdd,
    ArtistLightDto artist,
    AlbumLightDto album) {
}
