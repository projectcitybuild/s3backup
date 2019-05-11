package cloud.stonehouse.s3backup;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private final S3Backup s3Backup;
    private final String backupDir;
    private FileConfiguration config;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String backupDateFormat;
    private final int backupInterval;
    private final String chatPrefix;
    private final String bucket;
    private final Boolean debug;
    private final String helpString;
    private final int maxBackups;
    private final String prefix;
    private final String region;

    Config(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        loadDefaults();

        this.accessKeyId = config.getString("access-key-id");
        this.accessKeySecret = config.getString("access-key-secret");
        this.backupDateFormat = config.getString("backup-date-format");
        this.backupDir = "backup";
        this.backupInterval = config.getInt("backup-interval");
        this.bucket = config.getString("bucket");
        this.chatPrefix = "§7[§es3backup§7] ";
        this.debug = config.getBoolean("debug");
        this.helpString = "/s3backup [<backup <name>> <list>] [<get delete sign> <backup>]";
        this.maxBackups = config.getInt("max-backups");
        this.prefix = config.getString("prefix");
        this.region = config.getString("region");
    }

    private void loadDefaults() {
        config = s3Backup.getConfig();

        config.addDefault("access-key-id", "");
        config.addDefault("access-key-secret", "");
        config.addDefault("backup-date-format", "dd-MM-yyyy-HH-mm-ss");
        config.addDefault("backup-interval", 60);
        config.addDefault("bucket", "");
        config.addDefault("debug", false);
        config.addDefault("max-backups", 20);
        config.addDefault("prefix", "");
        config.addDefault("region", "us-west-2");
        config.options().copyDefaults(true);

        s3Backup.saveConfig();
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    String getBackupDateFormat() {
        return backupDateFormat;
    }

    public String getBackupDir() {
        return backupDir;
    }

    int getBackupInterval() {
        return backupInterval;
    }

    public String getBucket() {
        return bucket;
    }

    String getChatPrefix() {
        return chatPrefix;
    }

    Boolean getDebug() {
        return debug;
    }

    String getHelpString() {
        return helpString;
    }

    int getMaxBackups() {
        return maxBackups;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRegion() {
        return region;
    }
}
