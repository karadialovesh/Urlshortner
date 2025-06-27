package shorter.urlshortner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import shorter.urlshortner.Dto.CachedShortUrl;
import shorter.urlshortner.Dto.ShortenUrlRequest;
import shorter.urlshortner.Dto.ShortenUrlResponse;
import shorter.urlshortner.Dto.UpdateUrlRequest;
import shorter.urlshortner.exception.CustomShortCodeAlreadyExistsException;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.model.User;
import shorter.urlshortner.repository.ShortUrlRepository;
import shorter.urlshortner.repository.UserRepository;
import shorter.urlshortner.util.ReservedWords;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final ShortUrlRepository shortUrlRepo;
    private final UserRepository userRepo;

    @Cacheable(value = "shortUrls", key = "#shortCode")
    public CachedShortUrl getOriginalUrlFromCache(String shortCode) {
        ShortUrl entity = shortUrlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short code not found"));

        return new CachedShortUrl(
                entity.getShortCode(),
                entity.getOriginalUrl(),
                entity.getCreatedAt(),
                entity.getExpiryAt(),
                entity.getClickCount(),
                entity.getOwnerUsername()
        );
    }



    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request, String username) {
        User user = userRepo.findByUsername(username).orElseThrow();

        // ✅ Check for duplicate (same user, same original URL)
        Optional<ShortUrl> existing = shortUrlRepo.findByUserAndOriginalUrl(user, request.getOriginalUrl());
        if (existing.isPresent()) {
            return new ShortenUrlResponse(
                    "http://localhost:8080/u/" + existing.get().getShortCode(),
                    "This URL was already shortened. Returning the existing short link."
            );
        }

        // ✅ Handle custom code if provided
        String shortCode;
        if (request.getCustomCode() != null && !request.getCustomCode().isBlank()) {
            String customCode = request.getCustomCode().trim().toLowerCase();

            if (ReservedWords.isReserved(customCode)) {
                throw new RuntimeException("This custom short code is reserved. Please choose something else.");
            }

            if (shortUrlRepo.findByShortCode(customCode).isPresent()) {
                throw new CustomShortCodeAlreadyExistsException("Custom short code is already in use.");
            }

            shortCode = customCode;
        }else {
            shortCode = UUID.randomUUID().toString().substring(0, 6);
        }

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusDays(7))
                .clickCount(0)
                .user(user)
                .ownerUsername(user.getUsername())
                .build();

        shortUrlRepo.save(shortUrl);

        return new ShortenUrlResponse("http://localhost:8080/u/" + shortCode,
                request.getCustomCode() != null ? "Custom short URL created successfully."
                        : "Short URL created successfully.");
    }



    public String redirectToOriginal(String shortCode) {
        ShortUrl shortUrl = shortUrlRepo.findByShortCode(shortCode)
            .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (shortUrl.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This URL has expired");
        }

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepo.save(shortUrl);

        return shortUrl.getOriginalUrl();
    }
    @CacheEvict(value = "shortUrls", key = "#request.shortCode")
    public void updateExpiry(String username, UpdateUrlRequest request) {
        ShortUrl url = shortUrlRepo.findByShortCode(request.getShortCode())
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        // ✅ Check ownership
        if (!url.getOwnerUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this URL");
        }

        // ✅ Update expiry
        url.setExpiryAt(request.getNewExpiryAt());
        shortUrlRepo.save(url);

    }


    public List<ShortUrl> getUserUrls(String username) {

        User user = userRepo.findByUsername(username).orElseThrow();
        return shortUrlRepo.findByUser(user);
    }
}
