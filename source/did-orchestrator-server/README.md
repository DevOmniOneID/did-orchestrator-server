DID Orchestrator Server Source
==

이 곳은 Orchestrator 서버 소스 코드 및 빌드 파일 영역입니다.<br>
DID Orchestrator Server 자체에 대한 설명은 [README.md](../../README_ko.md)를 참조해 주십시오.

## 1. Back-end 영역 구성요소
본 영역은 기본적으로 Back-end에 해당하는 서버 사이드에 대한 구성요소를 포함하고 있습니다. 주요 항목은 아래와 같습니다.

- `gradle` : Gradle 빌드 설정 및 스크립트
- `libs` : 외부 라이브러리 및 종속성
- `src` : 주요 소스 코드 디렉터리
- `build.gradle` : Gradle 빌드 설정 파일

## 2. Front-end 영역 구성요소
Orchestrator 서버의 Front-end는 [TypeScript](https://www.typescriptlang.org/) 기반의 [React.js](https://react.dev/)로 개발되었으며, DID Orchestrator의 UI/UX를 제공하는 역할을 합니다.<br>
관련 모든 구성요소는 admin 폴더 하위에 위치하며 주요 항목은 아래와 같습니다.

- `src` : React 소스 코드가 위치
- `public` : index.html 등 정적 파일 저장 디렉토리
- `build.sh` : 프로젝트 빌드를 자동화하는 Bash 스크립트
- `package.json` : 프로젝트의 메타데이터와 의존성 목록을 포함하는 파일
- `package-lock.json` : package.json에서 정의된 의존성의 정확한 버전을 기록하는 파일
- `postcss.config.js` : PostCSS 설정 파일
- `tailwind.config.js` : Tailwind CSS 설정 파일
- `tsconfig.jso` : TypeScript 설정 파일

### React 실행 방법
Front-end 어플리케이션은 `admin` 디렉터리 하위에서 아래 명령어를 사용하여 실행할 수 있습니다.

```sh
npm install  # 필요 시 의존성 패키지 설치
npm start    # 개발 서버 실행
```

### 빌드 및 배포
정적 파일을 서버 사이드의 `resources/static` 디렉터리에 배포하려면 빌드 과정을 거쳐야 합니다.<br>이를 위해 `build.sh` 스크립트를 실행하면 자동으로 빌드 및 배포가 진행됩니다. 예제 커맨드는 다음과 같습니다.

```sh
npm run build  # React 빌드 실행
sh build.sh     # 빌드된 파일을 백엔드 리소스 영역으로 이동
```

빌드된 정적 파일은 `build` 디렉터리에 생성되며, 이를 Back-end의 `resources/static` 디렉터리로 이동시키면 서버에서 해당 파일을 서빙할 수 있습니다.
