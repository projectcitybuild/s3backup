package cloud.stonehouse.s3backup.s3;

import cloud.stonehouse.s3backup.S3Backup;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.bukkit.entity.Player;

import java.net.URL;
import java.util.Date;

public class S3Sign {

    private final S3Backup s3Backup;

    public S3Sign(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    public void sign(Player player, String backup) {
        Date expiration = new Date();
        long expire = expiration.getTime();
        expire += 1000 * 60 * 60;
        expiration.setTime(expire);

        GeneratePresignedUrlRequest urlRequest =
                new GeneratePresignedUrlRequest(s3Backup.getFileConfig().getBucket(),
                        s3Backup.getFileConfig().getPrefix() + backup)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3Backup.getClient().generatePresignedUrl(urlRequest);
        s3Backup.sendMessage(player, true, url.toString());
    }
}