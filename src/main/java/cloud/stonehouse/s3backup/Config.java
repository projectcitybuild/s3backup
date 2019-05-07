package cloud.stonehouse.s3backup;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private FileConfiguration config;

    public Config(S3Backup s3Backup) {
        config = s3Backup.getConfig();

        config.addDefault("access-key-id", "");
        config.addDefault("access-key-secret", "");
        config.addDefault("backup-interval", 60);
        config.addDefault("bucket", "");
        config.addDefault("debug", false);
        config.addDefault("local-prefix", "s3backup");
        config.addDefault("max-backups", 20);
        config.addDefault("prefix", "s3backup/");
        config.addDefault("region", "us-west-2");
        config.options().copyDefaults(true);

        s3Backup.saveConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
