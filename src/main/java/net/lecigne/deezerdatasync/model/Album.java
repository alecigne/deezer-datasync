package net.lecigne.deezerdatasync.model;

import java.time.Instant;
import lombok.Builder;

@Builder
public record Album(
    long deezerId,
    String artist,
    String title,
    Instant creationTimeUtc) {
}
