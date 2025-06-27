package shorter.urlshortner.Dto;

import java.time.LocalDateTime;

public record CachedShortUrl(
    String shortCode,
    String originalUrl,
    LocalDateTime createdAt,
    LocalDateTime expiryAt,
    int clickCount,
    String ownerUsername
) {}
