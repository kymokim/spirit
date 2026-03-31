# Spirit

`spirit`는 **한잔할까**의 메인 백엔드 서버입니다.

이 프로젝트는 매장 탐색, 주류 및 메뉴 정보 관리, 이벤트, 게시글, 알림, 인증 등 서비스의 핵심 기능을 담당합니다.

## 소개

- **한잔할까** 실서비스 백엔드
- Java 21, Spring Boot 기반 서버
- `store`, `drink`, `menu`, `event`, `post`, `comment`, `report`, `notification`, `auth` 등 도메인 중심 구조
- `agent` 모듈을 통한 AI 탐색 기능 포함

## 주요 기능

- 매장 등록, 제안, 점포 권한 및 관리자 관리
- 주류, 메뉴, 이벤트 정보 관리
- 피드형 게시글, 댓글, 좋아요, 저장, 공유 기능
- 인증 및 인가 API
- S3 기반 이미지 업로드 및 관리
- Swagger 기반 API 문서화
- Actuator, Prometheus 기반 모니터링

## 기술 스택

- Backend: Java, Spring Boot, Spring Security, Spring Data JPA, QueryDSL
- Database: MariaDB, H2
- Infra: Docker, AWS S3, GitHub Actions
- Observability: Actuator, Prometheus

## 참고

- 민감한 설정 파일과 운영 시크릿은 저장소에서 제외되어 있습니다.
- 로컬, 개발, 운영 환경별 설정은 별도로 관리합니다.
