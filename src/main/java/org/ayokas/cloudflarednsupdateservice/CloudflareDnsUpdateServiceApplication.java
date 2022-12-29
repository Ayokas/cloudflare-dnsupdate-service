package org.ayokas.cloudflarednsupdateservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CloudflareDnsUpdateServiceApplication {
    private static final Logger logger = LogManager.getLogger(CloudflareDnsUpdateServiceApplication.class);

    public static void main(String[] args) {
        logger.warn("Starting application!");
        SpringApplication.run(CloudflareDnsUpdateServiceApplication.class, args);
    }
}
