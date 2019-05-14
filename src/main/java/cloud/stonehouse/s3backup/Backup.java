package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Backup extends BukkitRunnable {

    private final String customPrefix;
    private final S3Backup s3Backup;
    private final Player player;

    Backup(S3Backup s3Backup, Player player, String customPrefix) {
        this.customPrefix = customPrefix;
        this.s3Backup = s3Backup;
        this.player = player;
        s3Backup.sendMessage(player, "Saving worlds. This will take a moment");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        s3Backup.sendMessage(player, "Worlds saved");
    }

    @Override
    public void run() {
        try {
            String dateFormat = s3Backup.getFileConfig().getBackupDateFormat();
            if (s3Backup.illegalString(dateFormat)) {
                throw new StringFormatException("Invalid date format. Only alphanumeric characters, underscores and " +
                        "hyphens are permitted. Please check config.yml");
            }
            if (s3Backup.illegalPrefix(s3Backup.getFileConfig().getPrefix())) {
                throw new StringFormatException("Invalid prefix format. Only alphanumeric characters, underscores, " +
                        "hyphens and forward slashes are permitted. Please check config.yml");
            }

            String archiveName = customPrefix + new SimpleDateFormat(s3Backup.getFileConfig().getBackupDateFormat())
                    .format(new Date()) + ".zip";
            String backupDir = s3Backup.getFileConfig().getBackupDir();

            s3Backup.sendMessage(player, "Backup initiated");
            String archivePath = backupDir + File.separator + archiveName;

            s3Backup.archive().zipFile(player, Paths.get("").toAbsolutePath().normalize().toString(), archivePath);
            s3Backup.s3Put().put(archiveName);
            s3Backup.archive().deleteFile(archivePath);

            s3Backup.sendMessage(player, "Backup complete");

            int maxBackups = s3Backup.getFileConfig().getMaxBackups();
            if (maxBackups > 0) {
                ArrayList<String> backups = new S3List(s3Backup).list(null, false);
                while (backups.size() > maxBackups) {
                    String remove = backups.get(0);
                    backups.remove(0);
                    s3Backup.s3Delete().delete(player, remove);
                }
            }

        } catch (Exception e) {
            s3Backup.exception(player, "Backup failed", e);
        }

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));
    }
}
