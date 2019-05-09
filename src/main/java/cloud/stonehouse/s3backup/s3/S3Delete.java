package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import org.bukkit.entity.Player;

public class S3Delete {

    private final S3Backup s3Backup;

    public S3Delete(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    public void delete(Player player, String backup) {
        String filePrefix = s3Backup.getFileConfig().getPrefix() + backup;
        try {
            if (s3Backup.backupExists(filePrefix)) {
                s3Backup.getClient().deleteObject(s3Backup.getFileConfig().getBucket(), filePrefix);
                s3Backup.sendMessage(player, "Backup " + backup + " has been deleted");
            } else {
                s3Backup.sendMessage(player, "Backup " + backup + " does not exist");
            }
        } catch (Exception e) {
            s3Backup.exception(player, "Backup delete failed", e);
        }
    }
}
