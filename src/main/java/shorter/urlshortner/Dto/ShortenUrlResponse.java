package shorter.urlshortner.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ShortenUrlResponse {
    private String shortUrl;
    private String message;

}
