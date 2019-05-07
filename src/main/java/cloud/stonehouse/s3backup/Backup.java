package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Backup extends BukkitRunnable {

    private final S3Backup s3Backup;
    private final Player player;

    Backup(S3Backup s3Backup, Player player) {
        this.s3Backup = s3Backup;
        this.player = player;
    }

    @Override
    public void run() {
        String archiveName = new SimpleDateFormat(s3Backup.getFileConfig().getBackupDateFormat())
                .format(new Date() + ".zip");
        String localPrefix = s3Backup.getFileConfig().getLocalPrefix();

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off"));
        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all"));

        try {
            s3Backup.sendMessage(player, true, "Backup initiated.");

            s3Backup.getArchive().zipFolder(new File(s3Backup.getServer().getWorldContainer().getAbsolutePath()),
                    new File(localPrefix + File.separator + archiveName));
            s3Backup.getS3Put().put(archiveName);
            s3Backup.getArchive().removeFile(new File(localPrefix + File.separator + archiveName));

            s3Backup.sendMessage(player, true, "Backup complete.");

        } catch (Exception e) {
            s3Backup.sendMessage(player, false, "Backup failed: " + e.getLocalizedMessage());
            s3Backup.exception(e);
        }

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));

        int maxBackups = s3Backup.getFileConfig().getMaxBackups();

        if (maxBackups > 0) {
            ArrayList<String> backups = new S3List(s3Backup).list();
            if (backups.size() > maxBackups) {
                int removed = 0;
                while (removed < (backups.size() - maxBackups)) {
                    String remove = backups.get(0);
                    backups.remove(0);
                    s3Backup.getS3Delete().delete(player, remove);
                    removed++;
                }
            }
        }
    }
}
