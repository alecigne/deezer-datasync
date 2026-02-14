package net.lecigne.deezerdatasync.adapters.out.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

record ArtistDto(
    long id,
    String name,
    @JsonProperty("time_add")
    Instant timeAdd) {
}
