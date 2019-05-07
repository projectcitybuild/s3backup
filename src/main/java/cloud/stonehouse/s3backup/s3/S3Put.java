package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;

import java.io.File;

public class S3Put {

    private final S3Backup s3Backup;

    public S3Put(S3Backup s3backup) {
        this.s3Backup = s3backup;
    }

    public void put(String archiveName) {
        s3Backup.getClient().putObject(s3Backup.getFileConfig().getBucket(),
                s3Backup.getFileConfig().getPrefix() + archiveName,
                new File(s3Backup.getFileConfig().getLocalPrefix() + File.separator + archiveName));
    }
}
