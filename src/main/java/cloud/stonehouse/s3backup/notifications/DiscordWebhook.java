package cloud.stonehouse.s3backup.notifications;

import cloud.stonehouse.s3backup.S3Backup;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookCluster;
import club.minnced.discord.webhook.send.WebhookEmbed;
import okhttp3.OkHttpClient;

public class DiscordWebhook {
    private final WebhookClient webhookClient;
    private final String prefix;

    public DiscordWebhook(S3Backup s3Backup) {
        String webhookUrl = s3Backup.getFileConfig().getWebhookUrl();
        prefix = stripPrefix(s3Backup.getFileConfig().getPrefix());
        webhookClient = WebhookClient.withUrl(webhookUrl);
    }

    private String stripPrefix(String fullPrefix) {
        if (fullPrefix.charAt(fullPrefix.length() - 1) == '/') {
            return fullPrefix.substring(0, fullPrefix.length() - 1);
        } else {
            return fullPrefix;
        }
    }

    public void send(DiscordNotification notification) {
        WebhookEmbed embed = notification.build(prefix);
        webhookClient.send(embed);
    }

    public void close() {
        webhookClient.close();
    }
}
