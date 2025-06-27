package shorter.urlshortner.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUrlRequest {
    private String shortCode;
    private LocalDateTime newExpiryAt;
}
