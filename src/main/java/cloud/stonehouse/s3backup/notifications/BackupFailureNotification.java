package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;

public class BackupFailureNotification implements DiscordNotification {
    private Exception exception;

    public BackupFailureNotification(Exception exception) {
        this.exception = exception;
    }

    @Override
    public WebhookEmbed build(String prefix) {
        return NotificationFactory.create()
                .setIsFailure()
                .setMessage(prefix + ": Backup error encountered")
                .setException(exception)
                .build();
    }
}
