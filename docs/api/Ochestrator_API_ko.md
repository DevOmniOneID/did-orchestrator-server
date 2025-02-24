# Orchestrator API 문서

---

- **일자**: 2025-02-21
- **버전**: v1.0.0

---

## 목차

- [Orchestrator API 문서](#orchestrator-api-문서)
  - [목차](#목차)
  - [1. 개요](#1-개요)
  - [2. API 목록](#2-api-목록)
  - [3. API 상세 설명](#3-api-상세-설명)
    - [3.1. 전체 서비스 시작/종료](#31-전체-서비스-시작종료)
      - [요청 예시](#요청-예시-1)
      - [응답 예시](#응답-예시-1)
    - [3.2. 특정 서비스 시작/종료](#32-특정-서비스-시작종료)
      - [요청 예시](#요청-예시-2)
      - [응답 예시](#응답-예시-2)
    - [3.3. 특정 서비스 상태 확인](#33-특정-서비스-상태-확인)
      - [요청 예시](#요청-예시-3)
      - [응답 예시](#응답-예시-3)
    - [3.4. 특정 서비스 리프레시](#34-특정-서비스-리프레시)
      - [요청 예시](#요청-예시-4)
      - [응답 예시](#응답-예시-4)
    - [3.5. HyperLedger Fabric 관련 API](#35-hyperledger-fabric-관련-api)
      - [요청 예시](#요청-예시-5)
      - [응답 예시](#응답-예시-5)
    - [3.6. PostgreSQL 관련 API](#36-postgresql-관련-api)
      - [요청 예시](#요청-예시-6)
      - [응답 예시](#응답-예시-6)
    - [3.7. Wallet 및 키 생성](#37-wallet-및-키-생성)
      - [요청 예시](#요청-예시-7)
      - [응답 예시](#응답-예시-7)
    - [3.8. DID 문서 생성](#38-did-문서-생성)
      - [요청 예시](#요청-예시-8)
      - [응답 예시](#응답-예시-8)
    - [3.9. 설정 조회 및 업데이트](#39-설정-조회-및-업데이트)
      - [요청 예시](#요청-예시-9)
      - [응답 예시](#응답-예시-9)
  - [4. 에러 코드](#4-에러-코드)
  - [5. 참고 사항](#5-참고-사항)

---

## 1. 개요

본 문서는 Orchestrator 서비스가 제공하는 API를 정의합니다. Orchestrator는 다양한 서비스의 시작, 종료, 상태 확인, 설정 관리 등을 담당합니다.

---

## 2. API 목록

| API                          | Method | URL                          | 설명                          |
|------------------------------|--------|------------------------------|-------------------------------|
| `전체 서비스 시작`            | GET    | `/startup/all`               | 모든 서비스 시작               |
| `전체 서비스 종료`            | GET    | `/shutdown/all`              | 모든 서비스 종료               |
| `특정 서비스 시작`            | GET    | `/startup/{port}`            | 특정 포트의 서비스 시작        |
| `특정 서비스 종료`            | GET    | `/shutdown/{port}`           | 특정 포트의 서비스 종료        |
| `특정 서비스 상태 확인`       | GET    | `/healthcheck/{port}`        | 특정 포트의 서비스 상태 확인   |
| `특정 서비스 리프레시`        | GET    | `/refresh/{port}`            | 특정 포트의 서비스 리프레시    |
| `HyperLedger Fabric 시작`                | GET    | `/startup/fabric`            | HyperLedger Fabric 서비스 시작             |
| `HyperLedger Fabric 종료`                | GET    | `/shutdown/fabric`           | HyperLedger Fabric 서비스 종료             |
| `HyperLedger Fabric 상태 확인`           | GET    | `/healthcheck/fabric`        | HyperLedger Fabric 서비스 상태 확인        |
| `PostgreSQL 시작`            | GET    | `/startup/postgre`           | PostgreSQL 서비스 시작         |
| `PostgreSQL 종료`            | GET    | `/shutdown/postgre`          | PostgreSQL 서비스 종료         |
| `PostgreSQL 상태 확인`       | GET    | `/healthcheck/postgre`       | PostgreSQL 서비스 상태 확인    |
| `Wallet 생성`                | POST   | `/create/wallet`             | Wallet 생성                   |
| `키 생성`                    | POST   | `/create/keys`               | Wallet 내 키 생성             |
| `DID 문서 생성`              | POST   | `/create/diddoc`             | DID 문서 생성                 |
| `설정 조회`                  | GET    | `/configs`                   | 현재 설정 조회                |
| `설정 업데이트`              | POST   | `/configs/update`            | 설정 업데이트                 |

---

## 3. API 상세 설명

### 3.1. 전체 서비스 시작/종료

- **URL**: `/startup/all`, `/shutdown/all`
- **Method**: `GET`
- **설명**: Orchestrator가 관리하는 모든 서비스를 시작하거나 종료합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/startup/all"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.2. 특정 서비스 시작/종료

- **URL**: `/startup/{port}`, `/shutdown/{port}`
- **Method**: `GET`
- **설명**: 특정 포트의 서비스를 시작하거나 종료합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/startup/8080"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.3. 특정 서비스 상태 확인

- **URL**: `/healthcheck/{port}`
- **Method**: `GET`
- **설명**: 특정 포트의 서비스 상태를 확인합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/healthcheck/8080"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.4. 특정 서비스 리프레시

- **URL**: `/refresh/{port}`
- **Method**: `GET`
- **설명**: 특정 포트의 서비스를 리프레시합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/refresh/8080"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.5. HyperLedger Fabric 관련 API

- **URL**: `/startup/fabric`, `/shutdown/fabric`, `/healthcheck/fabric`
- **Method**: `GET`
- **설명**: HyperLedger Fabric 서비스의 시작, 종료, 상태 확인을 수행합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/startup/fabric"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.6. PostgreSQL 관련 API

- **URL**: `/startup/postgre`, `/shutdown/postgre`, `/healthcheck/postgre`
- **Method**: `GET`
- **설명**: PostgreSQL 서비스의 시작, 종료, 상태 확인을 수행합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/startup/postgre"
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.7. Wallet 및 키 생성

- **URL**: `/create/wallet`, `/create/keys`
- **Method**: `POST`
- **설명**: Wallet을 생성하거나 Wallet 내에 키를 생성합니다.

#### 요청 예시

```shell
curl -X POST "http://${Host}:${Port}/create/wallet" \
-H "Content-Type: application/json" \
-d '{"filename": "wallet1", "password": "password123"}'
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.8. DID 문서 생성

- **URL**: `/create/diddoc`
- **Method**: `POST`
- **설명**: DID 문서를 생성합니다.

#### 요청 예시

```shell
curl -X POST "http://${Host}:${Port}/create/diddoc" \
-H "Content-Type: application/json" \
-d '{"filename": "wallet1", "password": "password123", "did": "did:example:123", "controller": "did:example:456", "type": "Ed25519"}'
```

#### 응답 예시

```json
{
  "status": "SUCCESS"
}
```

---

### 3.9. 설정 조회 및 업데이트

- **URL**: `/configs`, `/configs/update`
- **Method**: `GET`, `POST`
- **설명**: 현재 설정을 조회하거나 업데이트합니다.

#### 요청 예시

```shell
curl -X GET "http://${Host}:${Port}/configs"
```

#### 응답 예시

```json
{
  "blockchain": "fabric",
  "database": "postgresql",
  "services": {
    "service1": {
      "port": 8080,
      "status": "RUNNING"
    }
  }
}
```

---

## 4. 에러 코드

| 코드         | 설명                          |
|--------------|-------------------------------|
| `500`        | 서버 내부 오류                |
| `400`        | 잘못된 요청                   |

---

## 5. 참고 사항

- API 호출 시 발생하는 오류는 `status` 필드를 통해 확인할 수 있습니다.
- 설정 업데이트는 `POST` 메서드를 사용하며, 요청 본문에 업데이트할 설정을 포함해야 합니다.