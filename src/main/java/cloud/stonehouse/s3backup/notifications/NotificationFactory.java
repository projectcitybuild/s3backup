package cloud.stonehouse.s3backup.notifications;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NotificationFactory {
    WebhookEmbedBuilder builder;
    public NotificationFactory() {
        builder = new WebhookEmbedBuilder();
    }

    public static NotificationFactory create() {
        return new NotificationFactory();
    }

    public NotificationFactory setIsSuccessful() {
        builder.setColor(0x008000);
        return this;
    }

    public NotificationFactory setIsInfo() {
        builder.setColor(0x007fff);
        return this;
    }

    public NotificationFactory setIsFailure() {
        builder.setColor(0xff2052);
        return this;
    }

    public NotificationFactory setMessage(String message) {
        builder.setTitle(new WebhookEmbed.EmbedTitle(message, null));
        return this;
    }

    public NotificationFactory setException(Exception e) {
        builder.setDescription(e.getLocalizedMessage());
        return this;
    }

    public WebhookEmbed build() {
        return builder.build();
    }
}