package cloud.stonehouse.s3backup;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class CancellationCleanup extends BukkitRunnable {
    private S3Backup s3Backup;
    private String archiveName;
    private Player player;

    public CancellationCleanup(S3Backup s3Backup, Player player, String archiveName) {
        this.s3Backup = s3Backup;
        this.archiveName = archiveName;
    }

    @Override
    public void run() {
        String backupPath = s3Backup.getFileConfig().getBackupDir() + File.separator + archiveName;
        File backupArchive = new File(backupPath);

        if (backupArchive.delete()) {
            s3Backup.sendMessage(player, "Partial backup successfully deleted");
        } else {
            s3Backup.sendMessage(player, "Failed to delete partial backup: " + backupPath);
        }
    }
}
