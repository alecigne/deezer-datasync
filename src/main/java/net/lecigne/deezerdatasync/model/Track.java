package net.lecigne.deezerdatasync.model;

import java.time.Instant;
import lombok.Builder;

@Builder
public record Track(
    long deezerId,
    String artist,
    String title,
    String album,
    Instant creationTimeUtc) {
}
