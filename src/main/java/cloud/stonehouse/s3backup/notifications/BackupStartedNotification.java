package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;

public class BackupStartedNotification implements DiscordNotification{
    @Override
    public WebhookEmbed build() {
        return NotificationFactory.create()
                .setIsSuccessful()
                .setMessage("Backup has been started")
                .build();
    }
}
