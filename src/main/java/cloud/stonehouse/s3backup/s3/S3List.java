package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.TreeMap;

public class S3List {

    private final S3Backup s3Backup;

    public S3List(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    public TreeMap<Date, S3ObjectSummary> list(Player player, boolean list, Integer limit) {
        String prefix = s3Backup.getFileConfig().getPrefix();
        TreeMap<Date, S3ObjectSummary> backups = new TreeMap<>();
        ListObjectsV2Request request = new ListObjectsV2Request().
                withBucketName(s3Backup.getFileConfig().getBucket()).
                withPrefix(prefix);

        try {
            ListObjectsV2Result result;

            do {
                result = s3Backup.getClient().listObjectsV2(request);

                for (S3ObjectSummary os : result.getObjectSummaries()) {
                    backups.put(os.getLastModified(), os);
                }
                String token = result.getNextContinuationToken();
                request.setContinuationToken(token);
            } while (result.isTruncated());

            if (list) {
                int count = 0;
                int total = 0;
                long totalBackupSize = 0L;

                for (S3ObjectSummary backup : backups.values()) {
                    String name = backup.getKey().replace(prefix, "");
                    String size = s3Backup.convertBytes(backup.getSize(), false);

                    if (limit > 0) {
                        if (count >= backups.size() - limit) {
                            s3Backup.sendMessage(player, name + " (" + size + ")");
                            totalBackupSize += backup.getSize();
                            total++;

                        }
                    } else {
                        s3Backup.sendMessage(player, name + " (" + size + ")");
                        totalBackupSize += backup.getSize();
                        total++;

                    }
                    count++;
                }
                s3Backup.sendMessage(player, "Total space used by " + total + " backups: " +
                        s3Backup.convertBytes(totalBackupSize, false));
            }
            return backups;
        } catch (Exception e) {
            s3Backup.exception(player, "Error retrieving backup list", e);
        }
        return backups;
    }
}
