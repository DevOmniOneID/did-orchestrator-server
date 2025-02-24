# OpenDID Orchestrator 설치 및 구동 가이드

이 문서는 OpenDID Orchestrator를 설치하고 구동하는 방법을 설명합니다.  
아래 단계를 따라 진행하시면 됩니다.

---

## 1. did-orchestrator를 git clone해서 내려받기

먼저, OpenDID Orchestrator 프로젝트를 로컬 환경에 클론합니다.  
아래 명령어를 터미널에 입력하여 프로젝트를 다운로드하세요.

```bash
git clone https://github.com/your-repository/did-orchestrator.git
```

---

## 2. start.sh 실행

프로젝트를 클론한 후, 프로젝트 루트 디렉토리로 이동하여 `start.sh` 스크립트를 실행합니다.  
이 스크립트는 **did-release** 저장소에서 해당 버전에 맞는 서버 jar 파일을 다운로드하고, Orchestrator 서버를 구동합니다.

```bash
cd did-orchestrator
sh start.sh
```

서버를 종료하려면 `stop.sh` 스크립트를 실행하세요.

```bash
sh stop.sh
```

---

## 3. 브라우저에서 Orchestrator 접속

Orchestrator 서버가 정상적으로 구동되면, 브라우저에서 아래 주소로 접속합니다.  
기본 포트는 `9001`입니다.

```
http://<현재_IP>:9001
```

예를 들어, 로컬 환경에서 실행 중이라면:  
```
http://localhost:9001
```

---

## 4. Hyperledger Fabric 및 PostgreSQL를 위한 Docker 구동

OpenDID Orchestrator는 Hyperledger Fabric Network와 PostgreSQL DB를 사용합니다.  
이러한 서비스를 구동하기 위해 Docker Desktop 또는 Colima를 실행해야 합니다.  
아래는 각 운영체제별 실행 방법입니다.

### Windows
1. [Docker Desktop](https://www.docker.com/products/docker-desktop)을 다운로드 및 설치합니다.  
2. 설치 후 Docker Desktop을 실행합니다.  

### macOS
1. [Docker Desktop](https://www.docker.com/products/docker-desktop) 또는 [Colima](https://github.com/abiosoft/colima)를 설치합니다.  
2. Colima를 사용하는 경우, 다음 명령어로 Colima를 실행합니다:  
```bash
colima start
```

### Linux
1. Docker를 설치합니다.  
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
```
2. Docker 서비스를 시작합니다.  
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

---

## 5. 전체 entities 구동

1. 브라우저에서 Orchestrator에 접속한 후, **All Entities** 버튼을 클릭하여 전체 entities를 구동합니다.  

2. 각 서비스 및 엔티티가 구동이 되면 녹색이 점등됩니다.

*각 서비스 및 엔티티는 전체 구동/중지 및 개별 구동/중지가 가능합니다.

---

## 추가 참고 사항

- Docker 컨테이너가 정상적으로 실행되었는지 확인하려면 다음 명령어를 사용하세요:  
```bash
docker ps
```
- 문제가 발생할 경우, 로그를 확인하여 원인을 파악하세요:  
```bash
docker logs <컨테이너_ID>
```

---

이제 OpenDID Orchestrator가 정상적으로 구동되었습니다.  
추가적인 문의 사항은 프로젝트의 [이슈 트래커](https://github.com/OmnioneId/did-orchestrator/issues)를 이용해 주세요.