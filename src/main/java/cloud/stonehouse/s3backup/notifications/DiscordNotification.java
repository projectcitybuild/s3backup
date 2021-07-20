package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;

public interface DiscordNotification {
    WebhookEmbed build();
}
