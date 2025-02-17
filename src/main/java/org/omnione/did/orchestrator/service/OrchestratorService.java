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

package org.omnione.did.orchestrator.service;

import org.omnione.did.orchestrator.dto.OrchestratorDto;

public interface OrchestratorService {
    //블록체인(fabric)
    String requestStartupFabric();
    String requestShutdownFabric();
    OrchestratorDto requestHealthCheckFabric();

    // postgreSQL
    OrchestratorDto requestStartupPostgre();
    OrchestratorDto requestShutdownPostgre();
    OrchestratorDto requestHealthCheckPostgre();

    //서버
    OrchestratorDto requestStartup(String port);
    OrchestratorDto requestShutdown(String port);
    OrchestratorDto requestHealthCheck(String port);
    OrchestratorDto requestRefresh(String port);
    OrchestratorDto requestStartupAll();
    OrchestratorDto requestShutdownAll();

    String createWallet(String fileName, String password);
    String createKeys(String fileName, String password);
    String createDidDocument(String fileName, String password, String did, String controller);

    String getServerIp();
}
