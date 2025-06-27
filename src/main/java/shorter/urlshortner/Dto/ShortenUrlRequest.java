package shorter.urlshortner.Dto;

import lombok.Data;

@Data
public class ShortenUrlRequest {
    private String originalUrl;
    private String customCode; // optional field
}
