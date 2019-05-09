package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class S3List {

    private final S3Backup s3Backup;

    public S3List(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    public ArrayList<String> list(Player player, boolean list) {
        String prefix = s3Backup.getFileConfig().getPrefix();
        ArrayList<String> backups = new ArrayList<>();

        try {
            ObjectListing objectListing = s3Backup.getClient().listObjects(s3Backup.getFileConfig().getBucket(),
                    prefix);
            for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
                String backup = os.getKey().replace(prefix, "");
                backups.add(backup);
                if (list) {
                    s3Backup.sendMessage(player, backup);
                }
            }
            return backups;
        } catch (Exception e) {
            s3Backup.exception(player, "Error retrieving backup list", e);
        }
        return backups;
    }
}
