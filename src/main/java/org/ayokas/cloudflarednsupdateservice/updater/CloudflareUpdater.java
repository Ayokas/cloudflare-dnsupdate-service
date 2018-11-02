package org.ayokas.cloudflarednsupdateservice.updater;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ayokas.cloudflarednsupdateservice.CloudflareErrorException;
import org.ayokas.cloudflarednsupdateservice.CloudflareProperties;
import org.ayokas.cloudflarednsupdateservice.cloudflareAPI.UpdateDNSRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;

public class CloudflareUpdater {
    private static final Logger log = LogManager.getLogger(CloudflareUpdater.class);

    private static final String UPDATE_RECORD_URL = "%s/zones/%s/dns_records/%s";

    public static String updateRecord(String zone, String record, String type, String name, String content, String base64Credentials, int ttl, Boolean proxied) throws IOException, CloudflareErrorException {

        try {
            String base64Creds = base64Credentials.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Creds), Charset.forName("UTF-8"));

            // Set base-URL
            String baseUrl = CloudflareProperties.getApibaseurl();

            URL url = new URL(String.format(UPDATE_RECORD_URL, baseUrl, zone, record));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");

            // Check if Service-User or E-Mail is proviced for login
            // Cloudflare Service-User always starts with "v1.0-" | see https://api.cloudflare.com/#getting-started-requests
            if (credentials.startsWith("v1.0-")) {
                conn.setRequestProperty("X-Auth-User-Service-Key", credentials);
            } else {
                String[] basicValues = credentials.split(":", 2);
                conn.setRequestProperty("X-Auth-Email", basicValues[0]);
                conn.setRequestProperty("X-Auth-Key", basicValues[1]);
            }

            // Create request body
            UpdateDNSRecord requestBody = new UpdateDNSRecord(type, name, content);

            // Only update optional parameters if necessary
            if (proxied != null) {
                requestBody.setProxied(proxied);
            }

            // Set TTL (1 = automatic, Range: min = 120 max = 2147483647)
            // https://api.cloudflare.com/#dns-records-for-a-zone-update-dns-record
            if (ttl == 1 || (ttl >= 120)) {
                requestBody.setTtl(ttl);
            }

            // Convert to JSON request
            Gson g = new Gson();
            String jsonBody = g.toJson(requestBody);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();

            // Listen for Request
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder responseStringBuilder = new StringBuilder();
            log.info("Response from Cloudflare:");
            while ((output = br.readLine()) != null) {
                log.info(output);
                responseStringBuilder.append(output);
            }

            // Disconnect from Cloudflare
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
