# Orchestrator User Manual

## Overview
`Orchestrator` is a **server integrated management console** that provides functionality for monitoring and controlling the status of multiple servers.
This document includes descriptions of the layout and features of the Orchestrator.

## 1. How to Access
- Open your web browser and navigate to `http://<server IP>:9001`.
- The initial screen displays the overall status of all servers.

## 2. Screen Layout
The Orchestrator screen is composed of the following key areas.

![freepik license](https://raw.githubusercontent.com/DevOmniOneID/did-orchestrator-frontend/refs/heads/main/orchestrator.png)

### 2.1 Quick Start
Provides **Start All** and **Stop All** functions for all entities.

- **All Entities**
  - Status Icons:  
    - 🟢 All servers are running  
    - 🟡 Some servers are running  
    - 🔴 All servers are stopped  

  - `Start All`: Starts all entities.
  - `Stop All`: Stops all entities.

### 2.2 Repositories
Provides **Start, Stop, and Status** functions for each server.

- **Hyperledger Fabric**
  - Status Icons: 🟢 (Running) / 🔴 (Stopped)
  - `Start`: Start Hyperledger Fabric
  - `Stop`: Stop Hyperledger Fabric
  - `Status`: Check the running status of Hyperledger Fabric

- **PostgreSQL**
  - Status Icons: 🟢 (Running) / 🔴 (Stopped)
  - `Start`: Start PostgreSQL
  - `Stop`: Stop PostgreSQL
  - `Status`: Check the running status of PostgreSQL

### 2.3 Servers
Provides **Start, Stop, and Status** functions for each server.

- **Individual Server Management**
  - Status Icons: 🟢 (Running) / 🔴 (Stopped)
  - Displays each server's name and port number.
  - `Start`: Starts the individual server.
  - `Stop`: Stops the individual server.
  - `Status`: Checks the running status of the individual server.
  - `Settings`: Navigates to the individual server's settings page.
  - `Swagger`: Navigates to the individual server's Swagger page.

## 3. Cautions
- When using the `Start` or `Stop` buttons, please note that the operation may take some time depending on the server environment.
- Detailed configuration of individual servers can be adjusted via the settings page (accessed through the `Settings` button).
