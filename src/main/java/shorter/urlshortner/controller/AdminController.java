package shorter.urlshortner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.repository.ShortUrlRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ShortUrlRepository shortUrlRepo;

    // ✅ 1. Get all short URLs
    @GetMapping("/urls")
    public ResponseEntity<List<ShortUrl>> getAllUrls() {
        return ResponseEntity.ok(shortUrlRepo.findAll());
    }

    // ✅ 2. Delete short URL by ID
    @DeleteMapping("/url/{id}")
    public ResponseEntity<String> deleteUrl(@PathVariable Long id) {
        if (!shortUrlRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
        }
        shortUrlRepo.deleteById(id);
        return ResponseEntity.ok("Short URL deleted successfully.");
    }

    // ✅ 3. Get stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<ShortUrl> all = shortUrlRepo.findAll();
        int totalUrls = all.size();
        int totalClicks = all.stream().mapToInt(ShortUrl::getClickCount).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUrls", totalUrls);
        stats.put("totalClicks", totalClicks);

        return ResponseEntity.ok(stats);
    }
}
