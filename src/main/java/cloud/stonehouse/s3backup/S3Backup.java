package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.*;
import com.amazonaws.services.s3.AmazonS3;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class S3Backup extends JavaPlugin {

    private Archive archive;
    private AmazonS3 client;
    private Config config;
    private S3Delete s3Delete;
    private S3List s3List;
    private S3Put s3Put;
    private S3Sign s3Sign;

    @Override
    public void onEnable() {
        this.config = new Config(this);
        this.client = new S3Client(this).getClient();
        this.archive = new Archive(this);
        this.s3Delete = new S3Delete(this);
        this.s3List = new S3List(this);
        this.s3Put = new S3Put(this);
        this.s3Sign = new S3Sign(this);

        if (new File(this.getFileConfig().getLocalPrefix()).mkdir()) {
            this.sendMessage(null, "Created backup directory");
        }

        Objects.requireNonNull(this.getCommand("s3backup")).setExecutor(new CommandS3Backup(this));

        int backupInterval = this.getFileConfig().getBackupInterval();
        new Scheduler(this).runTaskTimer(this,
                20 * 60 * backupInterval,
                20 * 60 * backupInterval);

        new Metrics(this);
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
            this.sendMessage(player, "§c" + message + ": " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onDisable() {
    }

    Archive getArchive() {
        return archive;
    }

    public AmazonS3 getClient() {
        return this.client;
    }

    public Config getFileConfig() {
        return this.config;
    }

    S3Delete getS3Delete() {
        return s3Delete;
    }

    S3List getS3List() {
        return s3List;
    }

    S3Put getS3Put() {
        return s3Put;
    }

    S3Sign getS3Sign() {
        return s3Sign;
    }

    boolean hasPermission(Player player, String permission) {
        if (player != null) {
            return player.hasPermission(permission);
        } else {
            return true;
        }
    }

    public void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(getFileConfig().getChatPrefix() + message);
        } else {
            getLogger().info(message);
        }
    }
}
