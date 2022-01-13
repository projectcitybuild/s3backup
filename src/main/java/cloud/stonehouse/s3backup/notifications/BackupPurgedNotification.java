package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;

public class BackupPurgedNotification implements DiscordNotification{

    @Override
    public WebhookEmbed build(String prefix) {
        return NotificationFactory.create()
                .setIsInfo()
                .setMessage(prefix + ": Old backup purged")
                .build();
    }
}
