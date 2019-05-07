package cloud.stonehouse.s3backup;

import com.amazonaws.services.s3.AmazonS3;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class S3Backup extends JavaPlugin {

    private AmazonS3 client;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.config = new Config(this).getConfig();
        this.client = new S3Client(this).getClient();
        new File(getFileConfig().getString("local-prefix")).mkdir();

        this.getCommand("s3backup").setExecutor(new CommandBackup(this));
        new Scheduler(this).startScheduler();
    }

    @Override
    public void onDisable() {
    }

    public FileConfiguration getFileConfig() {
        return this.config;
    }

    public AmazonS3 getClient() {
        return this.client;

    }

    public void exception(Exception e) {
        if (getFileConfig().getBoolean("debug")) {
            e.printStackTrace();
        } else {
            getLogger().warning(e.getLocalizedMessage());
        }
    }
}
