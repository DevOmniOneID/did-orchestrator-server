/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.orchestrator.controller;

import org.omnione.did.base.property.ConfigProperties;
import org.omnione.did.base.property.ServicesProperties;
import org.omnione.did.orchestrator.dto.OrchestratorConfigDto;
import org.omnione.did.orchestrator.dto.OrchestratorResponseDto;
import org.omnione.did.orchestrator.dto.OrchestratorRequestDto;
import org.omnione.did.orchestrator.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrchestratorController {
    private final ConfigProperties configProperties;
    private final ServicesProperties servicesProperties;
    private final OrchestratorService orchestratorService;

    public OrchestratorController(ServicesProperties servicesProperties, OrchestratorService orchestratorService, ConfigProperties configProperties) {
        this.servicesProperties = servicesProperties;
        this.orchestratorService = orchestratorService;

        this.configProperties = configProperties;
    }

    @GetMapping("/")
    public String index(Model model) {

        List<String> serviceNames = servicesProperties.getServer().values().stream()
                .map(ServicesProperties.ServiceDetail::getName)
                .collect(Collectors.toList());

        List<Integer> servicePorts = servicesProperties.getServer().values().stream()
                .map(ServicesProperties.ServiceDetail::getPort)
                .collect(Collectors.toList());

        model.addAttribute("serviceNames", serviceNames);
        model.addAttribute("servicePorts", servicePorts);
        // 서버 ip
        model.addAttribute("serverIp", orchestratorService.getServerIp());

        return "index";
    }
    @GetMapping("/startup/all")
    public ResponseEntity<OrchestratorResponseDto> startupAll() {
        OrchestratorResponseDto response = new OrchestratorResponseDto();
        try {
            orchestratorService.requestStartupAll();
            response.setStatus("SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("ERROR");
            return ResponseEntity.status(500).body(response);
        }

    }

    @GetMapping("/shutdown/all")
    public ResponseEntity<OrchestratorResponseDto> shutdownAll() {
        OrchestratorResponseDto response = new OrchestratorResponseDto();
        try {
            orchestratorService.requestShutdownAll();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus("ERROR");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/startup/{port}")
    public ResponseEntity<OrchestratorResponseDto> startup(@PathVariable String port) {
        System.out.println("health check port : " + port);
        try {
            OrchestratorResponseDto response = orchestratorService.requestStartup(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }

    }

    @GetMapping("/shutdown/{port}")
    public ResponseEntity<OrchestratorResponseDto> shutdown(@PathVariable String port) {
        // shutdown post 요청
        System.out.println("shutdown check port : " + port);
        try {
            OrchestratorResponseDto response = orchestratorService.requestShutdown(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/{port}")
    public ResponseEntity<OrchestratorResponseDto> healthCheck(@PathVariable String port) {
        // healthcheck는 get 요청
        System.out.println("health check port : " + port);
        try {
            OrchestratorResponseDto response = orchestratorService.requestHealthCheck(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/refresh/{port}")
    public ResponseEntity<OrchestratorResponseDto> refresh(@PathVariable String port) {
        // refresh post 요청
        System.out.println("refresh check port : " + port);
        try {
            OrchestratorResponseDto response = orchestratorService.requestRefresh(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/startup/fabric")
    public ResponseEntity<OrchestratorResponseDto> fabricStartup() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestStartupFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/shutdown/fabric")
    public ResponseEntity<OrchestratorResponseDto> fabricShutdown() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestShutdownFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/fabric")
    public ResponseEntity<OrchestratorResponseDto> fabricHealthCheck() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestHealthCheckFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/startup/postgre")
    public ResponseEntity<OrchestratorResponseDto> postgreStartup() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestStartupPostgre();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/shutdown/postgre")
    public ResponseEntity<OrchestratorResponseDto> postgreShutdown() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestShutdownPostgre();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/postgre")
    public ResponseEntity<OrchestratorResponseDto> postgreHealthCheck() {
        try {
            OrchestratorResponseDto response = orchestratorService.requestHealthCheckPostgre();
            System.out.println("postgre response : " + response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create/wallet")
    public ResponseEntity<OrchestratorResponseDto> createWallet(@RequestBody OrchestratorRequestDto request) {
        try{
            OrchestratorResponseDto result = orchestratorService.createWallet(request.getFilename(), request.getPassword());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create/keys")
    public ResponseEntity<OrchestratorResponseDto> createWalletKeys(@RequestBody OrchestratorRequestDto request) {
        List<String> keyIds = request.getKeyIds();
        if (keyIds == null || keyIds.isEmpty()) {
            throw new IllegalArgumentException("keyIds cannot be null or empty");
        }

        try{
            OrchestratorResponseDto result = orchestratorService.createKeys(request.getFilename(), request.getPassword(), keyIds);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create/diddoc")
    public ResponseEntity<OrchestratorResponseDto> createDidDocument(@RequestBody OrchestratorRequestDto request) {
        try{
            OrchestratorResponseDto result = orchestratorService.createDidDocument(request.getFilename(), request.getPassword(), request.getDid(), request.getController(), request.getType());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/configs")
    public ResponseEntity<OrchestratorConfigDto> getConfig() {
        OrchestratorConfigDto response = new OrchestratorConfigDto(
                configProperties.getBlockchain(),
                configProperties.getDatabase(),
                configProperties.getServices()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/configs/update")
    public ResponseEntity<OrchestratorResponseDto> updateYaml(@RequestBody Map<String, Object> updates) {
        try {
            OrchestratorResponseDto response = orchestratorService.updateConfig(updates);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            OrchestratorResponseDto errorResponse = new OrchestratorResponseDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
