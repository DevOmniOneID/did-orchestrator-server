package org.omnione.did.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.base.property.BlockchainProperties;
import org.omnione.did.base.property.DatabaseProperties;
import org.omnione.did.base.property.ServicesProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorConfigDto {
    private BlockchainProperties blockchain;
    private DatabaseProperties database;
    private ServicesProperties services;
}