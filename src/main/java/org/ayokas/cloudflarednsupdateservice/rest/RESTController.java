package org.ayokas.cloudflarednsupdateservice.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ayokas.cloudflarednsupdateservice.CloudflareErrorException;
import org.ayokas.cloudflarednsupdateservice.updater.CloudflareUpdater;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

@RestController
public class RESTController {
    private static final Logger logger = LogManager.getLogger(RESTController.class);

    @RequestMapping("/rest/update/a")
    public ResponseEntity getExecuteARecordUpdate(
            @RequestParam(value = "address", defaultValue = "") String address,
            @RequestParam(value = "domain", defaultValue = "") String domain,
            HttpServletRequest request) {

        logger.info(String.format("New IPv4 update GET request from '%s' with update address '%s'", request.getRemoteAddr(), address));

        // Get Credentials from Basic Authentication
        String email;
        String apiKey;

        String basicAuth = request.getHeader("Authorization");
        if (basicAuth != null && basicAuth.startsWith("Basic")) {
            String base64Creds = basicAuth.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Creds), Charset.forName("UTF-8"));

            email = credentials.split(":", 2)[0];
            apiKey = credentials.split(":", 2)[1];
            String response;
            try {
                response = CloudflareUpdater.updateARecord(address, domain, email, apiKey);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please contact your administrator - IOException on updateARecord");
            } catch (CloudflareErrorException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Cloudflare responded with at errors: %s", e.getMessage()));
            }

            // Return Cloudflare response message as JSON
            logger.info("Update successful!");
            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No Credentials sent. Please use Cloudflare(R) email as user and API-Key as password for basic authentication.");
        }
    }
}
