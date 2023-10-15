package net.lecigne.deezerdatasync.model;

import java.time.Instant;
import lombok.Builder;

@Builder
public record Artist(
    long deezerId,
    String name,
    Instant creationTimeUtc) {
}
