package org.ayokas.cloudflarednsupdateservice.cloudflareAPI;

public class UpdateDNSRecord {

    private String type;
    private String name;
    private String content;
    private Integer ttl = null;
    private Boolean proxied = null;

    public UpdateDNSRecord(String type, String name, String content) {
        this.type = type;
        this.name = name;
        this.content = content;
    }

    public UpdateDNSRecord(String type, String name, String content, Integer ttl, Boolean proxied) {
        this.type = type;
        this.name = name;
        this.content = content;
        this.ttl = ttl;
        this.proxied = proxied;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public boolean isProxied() {
        return proxied;
    }

    public void setProxied(boolean proxied) {
        this.proxied = proxied;
    }
}
