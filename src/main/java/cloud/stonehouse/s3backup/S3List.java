package cloud.stonehouse.s3backup;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;

public class S3List {

    private S3Backup s3Backup;
    private ArrayList<String> backups;
    private String bucket;
    private String prefix;

    public S3List(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.backups = new ArrayList<>();
        this.bucket = s3Backup.getFileConfig().getString("bucket");
        this.prefix = s3Backup.getFileConfig().getString("prefix");
    }

    public ArrayList<String> list() {
        ObjectListing objectListing = s3Backup.getClient().listObjects(bucket, prefix);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            backups.add(os.getKey().replace(prefix, ""));
        }
        return backups;
    }
}
