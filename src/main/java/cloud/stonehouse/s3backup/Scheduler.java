package cloud.stonehouse.s3backup;

import org.bukkit.scheduler.BukkitRunnable;

class Scheduler extends BukkitRunnable {

    private final S3Backup s3Backup;

    Scheduler(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        s3Backup.sendMessage(null, true, "Backup scheduler started.");
    }

    @Override
    public void run() {
        new Backup(s3Backup, null).runTaskAsynchronously(s3Backup);
    }
}
