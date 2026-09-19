package me.uuun.warProject.discord;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookManager {
    private static final String WEBHOOK_URL =
            "";

    public static void send(String... content) {
        String message = String.join("\n", content);
        String json = "{\"content\":\"" + escapeJson(message) + "\"}";

        try {
            URL url = URI.create(WEBHOOK_URL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                System.err.println("[WebhookManager] Discord webhook вернул код: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            System.err.println("[WebhookManager] Ошибка отправки в вебхук: " + e.getMessage());
        }
    }

    private static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
