package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.*;
import com.amazonaws.services.s3.AmazonS3;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Objects;
import java.util.regex.Pattern;

public class S3Backup extends JavaPlugin {

    private Archive archive;
    private Boolean backupInProgress;
    private AmazonS3 client;
    private BukkitTask scheduler;
    private Config config;
    private S3Delete s3Delete;
    private S3List s3List;
    private S3Put s3Put;
    private S3Sign s3Sign;

    @Override
    public void onEnable() {
        config = new Config(this);
        client = new S3Client(this).getClient();
        archive = new Archive(this);
        s3Delete = new S3Delete(this);
        s3List = new S3List(this);
        s3Put = new S3Put(this);
        s3Sign = new S3Sign(this);

        if (new File(getFileConfig().getBackupDir()).mkdir()) {
            this.sendMessage(null, "Created backup directory");
        }

        Objects.requireNonNull(this.getCommand("s3backup")).setExecutor(new CommandS3Backup(this));

        int backupInterval = getFileConfig().getBackupInterval();
        scheduler = new Scheduler(this).runTaskTimer(this,
                20 * 60 * backupInterval,
                20 * 60 * backupInterval);

        setProgress(false);
    }

    @Override
    public void onDisable() {
        sendMessage(null, "Stopping backup scheduler");
        scheduler.cancel();
    }

    public boolean backupExists(String filePrefix) {
        return getClient().doesObjectExist(getFileConfig().getBucket(), filePrefix);
    }

    public void exception(Player player, String message, Exception e) {
        if (getFileConfig().getDebug()) {
            e.printStackTrace();
        } else {
            getLogger().warning(message + ": " + e.getLocalizedMessage());
        }
        if (player != null) {
            sendMessage(player, "§c" + message + ": " + e.getLocalizedMessage());
        }
    }

    Archive archive() {
        return archive;
    }


    public String convertBytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public AmazonS3 getClient() {
        return client;
    }

    public Config getFileConfig() {
        return config;
    }

    S3Delete s3Delete() {
        return s3Delete;
    }

    S3List s3List() {
        return s3List;
    }

    S3Put s3Put() {
        return s3Put;
    }

    S3Sign s3Sign() {
        return s3Sign;
    }

    boolean hasPermission(Player player, String permission) {
        if (player != null) {
            return player.hasPermission(permission);
        } else {
            return true;
        }
    }

    boolean illegalString(String string) {
        return !Pattern.compile("^[a-zA-Z0-9-_]+$").matcher(string).matches();
    }

    boolean illegalPrefix(String string) {
        if (string.equals("")) {
            return false;
        } else {
            return !Pattern.compile("^[a-zA-Z0-9-_/]+$").matcher(string).matches();
        }
    }

    boolean inProgress() {
        return backupInProgress;
    }

    public void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(getFileConfig().getChatPrefix() + message);
        } else {
            getLogger().info(message);
        }
    }

    void setProgress(Boolean progress) {
        backupInProgress = progress;
    }
}
