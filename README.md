# Digimon Time Stranger 도감

## 프로젝트 목적

**디지몬 타임 스트레인저** 게임의 디지몬 **진화 트리**와 **도감 정보**를 확인할 수 있는 웹사이트를 제작한다.

## 예정 기능

- 디지몬 목록 및 상세 도감 정보 조회
- 디지몬별 진화 전·후 관계를 포함한 진화 트리 조회
- 진화 조건 및 관련 정보 제공

## 기술 기반

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Flyway

## 데이터베이스 실행

MySQL이 실행 중인 상태에서 애플리케이션을 시작하면 Flyway가 `db/migration`의
마이그레이션을 적용해 데이터베이스와 테이블을 생성한다. 기본 연결 대상은 로컬 MySQL의
`digimon_time_stranger` 데이터베이스다.

필요하면 아래 환경 변수로 연결 정보를 지정한다.

| 환경 변수 | 기본값 |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `digimon_time_stranger` |
| `DB_USERNAME` | 필수 환경변수 |
| `DB_PASSWORD` | 필수 환경변수 |
