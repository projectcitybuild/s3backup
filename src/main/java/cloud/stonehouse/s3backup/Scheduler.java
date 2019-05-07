package cloud.stonehouse.s3backup;

public class Scheduler implements Runnable {

    private S3Backup s3Backup;
    private int minutes;
    private Thread scheduler;

    public Scheduler(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.minutes = s3Backup.getFileConfig().getInt("backup-interval");
        this.scheduler = new Thread(this);
    }

    public void startScheduler() {
        scheduler.start();
    }

    @Override
    public void run() {
        s3Backup.getLogger().info("Backup scheduler started.");
        while (true) {
            try {
                scheduler.sleep(1000 * 60 * minutes);
                new Backup(s3Backup, null).runTaskAsynchronously(s3Backup);
            } catch (InterruptedException e) {
                s3Backup.exception(e);
            }
        }
    }
}
