package cloud.stonehouse.s3backup;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class S3Get extends BukkitRunnable {
    private S3Backup s3Backup;
    private String backup;
    private String bucket;
    private String localPrefix;
    private Player player;
    private String prefix;

    public S3Get(S3Backup s3Backup, Player player, String backup) {
        this.s3Backup = s3Backup;
        this.backup = backup;
        this.bucket = s3Backup.getFileConfig().getString("bucket");
        this.localPrefix = s3Backup.getFileConfig().getString("local-prefix");
        this.player = player;
        this.prefix = s3Backup.getFileConfig().getString("prefix");
    }

    @Override
    public void run() {
        try {
            S3Object s3object = s3Backup.getClient().getObject(bucket, prefix + backup);
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            File targetFile = new File(localPrefix + File.separator + backup);
            OutputStream outStream;
            outStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outStream.close();

            if (player != null) {
                player.sendMessage("§7[§es3backup§7] " + backup + " downloaded to the backups directory.");
            } else {
                s3Backup.getLogger().info(backup + " downloaded to the backups directory.");
            }
        } catch (Exception e) {
            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup download failed: " + e.getLocalizedMessage());
            }
            s3Backup.exception(e);
        }
    }
}
