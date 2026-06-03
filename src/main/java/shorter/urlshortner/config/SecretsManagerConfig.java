package shorter.urlshortner.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.Map;

public class SecretsManagerConfig
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(
                    GetSecretValueRequest.builder()
                            .secretId("urlshortner=1/prod")
                            .build()
            );

            Map<String, String> secrets = new ObjectMapper().readValue(
                    response.secretString(), new TypeReference<>() {}
            );
            System.setProperty("DB_URL",      secrets.get("db.url").trim());
            System.setProperty("DB_USERNAME", secrets.get("db.username").trim());
            System.setProperty("DB_PASSWORD", secrets.get("db.password").trim());
            System.setProperty("BASE_URL",  secrets.get("base.url").trim());
            System.setProperty("S3_BUCKET",   secrets.get("s3.bucket").trim());
            System.setProperty("REDIS_HOST",  secrets.get("redis.host").trim());
            System.out.println("✓ Secrets loaded from AWS Secrets Manager");

        } catch (Exception e) {
            System.out.println("✗ Secrets Manager failed: " + e.getMessage());
        }
    }
}