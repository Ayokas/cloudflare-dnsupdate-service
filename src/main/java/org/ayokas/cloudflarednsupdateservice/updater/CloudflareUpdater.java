package org.ayokas.cloudflarednsupdateservice.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ayokas.cloudflarednsupdateservice.CloudflareProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudflareUpdater {
    private static final Logger log = LogManager.getLogger(CloudflareUpdater.class);

    public static String updateARecord(String address, String domain, String email, String apiKey) {
        try {
            String baseUrl = CloudflareProperties.getApibaseurl();
            String zone = CloudflareProperties.getZone();
            String record = CloudflareProperties.getRecord();

            URL url = new URL(String.format("%s/zones/%s/dns_records/%s", baseUrl, zone, record));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-Auth-Key", apiKey);
            conn.setRequestProperty("X-Auth-Email", email);
            String requestBody = String.format("{\"type\":\"A\",\"name\":\"%s\",\"content\":\"%s\",\"ttl\":120,\"proxied\":false}", domain, address);
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();

            if (conn.getResponseCode() != 200) {
                log.error("Cloudflare responses with HTTP error code: " + conn.getResponseCode() + " - " + conn.getResponseMessage());
                return "Cloudflare responses with HTTP error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder responseStringBuilder = new StringBuilder();
            log.info("Output from Cloudflare:");
            while ((output = br.readLine()) != null) {
                log.info(output);
                responseStringBuilder.append(output);
            }

            conn.disconnect();
            return responseStringBuilder.toString();
        } catch (IOException e) {
            log.error(e);
            return e.getMessage();
        }
    }
}
