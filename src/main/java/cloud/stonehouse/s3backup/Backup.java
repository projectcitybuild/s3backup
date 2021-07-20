package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.notifications.BackupFailureNotification;
import cloud.stonehouse.s3backup.notifications.BackupPurgedNotification;
import cloud.stonehouse.s3backup.notifications.BackupStartedNotification;
import cloud.stonehouse.s3backup.notifications.BackupSuccessNotification;
import cloud.stonehouse.s3backup.s3.S3List;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

class Backup extends BukkitRunnable {

    private final Boolean backupInProgress;
    private final String customPrefix;
    private final S3Backup s3Backup;
    private final Player player;
    private final boolean dryRun;

    Backup(S3Backup s3Backup, Player player, String customPrefix, boolean dryRun) {
        this.customPrefix = customPrefix;
        this.s3Backup = s3Backup;
        this.player = player;
        this.dryRun = dryRun;

        this.backupInProgress = s3Backup.inProgress();

        if (!backupInProgress) {
            s3Backup.sendMessage(player, "Saving worlds. This will take a moment");
            Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off"));
            Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all"));
            s3Backup.sendMessage(player, "Worlds saved");
        }
    }

    @Override
    public void run() {
        if (backupInProgress) {
            s3Backup.sendMessage(player, "A backup is already in progress");

        } else {
            s3Backup.setProgress(true);
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
                s3Backup.discordWebhook().send(new BackupStartedNotification());
                String archivePath = backupDir + File.separator + archiveName;

                s3Backup.archive().zipFile(player, Paths.get("").toAbsolutePath().normalize().toString(), archivePath, dryRun);

                if (dryRun) {
                    s3Backup.sendMessage(player, "Backup completed! This was a dry run.");
                    return;
                }

                s3Backup.s3Put().put(archiveName);
                s3Backup.archive().deleteFile(archivePath);

                s3Backup.sendMessage(player, "Backup complete");
                s3Backup.discordWebhook().send(new BackupSuccessNotification());

                int maxBackups = s3Backup.getFileConfig().getMaxBackups();
                if (maxBackups > 0) {
                    TreeMap<Date, S3ObjectSummary> backups = new S3List(s3Backup).list(null, false, 0);

                    while (backups.size() > maxBackups) {
                        Date remove = backups.firstKey();

                        s3Backup.s3Delete().delete(player, backups.get(remove).getKey().replace(s3Backup.getFileConfig()
                                .getPrefix(), ""));
                        backups.remove(remove);
                        s3Backup.discordWebhook().send(new BackupPurgedNotification());
                    }
                }

            } catch (Exception e) {
                s3Backup.exception(player, "Backup failed", e);
                s3Backup.discordWebhook().send(new BackupFailureNotification(e));
            } finally {
                Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));
                s3Backup.setProgress(false);
            }
        }

    }
}
