# 🐾 Petmily

반려동물 커뮤니티 플랫폼. 채널 기반 게시판, JWT + OAuth2 인증, AWS S3 파일 관리를 갖춘 풀스택 프로젝트입니다.

---

## 📌 원본 프로젝트

본 프로젝트는 **PRsH (이승현)** 님이 개발한 아래 저장소를 기반으로 작업한 프로젝트입니다.

> **원본 저장소:** [https://github.com/Pluteers/petmily-server](https://github.com/Pluteers/petmily-server)
> **원작자:** PRsH (이승현) — [@Pluteers](https://github.com/Pluteers)

원본 코드를 기반으로 버그 수정, 보안 강화, 코드 품질 개선, 프론트엔드 신규 구축 작업을 진행했습니다.

---

## 📁 프로젝트 구조

```
petmily/
├── petmily-server/     Spring Boot 백엔드 REST API
└── petmily-client/     React 프론트엔드 SPA
```

---

## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| 회원 인증 | 이메일/비밀번호 로그인, Google·Naver OAuth2 소셜 로그인 |
| JWT 인증 | AccessToken + RefreshToken 이중 토큰, RTR(Refresh Token Rotation) 방식 |
| 채널 | 카테고리 기반 채널 생성·수정·삭제, 즐겨찾기 |
| 게시글 | 채널별 게시글 CRUD, 조회수, 좋아요, 신고 (3회 누적 시 자동 삭제) |
| 댓글 | 댓글 CRUD, 좋아요 |
| 스크랩 | 게시글 저장 및 관리 |
| 파일 업로드 | AWS S3 연동 이미지 업로드 |
| 검색 | 게시글 제목 검색, 채널명 검색 |

---

## 🛠 기술 스택

### Backend

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 2.7.10 |
| 빌드 | Gradle 7.6.1 |
| DB | MySQL (운영) / H2 (개발) |
| ORM | Spring Data JPA / Hibernate |
| 인증 | Spring Security + JWT (`com.auth0:java-jwt:4.2.1`) + OAuth2 |
| 파일 | AWS S3 (`spring-cloud-starter-aws:2.2.6`) |
| API 문서 | Springfox Swagger 3.0 |
| 기타 | Lombok, Spring Batch, Thymeleaf |

### Frontend

| 항목 | 기술 |
|------|------|
| 프레임워크 | React 18 |
| 번들러 | Vite 5 |
| 라우팅 | React Router v6 |
| HTTP | Axios |
| 스타일 | Vanilla CSS |

---

## 🚀 실행 방법

### 사전 요구사항

- Java 17+
- Node.js 18+
- MySQL 8.0+
- AWS S3 버킷 (파일 업로드 기능 사용 시)

### 1. 백엔드 설정

```bash
cd petmily-server
```

`src/main/resources/application.yml` 생성 후 아래 내용 입력:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petmily?serverTimezone=Asia/Seoul
    username: {DB_USERNAME}
    password: {DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secretKey: {32자 이상의 시크릿 키}
  access:
    expiration: 3600000
    header: Authorization
  refresh:
    expiration: 1209600000
    header: Authorization-Refresh

cloud:
  aws:
    credentials:
      access-key: {AWS_ACCESS_KEY}
      secret-key: {AWS_SECRET_KEY}
    region:
      static: ap-northeast-2
    s3:
      bucket: {S3_BUCKET_NAME}
    stack:
      auto: false

app:
  base-url: http://localhost:8080

spring.security.oauth2.client.registration:
  google:
    client-id: {GOOGLE_CLIENT_ID}
    client-secret: {GOOGLE_CLIENT_SECRET}
    scope: email, profile
  naver:
    client-id: {NAVER_CLIENT_ID}
    client-secret: {NAVER_CLIENT_SECRET}
    redirect-uri: "{baseUrl}/login/oauth2/code/naver"
    authorization-grant-type: authorization_code
    scope: email, name
```

```bash
# 백엔드 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/petmily-0.0.1-SNAPSHOT.jar
```

### 2. 프론트엔드 설정

```bash
cd petmily-client

# 의존성 설치
npm install

# 환경 변수 설정
cp .env.example .env
# .env 파일에서 VITE_API_BASE_URL=http://localhost:8080 확인

# 개발 서버 실행
npm run dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000) 접속

### 3. Docker로 백엔드 실행

```bash
cd petmily-server

docker build -t petmily-server .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/petmily \
  -e SPRING_DATASOURCE_USERNAME={DB_USER} \
  -e SPRING_DATASOURCE_PASSWORD={DB_PW} \
  petmily-server
```

---

## 📖 API 문서

백엔드 실행 후 아래 URL에서 Swagger UI 확인:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🗂 주요 API 엔드포인트

### 인증 (공개)

```
POST /login                          # 로그인 (JSON body: {email, password})
POST /sign-up                        # 회원가입
POST /sign-up/email-check            # 이메일 중복 확인
POST /sign-up/nickname-check         # 닉네임 중복 확인
```

### 채널 / 게시글 (인증 필요)

```
GET    /channel                      # 채널 목록
POST   /channel                      # 채널 생성
GET    /channel/{channelId}/post     # 채널별 게시글 목록
POST   /channel/{id}/post/write      # 게시글 작성
POST   /channel/{channelId}/post/{postId}/like    # 좋아요
POST   /channel/{channelId}/post/{postId}/report  # 신고
POST   /channel/{channelId}/bookmark              # 즐겨찾기 추가
```

### 댓글 / 스크랩 (인증 필요)

```
GET    /post/{postId}/comment        # 댓글 목록
POST   /post/{postId}/comment/add    # 댓글 작성
POST   /post/{postId}/scrap          # 스크랩
GET    /scrap                        # 내 스크랩 목록
```

---

## 🔐 인증 흐름

```
1. POST /login → 응답 헤더에 AccessToken, RefreshToken 수신
2. 이후 모든 요청 헤더에 Authorization: Bearer {accessToken} 첨부
3. AccessToken 만료 시 Authorization-Refresh: {refreshToken} 헤더로 재발급 요청
4. RTR(Refresh Token Rotation) 방식: 재발급 시 RefreshToken도 함께 갱신
```

---

## 📋 변경 이력 (원본 대비)

### 버그 수정

- `Member.updateMember()` — self-assignment으로 인해 이메일·닉네임·비밀번호 업데이트가 실제로 적용되지 않던 버그 수정
- `JwtAuthenticationProcessingFilter` — 잘못된 토큰이 catch 블록을 통해 인증을 우회하던 버그 수정
- 전체 코드베이스에서 `Optional.get()` 무방비 사용 → `orElseThrow()` 전환

### 보안 강화

- 비밀번호 변경 시 현재 비밀번호 검증 추가 (`currentPassword` 필드)
- 좋아요 API에 인증(로그인) 요구 추가
- 임시 비밀번호 생성 시 `java.util.Random` → `SecureRandom` 교체
- 로그에서 AccessToken 값 노출 제거

### 코드 품질 개선

- 하드코딩된 도메인 URL (`http://petmily.duckdns.org`) → `@Value("${app.base-url}")` 외부화
- 댓글 조회 N+1 쿼리 → `@EntityGraph` 로 단일 쿼리 처리
- `likePost()` 메서드에 `@Transactional` 추가

### 신규 추가

- **프론트엔드 전체 구축** (`petmily-client/`) — React 18 + Vite 기반 SPA
  - 로그인, 회원가입, 채널 목록, 게시글 CRUD, 댓글 CRUD, 마이페이지, 검색 등 전 기능 구현
  - Axios 인터셉터를 통한 JWT 자동 갱신

---

## 📌 알려진 미해결 이슈

- [ ] 게시글·댓글 좋아요 중복 방지 미구현 (별도 Like 엔티티 필요)
- [ ] 목록 조회 API에 페이지네이션 미적용
- [ ] 로그인 시도 횟수 제한 없음
- [ ] 회원가입 시 이메일 인증 없음
- [ ] 파일 업로드 크기·타입 검증 없음

---

## 📜 라이선스

본 프로젝트는 원본 저장소([Pluteers/petmily-server](https://github.com/Pluteers/petmily-server))의 라이선스 정책을 따릅니다.
