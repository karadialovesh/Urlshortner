package shorter.urlshortner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    public String uploadQrCode(byte[] qrBytes, String shortCode) {
        String key = "qr/" + shortCode + ".png";

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .build(),
            RequestBody.fromBytes(qrBytes)
        );

        return "https://" + bucketName
             + ".s3." + region
             + ".amazonaws.com/" + key;
    }

    public boolean qrExists(String shortCode) {

        String key = "qr/" + shortCode + ".png";

        try {
            s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());

            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    public String getQrUrl(String shortCode) {

        String key = "qr/" + shortCode + ".png";

        return "https://"
                + bucketName
                + ".s3."
                + region
                + ".amazonaws.com/"
                + key;
    }
}