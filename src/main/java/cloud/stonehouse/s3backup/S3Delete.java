package cloud.stonehouse.s3backup;

import org.bukkit.entity.Player;

public class S3Delete {

    private S3Backup s3Backup;
    private String backup;
    private String bucket;
    private Player player;
    private String prefix;

    public S3Delete(S3Backup s3Backup, Player player, String backup) {
        this.s3Backup = s3Backup;
        this.backup = backup;
        this.bucket = s3Backup.getFileConfig().getString("bucket");
        this.player = player;
        this.prefix = s3Backup.getFileConfig().getString("prefix");
    }

    public void delete() {
        try {
            s3Backup.getClient().deleteObject(bucket, prefix + backup);
            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup " + backup + " has been deleted.");
            } else {
                s3Backup.getLogger().info("Backup " + backup + " has been deleted.");
            }
        } catch (Exception e) {
            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup delete failed: " + e.getLocalizedMessage());
            } else {
                s3Backup.exception(e);
            }
        }
    }
}
