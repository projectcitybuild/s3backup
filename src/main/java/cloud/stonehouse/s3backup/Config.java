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
    private final String customEndpoint;
    private final String bucket;
    private final Boolean debug;
    private final String helpString;
    private final String[] ignoreFiles;
    private final int maxBackups;
    private final Boolean pathStyleAccess;
    private final String prefix;
    private final String region;
    private final Integer signedUrlDuration;
    private final String signerOverride;

    Config(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.s3Backup.saveDefaultConfig();
        config = this.s3Backup.getConfig();

        accessKeyId = config.getString("access-key-id");
        accessKeySecret = config.getString("access-key-secret");
        backupDateFormat = config.getString("backup-date-format");
        backupDir = "backup";
        backupInterval = config.getInt("backup-interval");
        bucket = config.getString("bucket");
        chatPrefix = "§7[§es3backup§7] ";
        customEndpoint = config.getString("custom-endpoint");
        debug = config.getBoolean("debug");
        helpString = "/s3backup [<backup <name>> <list>] [<get delete sign> <backup>]";
        ignoreFiles = config.getList("ignore-files").toArray(new String[0]);
        maxBackups = config.getInt("max-backups");
        pathStyleAccess = config.getBoolean("path-style-access");
        prefix = config.getString("prefix");
        region = config.getString("region");
        signedUrlDuration = config.getInt("signed-url-duration");
        signerOverride = config.getString("signer-override");
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

    public String getCustomEndpoint() {
        return customEndpoint;
    }

    Boolean getDebug() {
        return debug;
    }

    String getHelpString() {
        return helpString;
    }

    String[] getIgnoreFiles() {
        return ignoreFiles;
    }

    int getMaxBackups() {
        return maxBackups;
    }

    public Boolean getPathStyleAccess() {
        return pathStyleAccess;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRegion() {
        return region;
    }

    public Integer getSignedUrlDuration() {
        return signedUrlDuration;
    }

    public String getSignerOverride() {
        return signerOverride;
    }
}
