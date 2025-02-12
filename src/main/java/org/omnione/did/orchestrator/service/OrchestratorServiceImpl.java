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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.property.ServiceProperty;
import org.omnione.did.orchestrator.dto.OrchestratorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrchestratorServiceImpl implements OrchestratorService{
    private final RestTemplate restTemplate = new RestTemplate();
    private final ServiceProperty serviceProperty;
    private final String JARS_DIR;
    private final Map<String, String> SERVER_JARS;
    private final Map<String, String> SERVER_JARS_FOLDER;

    @Autowired
    public OrchestratorServiceImpl(ServiceProperty serviceProperty) {
        this.serviceProperty = serviceProperty;
        this.JARS_DIR = System.getProperty("user.dir") + serviceProperty.getJarPath();
        this.SERVER_JARS = initializeServerJars();
        this.SERVER_JARS_FOLDER = initializeServerJarsFolder();
    }

    private Map<String, String> initializeServerJars() {
        Map<String, String> serverJars = new HashMap<>();
        serviceProperty.getServer().forEach((key, serverDetail) ->
                serverJars.put(String.valueOf(serverDetail.getPort()), serverDetail.getFile()));
        return serverJars;
    }

    private Map<String, String> initializeServerJarsFolder() {
        Map<String, String> serverJarsFolder = new HashMap<>();
        serviceProperty.getServer().forEach((key, serverDetail) ->
                serverJarsFolder.put(String.valueOf(serverDetail.getPort()), serverDetail.getName()));
        return serverJarsFolder;
    }

    @Override
    public String requestStartup(String port) {
        System.out.println("Startup request for port: " + port);
        try {
            if (port.equals("1000")) {
                for (String serverPort : SERVER_JARS.keySet()) {
                    startServer(serverPort);
                }
                return "All servers are starting...";
            } else if (SERVER_JARS.containsKey(port)) {
                startServer(port);
                return "Server on port " + port + " is starting...";
            } else {
                return "Invalid port number";
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown error";
    }

    @Override
    public OrchestratorDto requestShutdown(String port) {
        String targetUrl = getServerUrl() + port + "/actuator/shutdown";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<OrchestratorDto> response = restTemplate.postForEntity(targetUrl, requestEntity, OrchestratorDto.class);

        return response.getBody();
    }

    @Override
    public OrchestratorDto requestHealthCheck(String port) {
        String targetUrl = getServerUrl() + port + "/actuator/health";
        System.out.println("target url : " + targetUrl);

        ResponseEntity<OrchestratorDto> response = restTemplate.getForEntity(targetUrl, OrchestratorDto.class);
        return response.getBody();
    }

    @Override
    public OrchestratorDto requestRefresh(String port) {
        String targetUrl = getServerUrl() + port + "/actuator/refresh";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<OrchestratorDto> response = restTemplate.postForEntity(targetUrl, requestEntity, OrchestratorDto.class);

        return response.getBody();
    }


    @Override
    public String requestStartupFabric() {
        System.out.println("requestStartupFabric");
        try {
            String fabricShellPath = System.getProperty("user.dir") + "/shells/Fabric";
            ProcessBuilder builder = new ProcessBuilder("sh", fabricShellPath + "/start.sh");
            builder.directory(new File(fabricShellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } return "fabric startup";
    }

    @Override
    public String requestShutdownFabric() {
        System.out.println("requestShutdownFabric");
        try {
            String fabricShellPath = System.getProperty("user.dir") + "/shells/Fabric";
            ProcessBuilder builder = new ProcessBuilder("sh", fabricShellPath + "/stop.sh");
            builder.directory(new File(fabricShellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } return "fabric stop";
    }

    @Override
    public String requestHealthCheckFabric() {
        System.out.println("requestHealthCheckFabric");
        try {
            String fabricShellPath = System.getProperty("user.dir") + "/shells/Fabric";
            ProcessBuilder builder = new ProcessBuilder("sh", fabricShellPath + "/status.sh");
            builder.directory(new File(fabricShellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } return "fabric healthcheck";
    }

    @Override
    public OrchestratorDto requestStartupPostgre() {
        System.out.println("requestStartupPostgre");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String postgreShellPath = System.getProperty("user.dir") + "/shells/Postgre";
            ProcessBuilder builder = new ProcessBuilder("sh", postgreShellPath + "/start.sh");
            builder.directory(new File(postgreShellPath));

            Process process = builder.start();
            String output = getProcessOutput(process);
            System.out.println("startup : " + output);
            if (output.contains("Started")) {
                healthCheckCallback();
                response.setStatus("UP");
                return response;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        response.setStatus("ERROR");
        return response;
    }

    @Override
    public OrchestratorDto requestShutdownPostgre() {
        System.out.println("requestShutdownPostgre");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String postgreShellPath = System.getProperty("user.dir") + "/shells/Postgre";
            ProcessBuilder builder = new ProcessBuilder("sh", postgreShellPath + "/stop.sh");
            builder.directory(new File(postgreShellPath));

            Process process = builder.start();
            String output = getProcessOutput(process);
            if (output.contains("1")) {
                healthCheckCallback();
                response.setStatus("UP");
                return response;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        response.setStatus("ERROR");
        return response;
    }

    @Override
    public OrchestratorDto requestHealthCheckPostgre() {
        System.out.println("requestHealthCheckPostgre");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String postgreShellPath = System.getProperty("user.dir") + "/shells/Postgre";
            ProcessBuilder builder = new ProcessBuilder("sh", postgreShellPath + "/status.sh");
            builder.directory(new File(postgreShellPath));

            Process process = builder.start();
            String output = getProcessOutput(process);

            if (output.contains("1")) {
                healthCheckCallback();
                response.setStatus("UP");
                return response;
            }

            //isPostgreSQLRunning();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        response.setStatus("ERROR");
        return response;
    }

    private void healthCheckCallback() {
        System.out.println("구동중...");
    }

    @Override
    public String getServerIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unknown IP";
    }

    private void startServer(String port) throws IOException, InterruptedException {
        String jarFileName = SERVER_JARS.get(port);
        String jarFolderName = SERVER_JARS_FOLDER.get(port);
        File jarFile = new File(JARS_DIR + "/" + jarFolderName + "/" + jarFileName);

        if (!jarFile.exists()) {
            System.err.println("JAR file not found: " + jarFile.getAbsolutePath());
            return;
        }

        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath());
        builder.directory(new File(JARS_DIR));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.start();

        System.out.println("Server started on port " + port + ", waiting for health check...");

        Thread.sleep(10000);
        if (isServerRunning(port)) {
            System.out.println("Server on port " + port + " is running!");
        } else {
            System.err.println("Server on port " + port + " failed to start.");
        }
    }

    private boolean isServerRunning(String port) {
        try {
            URL url = new URL("http://localhost:" + port + "/actuator/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            System.out.println("running code : " + responseCode);
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }
    private String getServerUrl() {
        return "http://" + getServerIp() + ":";
    }

    private String getProcessOutput2(Process process) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        System.out.println("getProcessOutput : " + reader.readLine());
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exited with code: " + exitCode);

        return output.toString().trim();
    }

    private String getProcessOutput(Process process) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();

        Thread stdoutThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[OUTPUT] " + line);
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread stderrThread = new Thread(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println("[OUTPUT] " + line);
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stdoutThread.start();
        stderrThread.start();

        stdoutThread.join();
        stderrThread.join();

        int exitCode = process.waitFor();
        System.out.println("Process exited with code: " + exitCode);

        return output.toString().trim();
    }


    private boolean isPostgreSQLRunning() {
        System.out.println("isPostgreSQLRunning");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("PostgreSQL is running");
        String JDBC_URL = "jdbc:postgresql://localhost:5432/omn";
        String JDBC_USER = "omn";
        String JDBC_PASSWORD = "omn";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT version();")) {

            if (resultSet.next()) {
                System.out.println("PostgreSQL is running: " + resultSet.getString(1));
                return true;
            }
        } catch (Exception e) {
            System.err.println("PostgreSQL is not running: " + e.getMessage());
        }
        return false;
    }
}



