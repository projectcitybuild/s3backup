package cloud.stonehouse.s3backup;

import org.bukkit.scheduler.BukkitRunnable;

class Scheduler extends BukkitRunnable {

    private final S3Backup s3Backup;

    Scheduler(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        s3Backup.sendMessage(null, "Starting backup scheduler");
    }

    @Override
    public void run() {
        new Backup(s3Backup, null, "", false).runTaskAsynchronously(s3Backup);
    }
}
