package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Client {

    private final S3Backup s3Backup;
    private AmazonS3 client;

    public S3Client(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.client = buildClient();
    }

    private AmazonS3 buildClient() {
        try {
            String accessKeyId = s3Backup.getFileConfig().getAccessKeyId();
            String accessKeySecret = s3Backup.getFileConfig().getAccessKeySecret();

            if (accessKeyId.equals("") || accessKeySecret.equals("")) {
                client = AmazonS3ClientBuilder
                        .standard()
                        .withRegion(s3Backup.getFileConfig().getRegion())
                        .build();
            } else {
                AWSCredentials credentials = new BasicAWSCredentials(
                        accessKeyId,
                        accessKeySecret
                );

                String customEndpoint = s3Backup.getFileConfig().getCustomEndpoint();

                if (customEndpoint.equals("")) {
                    client = AmazonS3ClientBuilder
                            .standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(s3Backup.getFileConfig().getRegion())
                            .build();

                } else {
                    ClientConfiguration clientConfiguration = new ClientConfiguration();
                    clientConfiguration.setSignerOverride(s3Backup.getFileConfig().getSignerOverride());

                    client = AmazonS3ClientBuilder
                            .standard()
                            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(customEndpoint, s3Backup.getFileConfig().getRegion()))
                            .withPathStyleAccessEnabled(s3Backup.getFileConfig().getPathStyleAccess())
                            .withClientConfiguration(clientConfiguration)
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .build();
                }
            }
            return client;
        } catch (Exception e) {
            s3Backup.exception(null, "Failed to build s3 client", e);
            return null;
        }
    }

    public AmazonS3 getClient() {
        return client;
    }
}
