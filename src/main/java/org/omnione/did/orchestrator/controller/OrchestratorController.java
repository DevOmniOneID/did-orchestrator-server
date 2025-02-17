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

import org.omnione.did.base.property.ServiceProperty;
import org.omnione.did.orchestrator.dto.OrchestratorDto;
import org.omnione.did.orchestrator.dto.RequestDto;
import org.omnione.did.orchestrator.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrchestratorController {
    private final ServiceProperty serviceProperty;
    private final OrchestratorService orchestratorService;

    public OrchestratorController(ServiceProperty serviceProperty, OrchestratorService orchestratorService) {
        this.serviceProperty = serviceProperty;
        this.orchestratorService = orchestratorService;

    }

    @GetMapping("/")
    public String index(Model model) {

        List<String> serviceNames = serviceProperty.getServer().values().stream()
                .map(ServiceProperty.ServiceDetail::getName)
                .collect(Collectors.toList());

        List<Integer> servicePorts = serviceProperty.getServer().values().stream()
                .map(ServiceProperty.ServiceDetail::getPort)
                .collect(Collectors.toList());

        model.addAttribute("serviceNames", serviceNames);
        model.addAttribute("servicePorts", servicePorts);
        // 서버 ip
        model.addAttribute("serverIp", orchestratorService.getServerIp());

        return "index";
    }
    @GetMapping("/startup/all")
    public ResponseEntity<OrchestratorDto> startupAll() {
        try {
            OrchestratorDto response = orchestratorService.requestStartupAll();
            System.out.println("cnt : " + response.getCnt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }

    }

    @GetMapping("/shutdown/all")
    public ResponseEntity<OrchestratorDto> shutdownAll() {
        try {
            OrchestratorDto response = orchestratorService.requestShutdownAll();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/startup/{port}")
    public ResponseEntity<OrchestratorDto> startup(@PathVariable String port) {
        System.out.println("health check port : " + port);
        try {
            OrchestratorDto response = orchestratorService.requestStartup(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }

    }

    @GetMapping("/shutdown/{port}")
    public ResponseEntity<OrchestratorDto> shutdown(@PathVariable String port) {
        // shutdown post 요청
        System.out.println("shutdown check port : " + port);
        try {
            OrchestratorDto response = orchestratorService.requestShutdown(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/{port}")
    public ResponseEntity<OrchestratorDto> healthCheck(@PathVariable String port) {
        // healthcheck는 get 요청
        System.out.println("health check port : " + port);
        try {
            OrchestratorDto response = orchestratorService.requestHealthCheck(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error: " + port);

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/refresh/{port}")
    public ResponseEntity<OrchestratorDto> refresh(@PathVariable String port) {
        // refresh post 요청
        System.out.println("refresh check port : " + port);
        try {
            OrchestratorDto response = orchestratorService.requestRefresh(port);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/startup/fabric")
    public ResponseEntity<String> fabricStartup() {
        try {
            String response = orchestratorService.requestStartupFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            String errorResponse = "ERROR";
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/shutdown/fabric")
    public ResponseEntity<String> fabricShutdown() {
        try {
            String response = orchestratorService.requestShutdownFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            String errorResponse = "ERROR";
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/fabric")
    public ResponseEntity<OrchestratorDto> fabricHealthCheck() {
        try {
            OrchestratorDto response = orchestratorService.requestHealthCheckFabric();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("response error");

            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/startup/postgre")
    public ResponseEntity<OrchestratorDto> postgreStartup() {
        try {
            OrchestratorDto response = orchestratorService.requestStartupPostgre();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/shutdown/postgre")
    public ResponseEntity<OrchestratorDto> postgreShutdown() {
        try {
            OrchestratorDto response = orchestratorService.requestShutdownPostgre();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/healthcheck/postgre")
    public ResponseEntity<OrchestratorDto> postgreHealthCheck() {
        try {
            OrchestratorDto response = orchestratorService.requestHealthCheckPostgre();
            System.out.println("postgre response : " + response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrchestratorDto errorResponse = new OrchestratorDto();
            errorResponse.setStatus("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/create/wallet")
    public ResponseEntity<String> createWallet(@RequestBody RequestDto request) {
        String result = orchestratorService.createWallet(request.getFilename(), request.getPassword());
        return ResponseEntity.ok(result);
    }
    @PostMapping("/create/diddoc")
    public ResponseEntity<String> createDidDocument(@RequestBody RequestDto request) {
        String result = orchestratorService.createDidDocument(request.getFilename(), request.getPassword(), request.getDid(), request.getController());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create/keys")
    public ResponseEntity<String> createWalletKeys(@RequestBody RequestDto request) {
        String result = orchestratorService.createKeys(request.getFilename(), request.getPassword());
        return ResponseEntity.ok(result);
    }

}
