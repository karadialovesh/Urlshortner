package shorter.urlshortner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shorter.urlshortner.Dto.CachedShortUrl;
import shorter.urlshortner.Dto.ShortenUrlRequest;
import shorter.urlshortner.Dto.ShortenUrlResponse;
import shorter.urlshortner.Dto.UpdateUrlRequest;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.service.UrlService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                return userDetails.getUsername();
            }
            return principal.toString(); // If stored as string (e.g., in JWT)
        }
        throw new RuntimeException("Unauthorized");
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenUrlResponse> createShortUrl(
            @RequestBody ShortenUrlRequest request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(urlService.shortenUrl(request, username));
    }

    @GetMapping("/u/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
//        String original = urlService.redirectToOriginal(shortCode);//redis
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create(original))
//                .build();   //redis
        CachedShortUrl url = urlService.getOriginalUrlFromCache(shortCode);

        if (url.expiryAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This URL has expired");
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.originalUrl()))
                .build();
    }

    @PutMapping("/update-expiry")
    public ResponseEntity<String> updateUrlExpiry(@RequestBody UpdateUrlRequest request) {
        String username = getCurrentUsername();
        urlService.updateExpiry(username, request);
        return ResponseEntity.ok("Expiry updated successfully");
    }

    @GetMapping("/api/my-urls")
    public ResponseEntity<List<ShortUrl>> getMyUrls() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(urlService.getUserUrls(username));
    }
}
