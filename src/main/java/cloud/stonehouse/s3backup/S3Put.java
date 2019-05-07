package cloud.stonehouse.s3backup;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;

public class S3Put {

    private String archiveName;
    private String bucket;
    private AmazonS3 client;
    private String localPrefix;
    private String prefix;

    public S3Put(S3Backup s3backup, String archiveName) {
        this.archiveName = archiveName;
        this.bucket = s3backup.getFileConfig().getString("bucket");
        this.client = s3backup.getClient();
        this.localPrefix = s3backup.getFileConfig().getString("local-prefix");
        this.prefix = s3backup.getFileConfig().getString("prefix");
    }

    public PutObjectResult upload() {
        return client.putObject(bucket,
                prefix + archiveName,
                new File(localPrefix + File.separator + archiveName));
    }
}
