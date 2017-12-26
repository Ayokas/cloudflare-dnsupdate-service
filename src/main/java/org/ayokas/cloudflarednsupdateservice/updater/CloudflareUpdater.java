package org.ayokas.cloudflarednsupdateservice.updater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ayokas.cloudflarednsupdateservice.CloudflareErrorException;
import org.ayokas.cloudflarednsupdateservice.CloudflareProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudflareUpdater {
    private static final Logger log = LogManager.getLogger(CloudflareUpdater.class);

    public static String updateARecord(String address, String domain, String email, String apiKey) throws IOException, CloudflareErrorException {
        try {
            String baseUrl = CloudflareProperties.getApibaseurl();
            String zone = CloudflareProperties.getZone();
            String record = CloudflareProperties.getRecord();

            if (baseUrl.isEmpty() || zone.isEmpty() || record.isEmpty()) {
                throw new CloudflareErrorException("Cloudflare baseUrl, zone or record-id not set in application.properties!");
            }

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

            // Get response body either for successful response or error analysis
            if (conn.getResponseCode() != 200) {
                log.error(String.format("Cloudflare responses with HTTP error code: %d - %s - %s", conn.getResponseCode(), conn.getResponseMessage(), responseStringBuilder.toString()));
                throw new CloudflareErrorException(responseStringBuilder.toString());
            } else {
                return responseStringBuilder.toString();
            }
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
    }
}
