# OpenDID Orchestrator Installation and Operation Guide

This document explains how to install and run the OpenDID Orchestrator.  
Follow the steps below to proceed.

---

## 1. Clone the did-orchestrator repository

First, clone the OpenDID Orchestrator project to your local environment.  
Run the following command in your terminal to download the project.

```bash
git clone https://github.com/OmniOneID/did-release.git
```

---

## 2. Run `start.sh`

After cloning the project, navigate to the project root directory and run the `start.sh` script.  
This script downloads the necessary entity server JAR files for the specified version from the **did-release** repository and starts the Orchestrator server.

```bash
cd did-release/release-[version]/deployment
sh start.sh
```

To stop the server, run the `stop.sh` script:

```bash
sh stop.sh
```

---

## 3. Access the Orchestrator in a browser

Once the Orchestrator server is successfully running, access it in your browser using the following address.  
The default port is `9001`.

```
http://<current_IP>:9001
```

For example, if running locally:
```
http://localhost:9001
```

---

## 4. Run Docker for Hyperledger Fabric and PostgreSQL

OpenDID Orchestrator relies on the Hyperledger Fabric Network and PostgreSQL database.  
You need to run these services using Docker Desktop or Colima.  
Follow the instructions below based on your operating system.

### Windows
1. Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop).
2. Launch Docker Desktop after installation.
3. Follow the [Fabric Install Guide](https://hyperledger-fabric.readthedocs.io/en/latest/prereqs.html#wsl2) to install WSL2.

### macOS
1. Install either [Docker Desktop](https://www.docker.com/products/docker-desktop) or [Colima](https://github.com/abiosoft/colima).
2. If using Colima, start it with the following command:

```bash
colima start
```

### Linux
1. Install Docker:

```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
```

2. Start the Docker service:

```bash
sudo systemctl start docker
sudo systemctl enable docker
```

---

## 5. Start all entities

1. Open the Orchestrator in a browser and click the **All Entities** button to start all entities.
2. Once each service and entity is running, the status indicators will turn green.

*Each service and entity can be started/stopped individually or all at once.*

---

## 6. Generate Wallet and DID Document

Wallet and DID Documents can be generated either for all entities at once or for individual entities.

### Generate for all entities
Click the **Generate All** button and enter a password to generate Wallet and DID Documents.  
Wallets and DID Documents will be automatically created based on the entity names.

### Generate for individual entities
To manually generate a Wallet and DID Document for a specific entity, go to the **Servers** tab and click the Wallet or DID Document button for the desired entity.

*The generated Wallets require setting the wallet path and password in each entity’s settings page.  
DID Documents are used when registering entities.*

---

## 7. TAS Individual Configuration and Issuance of Membership Certificates

Navigate to the **Servers** tab, click the **Settings** button for TAS, and go to the TAS settings page.  
Follow the installation guide to configure TAS, register entities, and issue membership certificates.

---

## Additional Notes

- If services or entities fail to start properly, check the logs for debugging.

---

Now, the OpenDID Orchestrator should be running successfully.  
For additional inquiries, please use the project's [Issue Tracker](https://github.com/OmnioneId/did-orchestrator/issues).
