package cloud.stonehouse.s3backup;

import cloud.stonehouse.s3backup.s3.S3Client;
import cloud.stonehouse.s3backup.s3.S3Delete;
import cloud.stonehouse.s3backup.s3.S3List;
import cloud.stonehouse.s3backup.s3.S3Put;
import com.amazonaws.services.s3.AmazonS3;
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

    @Override
    public void onEnable() {
        this.config = new Config(this);
        this.client = new S3Client(this).getClient();
        this.archive = new Archive(this);
        this.s3Delete = new S3Delete(this);
        this.s3List = new S3List(this);
        this.s3Put = new S3Put(this);

        if (new File(this.getFileConfig().getLocalPrefix()).mkdir()) {
            this.sendMessage(null, true, "Created backup directory.");
        }

        Objects.requireNonNull(this.getCommand("s3backup")).setExecutor(new CommandBackup(this));

        new Scheduler(this).runTaskTimerAsynchronously(this,
                20 * 60 * this.getFileConfig().getBackupInterval(),
                20 * 60 * this.getFileConfig().getBackupInterval());
    }

    public void exception(Exception e) {
        if (getFileConfig().getDebug()) {
            e.printStackTrace();
        } else {
            getLogger().warning(e.getLocalizedMessage());
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

    public void sendMessage(Player player, boolean server, String message) {
        if (player != null) {
            player.sendMessage(getFileConfig().getChatPrefix() + message);
        }
        if (server) {
            getLogger().info(message);
        }
    }
}
