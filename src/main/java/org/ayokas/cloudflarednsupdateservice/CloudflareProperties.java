package org.ayokas.cloudflarednsupdateservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("cloudflare")
@Component
public class CloudflareProperties {

    private String zone = "";
    private String record = "";
    private String apibaseurl = "";

    public CloudflareProperties() {
    }

    public String getZone() {
        return zone;
    }

    public String getRecord() {
        return record;
    }

    public String getApibaseurl() {
        return apibaseurl;
    }

}
