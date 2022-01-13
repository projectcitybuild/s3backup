package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;

public class BackupSuccessNotification implements DiscordNotification{
    @Override
    public WebhookEmbed build(String prefix) {
        return NotificationFactory.create()
                .setIsSuccessful()
                .setMessage(prefix + ": Backup succeeded")
                .build();
    }
}
