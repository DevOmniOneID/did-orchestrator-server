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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
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
    private final String WALLET_DIR;
    private final String DID_DOC_DIR;
    private final String CLI_TOOL_DIR;

    @Autowired
    public OrchestratorServiceImpl(ServiceProperty serviceProperty) {
        this.serviceProperty = serviceProperty;
        this.JARS_DIR = System.getProperty("user.dir") + serviceProperty.getJarPath();
        this.SERVER_JARS = initializeServerJars();
        this.SERVER_JARS_FOLDER = initializeServerJarsFolder();
        this.WALLET_DIR = System.getProperty("user.dir") + serviceProperty.getWalletPath();
        this.DID_DOC_DIR = System.getProperty("user.dir") + serviceProperty.getDidDocPath();
        this.CLI_TOOL_DIR = System.getProperty("user.dir") + serviceProperty.getCliToolPath();
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

    interface FabricStartupCallback {
        void onStartupComplete();
        void onStartupFailed();
    }
    @Override
    public OrchestratorDto requestStartupAll() {
        int cnt = 0;

        OrchestratorDto response = new OrchestratorDto();
        response.setStatus("Unknown error");
        try {
            for (String serverPort : SERVER_JARS.keySet()) {
                String status = startServer(serverPort);
                if(status.equals("UP")){
                    cnt++;
                }
            }
            response.setCnt(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public OrchestratorDto requestShutdownAll() {
        int cnt = 0;

        OrchestratorDto response = new OrchestratorDto();
        response.setStatus("Unknown error");
        try {
            for (String serverPort : SERVER_JARS.keySet()) {
                requestShutdown(serverPort);
                cnt++;

            }
            response.setCnt(cnt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String createWallet(String fileName, String password) {
        System.out.println("createWallet : " + fileName + " / " + password);
        try {
            String shellPath = System.getProperty("user.dir") + "/tool";
            ProcessBuilder builder = new ProcessBuilder("sh", shellPath + "/create_wallet.sh", fileName);
            builder.directory(new File(shellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = builder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(password);
                writer.newLine();
                writer.flush();
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Wallet creation successful.");
                return "SUCCESS";
            } else {
                System.err.println("Wallet creation failed.");
                return "ERROR";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    @Override
    public String createKeys(String fileName, String password) {
        System.out.println("createKeys : " + fileName + " / " + password);
        String[] keyId = {"assert", "auth", "keyagree", "invoke"};
        try {
            for(int i = 0; i < 4; i++) {
                String shellPath = System.getProperty("user.dir") + "/tool";
                ProcessBuilder builder = new ProcessBuilder("sh", shellPath + "/create_keys.sh", fileName + ".wallet", keyId[i]);
                builder.directory(new File(shellPath));
                builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                builder.redirectError(ProcessBuilder.Redirect.INHERIT);

                Process process = builder.start();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(password);
                    writer.newLine();
                    writer.flush();
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Keypair creation successful.");
                } else {
                    System.err.println("Keypair creation failed.");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "SUCCESS";
    }

    @Override
    public String createDidDocument(String fileName, String password, String did, String controller) {
        System.out.println("createDidDocument : " + fileName + " / " + password + " / " + did + " / " + controller);
        try {
            String shellPath = System.getProperty("user.dir") + "/tool";
            ProcessBuilder builder = new ProcessBuilder("sh", shellPath + "/create_did_doc.sh", fileName + ".wallet", fileName + ".did", did, controller);
            builder.directory(new File(shellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = builder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(password);
                writer.newLine();
                writer.flush();
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("DID Documents creation successful.");
            } else {
                System.err.println("DID Documents creation failed.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "SUCCESS";
    }

    @Override
    public OrchestratorDto requestStartup(String port) {
        OrchestratorDto response = new OrchestratorDto();
        response.setStatus("Unknown error");
        System.out.println("Startup request for port: " + port);
        try {
            if (port.equals("1000")) {
                for (String serverPort : SERVER_JARS.keySet()) {
                    startServer(serverPort);
                }
                response.setStatus("OrchestratorDtoAll servers are starting...");
            } else if (SERVER_JARS.containsKey(port)) {
                response.setStatus(startServer(port));
            } else {
                response.setStatus("Invalid port number");
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public OrchestratorDto requestShutdown(String port) {
        OrchestratorDto response = new OrchestratorDto();
        try {
            String targetUrl = getServerUrl() + port + "/actuator/shutdown";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
            response = restTemplate.postForEntity(targetUrl, requestEntity, OrchestratorDto.class).getBody();
            response.setStatus("DOWN");
        } catch (Exception e) {
            response.setStatus("ERROR");
            return response;
        }
        return response;
    }

    @Override
    public OrchestratorDto requestHealthCheck(String port) {
        OrchestratorDto response = new OrchestratorDto();
        try {
            String targetUrl = getServerUrl() + port + "/actuator/health";
            System.out.println("target url : " + targetUrl);

            response = restTemplate.getForEntity(targetUrl, OrchestratorDto.class).getBody();
            System.out.println("requestHealthCheck : " + response);
        } catch (Exception e) {
            response.setStatus("ERROR");
            return response;
        }
        return response;
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
    public OrchestratorDto requestStartupFabric() {
        System.out.println("requestStartupFabric");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String fabricShellPath = System.getProperty("user.dir") + "/shells/Fabric";
            String logFilePath = fabricShellPath + "/fabric.log";

            ProcessBuilder builder = new ProcessBuilder(
                    "sh", "-c", "nohup " + fabricShellPath + "/start.sh > " + logFilePath + " 2>&1 &"
            );
            builder.directory(new File(fabricShellPath));
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.start();

            watchFabricLogs(logFilePath, new FabricStartupCallback() {
                @Override
                public void onStartupComplete() {
                    System.out.println("Hyperledger Fabric is running successfully!");
                }
                @Override
                public void onStartupFailed() {
                    System.out.println("Fabric startup failed.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fabric startup error: " + e.getMessage());
        }
        response.setStatus("UP");
        return response;
    }

    private void watchFabricLogs(String logFilePath, FabricStartupCallback callback) {
        File logFile = new File(logFilePath);
        System.out.println("Monitoring log file: " + logFilePath);

        try {
            while (!logFile.exists() || logFile.length() == 0) {
                System.out.println("Waiting for log file to be created...");
                Thread.sleep(3000);
            }

            long lastReadPosition = 0;
            while (true) {
                try (RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
                    reader.seek(lastReadPosition);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);

                        if (line.contains("Chaincode initialization is not required")) {
                            callback.onStartupComplete();
                            return;
                        }
                    }
                    lastReadPosition = reader.getFilePointer();
                }
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Log monitoring error: " + e.getMessage());
            callback.onStartupFailed();
        }
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
    public OrchestratorDto requestHealthCheckFabric() {
        System.out.println("requestHealthCheckFabric");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String fabricShellPath = System.getProperty("user.dir") + "/shells/Fabric";
            ProcessBuilder builder = new ProcessBuilder("sh", fabricShellPath + "/status.sh");
            builder.directory(new File(fabricShellPath));
            Process process = builder.start();
            String output = getProcessOutput(process);

            if (output.contains("200")) {
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
    public OrchestratorDto requestStartupPostgre() {
        System.out.println("requestStartupPostgre");
        OrchestratorDto response = new OrchestratorDto();
        try {
            String postgreShellPath = System.getProperty("user.dir") + "/shells/Postgre";
            ProcessBuilder builder = new ProcessBuilder("sh", postgreShellPath + "/start.sh");
            builder.directory(new File(postgreShellPath));

            Process process = builder.start();
            String output = getProcessOutput(process);
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
            if (output.contains("stop")) {
                healthCheckCallback();
                response.setStatus("DOWN");
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

    private String startServer(String port) throws IOException, InterruptedException {
        String jarFileName = SERVER_JARS.get(port);
        String jarFolderName = SERVER_JARS_FOLDER.get(port);
        File jarFile = new File(JARS_DIR + "/" + jarFolderName + "/" + jarFileName);

        if (!jarFile.exists()) {
            System.err.println("JAR file not found: " + jarFile.getAbsolutePath());
            return "ERROR";
        }

        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarFile.getAbsolutePath());
        builder.directory(new File(JARS_DIR));
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.start();
//        Process process = builder.start();
//        String output = getProcessOutput(process);
//        System.out.println("##############################output / " + port + " : " + output);
//        if (output.contains("Tomcat started on port " + port)) {
//            healthCheckCallback();
//        }
//
//        System.out.println("Server started on port " + port + ", waiting for health check...");

        Thread.sleep(7000);
        if (isServerRunning(port)) {
            System.out.println("Server on port " + port + " is running!");
            return "UP";
        } else {
            System.err.println("Server on port " + port + " failed to start.");
            return "DOWN";
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



