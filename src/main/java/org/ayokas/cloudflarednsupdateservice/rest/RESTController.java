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

@RestController
public class RESTController {
    private static final Logger logger = LogManager.getLogger(RESTController.class);

    @RequestMapping("/update")
    public ResponseEntity getExecuteARecordUpdate(
            @RequestParam(value = "zone", defaultValue = "") String zone,
            @RequestParam(value = "record", defaultValue = "") String record,
            @RequestParam(value = "type", defaultValue = "AAAA") String type,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "content", defaultValue = "") String content,
            @RequestParam(value = "ttl", defaultValue = "0") String ttl,
            @RequestParam(value = "proxied", required = false) Boolean proxied,
            HttpServletRequest request) {

        logger.info(String.format("New DNS record update, type: %s", type));

        // Check if credentials are available and update record
        String basicAuth = request.getHeader("Authorization");
        if (basicAuth != null && basicAuth.startsWith("Basic")) {
            String response;
            try {
                int iTtl = Integer.parseInt(ttl);
                response = CloudflareUpdater.updateRecord(zone, record, type, name, content, basicAuth, iTtl, proxied);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please contact your administrator - IOException on updateRecord");
            } catch (CloudflareErrorException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Cloudflare responded with at errors: %s", e.getMessage()));
            }

            // Return Cloudflare response message as JSON
            logger.info("DNS update successful!");
            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No Credentials sent. Please use Cloudflare(R) email as user and API-Key as password for basic authentication.");
        }
    }
}
