package dev.latestion.marketplace.utils;

import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class DiscordWebhook {

    public static void sendWebhook(String webhookUrl, String title, String description, String footerText, String imageUrl, String color) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\n" +
                    "  \"embeds\": [\n" +
                    "    {\n" +
                    "      \"title\": \"" + title + "\",\n" +
                    "      \"description\": \"" + description + "\",\n" +
                    "      \"color\": " + color + ",\n" +
                    "      \"footer\": {\n" +
                    "        \"text\": \"" + footerText + "\"\n" +
                    "      },\n" +
                    "      \"image\": {\n" +
                    "        \"url\": \"" + imageUrl + "\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            connection.getResponseCode(); // To trigger the request
            connection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Something went wrong with Discord Webhooks.", e);
        }
    }
}