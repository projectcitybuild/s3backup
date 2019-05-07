package cloud.stonehouse.s3backup;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Client {

    private AmazonS3 client;
    private S3Backup s3Backup;

    public S3Client(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        String accessKeyId = s3Backup.getFileConfig().getString("access-key-id");
        String accessKeySecret = s3Backup.getFileConfig().getString("access-key-secret");
        String region = s3Backup.getFileConfig().getString("region");
        this.client = buildClient(accessKeyId, accessKeySecret, region);
    }

    private AmazonS3 buildClient(String accessKeyId, String accessKeySecret, String region) {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    accessKeyId,
                    accessKeySecret
            );

            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(region)
                    .build();

            return client;
        } catch (Exception e) {
            s3Backup.exception(e);
            return null;
        }
    }

    public AmazonS3 getClient() {
        return this.client;
    }
}
