package org.ayokas.cloudflarednsupdateservice.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ayokas.cloudflarednsupdateservice.CloudflareErrorException;
import org.ayokas.cloudflarednsupdateservice.CloudflareProperties;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudflareUpdater {
    private static final Logger log = LogManager.getLogger(CloudflareUpdater.class);

    public static String updateARecord(CloudflareProperties cloudflareProperties, String address, String domain, String email, String apiKey) throws IOException, CloudflareErrorException {
        try {
            String baseUrl = cloudflareProperties.getApibaseurl();
            String zone = cloudflareProperties.getZone();
            String record = cloudflareProperties.getRecord();

            if (baseUrl.isEmpty() || zone.isEmpty() || record.isEmpty()) {
                throw new CloudflareErrorException("Cloudflare baseUrl, zone or record-id not set in application.properties!");
            }

            URL url = new URL(String.format("%s/zones/%s/dns_records/%s", baseUrl, zone, record));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            // Authorization depends on if e-mail is set to cloudflare-token -> API-Token Authorization
            if (email.equals("cloudflare-token")) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            } else {
                conn.setRequestProperty("X-Auth-Key", apiKey);
                conn.setRequestProperty("X-Auth-Email", email);
            }

            String requestBody = String.format("{\"type\":\"A\",\"name\":\"%s\",\"content\":\"%s\",\"ttl\":120,\"proxied\":false}", domain, address);
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();

            // Get response body either for successful response or error analysis
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                log.info("Return code from cloudflare: " + conn.getResponseCode());
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(inputStreamReader);

                String output;
                StringBuilder responseStringBuilder = new StringBuilder();
                log.info("Output from Cloudflare:");
                while ((output = br.readLine()) != null) {
                    log.info(output);
                    responseStringBuilder.append(output);
                }
                return responseStringBuilder.toString();

            } else {
                if (conn.getErrorStream() != null) {
                    InputStreamReader errorStream = new InputStreamReader(conn.getErrorStream());
                    BufferedReader br = new BufferedReader(errorStream);

                    String output;
                    StringBuilder responseStringBuilder = new StringBuilder();
                    log.info("Output from Cloudflare:");
                    while ((output = br.readLine()) != null) {
                        log.info(output);
                        responseStringBuilder.append(output);
                    }
                    String errorMessage = String.format("Cloudflare responses with HTTP error code: %d - %s - %s", conn.getResponseCode(), conn.getResponseMessage(), responseStringBuilder);
                    log.error(errorMessage);
                    throw new CloudflareErrorException(errorMessage);
                } else {
                    throw new CloudflareErrorException(String.format("Cloudflare returned error code %d", conn.getResponseCode()));
                }
            }
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
    }
}
