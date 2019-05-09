package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Backup extends BukkitRunnable {

    private final S3Backup s3Backup;
    private final Player player;

    Backup(S3Backup s3Backup, Player player) {
        this.s3Backup = s3Backup;
        this.player = player;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
    }

    @Override
    public void run() {
        String archiveName = new SimpleDateFormat(s3Backup.getFileConfig().getBackupDateFormat())
                .format(new Date()) + ".zip";
        String localPrefix = s3Backup.getFileConfig().getLocalPrefix();

        try {
            s3Backup.sendMessage(player, "Backup initiated");
            String archivePath = localPrefix + "/" + archiveName;

            s3Backup.getArchive().zipFile(Paths.get("").toAbsolutePath().normalize().toString(), archivePath);
            s3Backup.getS3Put().put(archiveName);
            s3Backup.getArchive().deleteFile(archivePath);

            s3Backup.sendMessage(player, "Backup complete");

            int maxBackups = s3Backup.getFileConfig().getMaxBackups();
            if (maxBackups > 0) {
                ArrayList<String> backups = new S3List(s3Backup).list();
                while (backups.size() > maxBackups) {
                    String remove = backups.get(0);
                    backups.remove(0);
                    s3Backup.getS3Delete().delete(player, remove);
                }
            }

        } catch (Exception e) {
            s3Backup.exception(player, "Backup failed", e);
        }

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));
    }
}
