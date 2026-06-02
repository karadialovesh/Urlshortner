package shorter.urlshortner.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.repository.ShortUrlRepository;
import shorter.urlshortner.service.S3Service;
import shorter.urlshortner.util.QrCodeGenerator;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrCodeController {

    private final ShortUrlRepository shortUrlRepository;
    private final S3Service s3Service;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Map<String, String>> getQrCode(
            @PathVariable String shortCode)
            throws WriterException {

        Optional<ShortUrl> optional =
                shortUrlRepository.findByShortCode(shortCode);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (s3Service.qrExists(shortCode)) {

            return ResponseEntity.ok(
                    Map.of(
                            "shortCode", shortCode,
                            "qrUrl", s3Service.getQrUrl(shortCode)
                    ));
        }

        String fullUrl =
                "http://localhost:8080/u/" + shortCode;

        byte[] qrImage =
                QrCodeGenerator.generateQrCode(
                        fullUrl,
                        250,
                        250);

        String s3Url =
                s3Service.uploadQrCode(
                        qrImage,
                        shortCode);

        return ResponseEntity.ok(
                Map.of(
                        "shortCode", shortCode,
                        "qrUrl", s3Url
                ));
    }
}
