package shorter.urlshortner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    List<ShortUrl> findByUser(User user);
    Optional<ShortUrl> findByUserAndOriginalUrl(User user, String originalUrl);
    @Transactional
    @Modifying
    @Query("DELETE FROM ShortUrl s WHERE s.expiryAt < :cutoff")
    int deleteExpiredAndAgedUrls(@Param("cutoff") LocalDateTime cutoff);
    @Transactional
    @Modifying
    @Query("DELETE FROM ShortUrl s WHERE s.expiryAt < :cutoff")
    int deleteByExpiryAtBefore(@Param("cutoff") LocalDateTime cutoff);

}
