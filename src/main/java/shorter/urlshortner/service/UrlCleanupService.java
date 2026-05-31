package shorter.urlshortner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shorter.urlshortner.repository.ShortUrlRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlCleanupService {

    private final ShortUrlRepository shortUrlRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteOldExpiredUrls() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(1);
        int deletedCount = shortUrlRepository.deleteExpiredAndAgedUrls(cutoff);
        System.out.println("🧹 Deleted " + deletedCount + " expired URLs older than 1 year");
    }
}
