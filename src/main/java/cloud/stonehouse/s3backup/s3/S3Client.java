package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Client {

    private AmazonS3 client;
    private final S3Backup s3Backup;

    public S3Client(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.client = buildClient();
    }

    private AmazonS3 buildClient() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    s3Backup.getFileConfig().getAccessKeyId(),
                    s3Backup.getFileConfig().getAccessKeySecret()
            );

            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(s3Backup.getFileConfig().getRegion())
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