package cloud.stonehouse.s3backup;

import com.amazonaws.services.s3.model.PutObjectResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Backup extends BukkitRunnable {

    private S3Backup s3Backup;
    private SimpleDateFormat dateFormat;
    private String datePattern = "dd-MM-yyyy--HH-mm-ss";
    private String localPrefix;
    private Player player;

    public Backup(S3Backup s3Backup, Player player) {
        this.s3Backup = s3Backup;
        this.dateFormat = new SimpleDateFormat(datePattern);
        this.localPrefix = s3Backup.getFileConfig().getString("local-prefix");
        this.player = player;
    }

    @Override
    public void run() {
        Archive archive = new Archive(s3Backup);
        String archiveName = dateFormat.format(new Date()) + ".zip";

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off"));
        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all"));

        try {
            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup initiated.");
            }

            s3Backup.getLogger().info("Backup initiated.");

            archive.zipFolder(new File(s3Backup.getServer().getWorldContainer().getAbsolutePath()),
                    new File(localPrefix + File.separator + archiveName));

            PutObjectResult upload = new S3Put(s3Backup, archiveName).upload();
            archive.removeFile(new File(localPrefix + File.separator + archiveName));

            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup complete.");
            }

            s3Backup.getLogger().info("Backup complete. MD5: " + upload.getContentMd5());

        } catch (Exception e) {

            if (player != null) {
                player.sendMessage("§7[§es3backup§7] Backup failed: " + e.getLocalizedMessage());
            }
            s3Backup.exception(e);
        }

        Bukkit.getScheduler().callSyncMethod(s3Backup, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));

        int maxBackups = s3Backup.getFileConfig().getInt("max-backups");

        if (maxBackups > 0) {
            ArrayList<String> backups = new S3List(s3Backup).list();
            if (backups.size() > maxBackups) {
                int removed = 0;
                while (removed < (backups.size() - maxBackups)) {
                    String remove = backups.get(0);
                    backups.remove(0);
                    new S3Delete(s3Backup, player, remove).delete();
                    removed++;
                }
            }
        }
    }
}
