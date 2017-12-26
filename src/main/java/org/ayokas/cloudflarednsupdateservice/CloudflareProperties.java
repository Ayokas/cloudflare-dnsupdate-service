package org.ayokas.cloudflarednsupdateservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloudflare")
public class CloudflareProperties {

    private static String zone = "";
    private static String record = "";
    private static String apibaseurl = "";

    public CloudflareProperties() {
    }

    public static String getZone() {
        return zone;
    }

    public static void setZone(String zone) {
        CloudflareProperties.zone = zone;
    }

    public static String getRecord() {
        return record;
    }

    public static void setRecord(String record) {
        CloudflareProperties.record = record;
    }

    public static String getApibaseurl() {
        return apibaseurl;
    }

    public static void setApibaseurl(String apibaseurl) {
        CloudflareProperties.apibaseurl = apibaseurl;
    }
}
