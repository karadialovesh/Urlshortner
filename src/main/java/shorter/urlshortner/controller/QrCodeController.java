package shorter.urlshortner.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorter.urlshortner.model.ShortUrl;
import shorter.urlshortner.repository.ShortUrlRepository;
import shorter.urlshortner.util.QrCodeGenerator;

import java.util.Optional;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrCodeController {

    private final ShortUrlRepository shortUrlRepository;

    @GetMapping("/{shortCode}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String shortCode) throws WriterException {
        Optional<ShortUrl> optional = shortUrlRepository.findByShortCode(shortCode);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String fullUrl = "http://localhost:8080/u/" + shortCode; // or your domain
        byte[] qrImage = QrCodeGenerator.generateQrCode(fullUrl, 250, 250);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + shortCode + ".png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
}
