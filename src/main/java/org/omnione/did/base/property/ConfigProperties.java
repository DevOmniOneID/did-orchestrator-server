package org.omnione.did.base.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationPropertiesScan
@Configuration
public class ConfigProperties {
    private final BlockchainProperties blockchain;
    private final DatabaseProperties database;
    private final ServicesProperties services;

    public ConfigProperties(BlockchainProperties blockchain, DatabaseProperties database, ServicesProperties services) {
        this.blockchain = blockchain;
        this.database = database;
        this.services = services;
    }
}