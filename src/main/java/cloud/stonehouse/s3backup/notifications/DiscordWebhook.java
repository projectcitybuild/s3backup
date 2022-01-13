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
        prefix = s3Backup.getFileConfig().getPrefix();
        webhookClient = WebhookClient.withUrl(webhookUrl);
    }

    public void send(DiscordNotification notification) {
        WebhookEmbed embed = notification.build(prefix);
        webhookClient.send(embed);
    }

    public void close() {
        webhookClient.close();
    }
}
