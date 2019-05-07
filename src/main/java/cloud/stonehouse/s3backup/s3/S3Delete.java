package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import org.bukkit.entity.Player;

public class S3Delete {

    private final S3Backup s3Backup;

    public S3Delete(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    public void delete(Player player, String backup) {
        try {
            s3Backup.getClient().deleteObject(s3Backup.getFileConfig().getBucket(), s3Backup.getFileConfig()
                    .getPrefix() + backup);
            s3Backup.sendMessage(player, true, "Backup " + backup + " has been deleted.");
        } catch (Exception e) {
            s3Backup.sendMessage(player, false, "Backup delete failed: " + e.getLocalizedMessage());
            s3Backup.exception(e);
        }
    }
}
