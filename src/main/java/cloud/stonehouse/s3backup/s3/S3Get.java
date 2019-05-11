package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class S3Get extends BukkitRunnable {

    private final S3Backup s3Backup;
    private final String backup;
    private final Player player;

    public S3Get(S3Backup s3Backup, Player player, String backup) {
        this.s3Backup = s3Backup;
        this.backup = backup;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            String filePrefix = s3Backup.getFileConfig().getPrefix() + backup;
            if (s3Backup.backupExists(filePrefix)) {
                S3Object s3object = s3Backup.getClient().getObject(s3Backup.getFileConfig().getBucket(), filePrefix);
                s3Backup.sendMessage(player, "Started download of " + backup);

                S3ObjectInputStream inputStream = s3object.getObjectContent();
                File targetFile = new File(s3Backup.getFileConfig().getBackupDir() + File.separator + backup);
                OutputStream outStream;
                outStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outStream.close();

                s3Backup.sendMessage(player, backup + " downloaded to the local backup directory");
            } else {
                s3Backup.sendMessage(player, "Backup " + backup + " does not exist");
            }
        } catch (Exception e) {
            s3Backup.exception(player, "Backup download failed", e);
        }
    }
}
