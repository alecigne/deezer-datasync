package net.lecigne.deezerdatasync.domain.album;

import java.time.Instant;
import lombok.Builder;

@Builder
public record Album(
    AlbumId albumId,
    String artist,
    String title,
    Instant creationTimeUtc) {
}
