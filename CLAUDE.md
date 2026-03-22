# Petmily 프로젝트 가이드

반려동물 커뮤니티 플랫폼 ("Pet" + "Family"). 채널 기반 게시판, JWT + OAuth2 인증, AWS S3 파일 관리를 갖춘 풀스택 프로젝트.

---

## 프로젝트 구조

```
petmily/
├── petmily-server/     Spring Boot 백엔드 API 서버
└── petmily-client/     React 프론트엔드 SPA
```

---

## 백엔드 (petmily-server)

### 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 2.7.10 |
| 빌드 도구 | Gradle 7.6.1 |
| ORM | Spring Data JPA / Hibernate |
| DB | MySQL (운영), H2 (개발) |
| 인증 | JWT (HMAC512, `com.auth0:java-jwt:4.2.1`) + OAuth2 (Google, Naver) |
| 파일 스토리지 | AWS S3 (`spring-cloud-starter-aws:2.2.6.RELEASE`) |
| API 문서 | Springfox Swagger 3.0 (`/swagger-ui/index.html`) |
| 기타 | Lombok, Spring Batch, Thymeleaf |

### 소스 디렉토리 구조

```
src/main/java/com/pet/petmily/
├── board/
│   ├── controller/     PostController.java
│   ├── dto/            PostDTO.java, ChannelDTO.java
│   ├── entity/         Post.java, Channel.java, Category.java, Favorite.java
│   ├── repository/     PostRepository.java, ChannelRepository.java ...
│   ├── response/       Response.java, ChannelResponse.java
│   └── service/        PostService.java, ChannelService.java
├── comment/
│   ├── controller/     CommentController.java
│   ├── dto/            CommentDTO.java
│   ├── entity/         Comment.java
│   ├── repository/     CommentRepository.java
│   ├── response/       CommentResponse.java, CommentAndPostResponse.java
│   └── service/        CommentService.java
├── scrap/
│   ├── controller/     ScrapController.java
│   ├── dto/            ScrapDTO.java
│   ├── entity/         Scrap.java
│   ├── repository/     ScrapRepository.java
│   └── service/        ScrapService.java
├── report/
│   ├── dto/            ReportDTO.java
│   ├── entity/         Report.java
│   ├── repository/     ReportRepository.java
│   └── ReportResponse.java
├── user/
│   ├── config/         SecurityConfig.java
│   ├── controller/     MemberController.java
│   ├── dto/            MemberSignUpDto.java, MemberUpdateDTO.java
│   ├── entity/         Member.java, BaseTimeEntity.java, Role.java, SocialType.java
│   ├── filter/         JwtAuthenticationProcessingFilter.java
│   │                   CustomJsonUsernamePasswordAuthenticationFilter.java
│   ├── handler/        LoginSuccessHandler.java, LoginFailureHandler.java
│   ├── oauth2/
│   │   ├── handler/    OAuth2LoginSuccessHandler.java, OAuth2LoginFailureHandler.java
│   │   ├── service/    CustomOAuth2UserService.java
│   │   └── userinfo/   GoogleOAuth2UserInfo.java, NaverOAuth2UserInfo.java
│   ├── repository/     MemberRepository.java
│   ├── service/        JwtService.java, LoginService.java, MemberService.java
│   └── util/           PasswordUtil.java
├── s3/
│   ├── component/      S3Component.java
│   └── service/        AWSS3UploadService.java, FileUploadService.java
├── config/             SwaggerConfig.java
└── exception/          GlobalExceptionHandler.java
```

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 컴파일만
./gradlew compileJava

# 실행
./gradlew bootRun

# Docker 빌드
docker build -t petmily-server .
docker run -p 8080:8080 petmily-server
```

### 환경 설정 (application.yml 필수 항목)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petmily
    username: {DB_USER}
    password: {DB_PASSWORD}

jwt:
  secretKey: {최소 32자 이상의 시크릿 키}
  access:
    expiration: 3600000        # 1시간 (ms)
    header: Authorization
  refresh:
    expiration: 1209600000     # 14일 (ms)
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

app:
  base-url: http://localhost:8080   # 게시글·채널 URL 생성에 사용
```

### API 엔드포인트 요약

#### 인증 (공개)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/login` | JSON 로그인 (body: `{email, password}`) |
| POST | `/sign-up` | 회원가입 |
| POST | `/sign-up/email-check` | 이메일 중복 확인 |
| POST | `/sign-up/nickname-check` | 닉네임 중복 확인 |

#### 채널
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/channel` | 전체 채널 조회 |
| POST | `/channel` | 채널 생성 |
| PUT | `/channel/update/{id}` | 채널 수정 (본인만) |
| DELETE | `/channel/delete/{id}` | 채널 삭제 (본인만, cascade) |
| GET | `/channel/mypage` | 내가 만든 채널 |
| GET | `/channel/search?query=` | 채널 검색 |

#### 게시글
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/channel/{channelId}/post` | 채널 게시글 목록 |
| GET | `/channel/{channelId}/post/{id}` | 게시글 상세 (조회수 +1) |
| POST | `/channel/{id}/post/write` | 게시글 작성 |
| PUT | `/channel/{channelId}/post/update/{id}` | 게시글 수정 (본인만) |
| DELETE | `/channel/{channelId}/post/delete/{id}` | 게시글 삭제 (본인만) |
| POST | `/channel/{channelId}/post/{postId}/like` | 좋아요 |
| POST | `/channel/{channelId}/post/{postId}/report` | 신고 (3회 누적 시 자동 삭제) |
| GET | `/post/mypage` | 내가 쓴 게시글 |
| GET | `/post/search?query=` | 게시글 검색 |

#### 즐겨찾기
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/channel/{channelId}/bookmark` | 즐겨찾기 추가 (본인 채널 불가) |
| DELETE | `/channel/{channelId}/bookmark` | 즐겨찾기 삭제 |
| GET | `/bookmark` | 내 즐겨찾기 목록 |

#### 댓글
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/post/{postId}/comment` | 댓글 목록 |
| POST | `/post/{postId}/comment/add` | 댓글 작성 |
| PUT | `/comment/{id}/update` | 댓글 수정 (본인만) |
| DELETE | `/comment/{id}/delete` | 댓글 삭제 (본인만) |
| POST | `/comment/{id}/like` | 댓글 좋아요 |
| GET | `/comment/mycomment` | 내가 쓴 댓글 |

#### 스크랩
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/post/{postId}/scrap` | 스크랩 추가 |
| DELETE | `/scrap/{scrapId}` | 스크랩 취소 |
| GET | `/scrap` | 내 스크랩 목록 |

#### 파일
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/upload` | S3 파일 업로드 (multipart) |
| GET | `/download/{filename}` | S3 파일 다운로드 |

### 인증 흐름

```
[로그인] POST /login {email, password}
  → 응답 헤더: Authorization: Bearer {accessToken}
               Authorization-Refresh: {refreshToken}

[인증 요청] 모든 보호된 API
  → 요청 헤더: Authorization: Bearer {accessToken}

[토큰 갱신] AccessToken 만료 시
  → 요청 헤더: Authorization-Refresh: {refreshToken}
  → 응답 헤더: 새 AccessToken, 새 RefreshToken (RTR 방식)
```

### 필터 체인 순서

```
LogoutFilter
  → JwtAuthenticationProcessingFilter   (JWT 검증 및 인증 처리)
  → CustomJsonUsernamePasswordAuthenticationFilter  (로그인 처리)
```

---

## 프론트엔드 (petmily-client)

### 기술 스택

| 항목 | 내용 |
|------|------|
| 프레임워크 | React 18 |
| 번들러 | Vite 5 |
| 라우팅 | React Router v6 |
| HTTP 클라이언트 | Axios (인터셉터로 토큰 자동 처리) |
| 스타일 | Vanilla CSS (외부 UI 라이브러리 없음) |

### 소스 디렉토리 구조

```
src/
├── api/
│   ├── axios.js        Axios 인스턴스 (요청/응답 인터셉터, 토큰 자동 갱신)
│   ├── auth.js         로그인, 회원가입, 중복 확인
│   ├── channel.js      채널 CRUD, 즐겨찾기
│   ├── post.js         게시글 CRUD, 좋아요, 신고, 스크랩
│   └── comment.js      댓글 CRUD, 좋아요
├── context/
│   └── AuthContext.jsx 전역 인증 상태 (user, login, logout)
├── components/
│   ├── Header.jsx      상단 네비게이션 + 검색
│   ├── Modal.jsx       삭제 확인 모달
│   └── PrivateRoute.jsx 인증 보호 라우트
├── pages/
│   ├── LoginPage.jsx
│   ├── SignupPage.jsx   이메일/닉네임 중복 확인 포함
│   ├── HomePage.jsx     채널 목록, 채널 생성, 즐겨찾기 토글
│   ├── ChannelPage.jsx  채널별 게시글 목록
│   ├── PostDetailPage.jsx  게시글 상세, 좋아요, 스크랩, 신고, 댓글
│   ├── PostWritePage.jsx
│   ├── PostEditPage.jsx
│   ├── MyPage.jsx       내 게시글/댓글/스크랩/즐겨찾기/채널 탭
│   └── SearchPage.jsx   게시글·채널 통합 검색
└── styles/
    └── global.css
```

### 빌드 및 실행

```bash
cd petmily-client

# 의존성 설치
npm install

# 환경 변수 설정
cp .env.example .env
# VITE_API_BASE_URL=http://localhost:8080 으로 편집

# 개발 서버 시작 (http://localhost:3000)
npm run dev

# 프로덕션 빌드
npm run build

# 빌드 결과 미리보기
npm run preview
```

### Vite 프록시 설정

개발 환경에서 `/api` 경로는 `localhost:8080`으로 프록시되어 CORS 없이 동작한다.
(`vite.config.js` 참고)

### 인증 토큰 관리

- `localStorage`에 `accessToken`, `refreshToken`, `userEmail` 저장
- Axios 요청 인터셉터에서 `Authorization: Bearer {token}` 헤더 자동 첨부
- 401 응답 시 `Authorization-Refresh` 헤더로 자동 갱신 후 원래 요청 재시도
- 갱신 실패 시 로컬스토리지 초기화 후 `/login` 리다이렉트

---

## 코드 컨벤션

### 백엔드 (Java)

#### 네이밍
```
클래스   : PascalCase         (PostService, ChannelDTO)
메서드   : camelCase          (writePost, getChannelById)
변수     : camelCase          (channelId, memberOptional)
상수     : UPPER_SNAKE_CASE   (NO_CHECK_URL)
패키지   : 소문자             (com.pet.petmily.board)
```

#### 계층 구조 패턴
```
Controller → Service → Repository → Entity
- Controller: 인증 처리, 요청/응답 변환만 담당
- Service:    @Transactional 비즈니스 로직
- Repository: JpaRepository 상속, 커스텀 쿼리 메서드
- Entity:     @Entity, BaseTimeEntity 상속 (createDate, lastModifiedDate 자동 관리)
```

#### 응답 포맷
```java
// 표준 응답 래퍼
new Response("상태 메시지", "설명", data)

// 채널 포함 응답
new ChannelResponse("상태", "설명", channelName, nickname, data)
```

#### Optional 처리 규칙
```java
// 금지: NPE 발생 가능
repository.findById(id).get()

// 필수: 항상 orElseThrow 사용
repository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지"))
```

#### 트랜잭션 규칙
- 데이터 변경 메서드: `@Transactional`
- 단순 조회 메서드: `@Transactional(readOnly = true)`
- 같은 서비스 내 self-invocation은 트랜잭션이 적용되지 않으므로 주의

#### 예외 처리
- 컨트롤러/서비스에서 `ResponseStatusException` 또는 `IllegalArgumentException` 사용
- 전역 예외는 `GlobalExceptionHandler` (`@ControllerAdvice`)에서 처리

#### 보안 규칙
- 비밀번호는 반드시 `PasswordEncoder`로 인코딩 후 저장
- 민감 정보 (토큰, 비밀번호)는 로그에 출력 금지
- 난수 생성은 반드시 `SecureRandom` 사용 (`java.util.Random` 금지)
- 권한 검증: 게시글/채널/댓글 수정·삭제 전 반드시 작성자 확인

### 프론트엔드 (JavaScript/React)

#### 네이밍
```
컴포넌트 파일  : PascalCase   (PostDetailPage.jsx)
함수형 컴포넌트: PascalCase   (function PostDetailPage())
훅·유틸 함수  : camelCase    (useAuth, handleSubmit)
API 함수      : camelCase    (getChannels, writePost)
CSS 클래스    : kebab-case   (.post-detail-header)
```

#### 컴포넌트 구조 패턴
```jsx
export default function PageName() {
  // 1. 훅 선언 (useParams, useNavigate, useAuth, useState, useEffect)
  // 2. 데이터 로딩 함수
  // 3. 이벤트 핸들러 (handle{Action} 네이밍)
  // 4. 조건부 렌더링 (로딩, 에러)
  // 5. JSX return
}
```

#### API 호출 규칙
- 모든 API 함수는 `src/api/` 디렉토리에 도메인별로 분리
- 직접 `axios` 사용 금지 → `src/api/axios.js`의 인스턴스 사용
- 로그인 API만 예외적으로 `axios` 직접 사용 (토큰 미첨부 목적)

#### 상태 관리
- 전역 인증 상태: `AuthContext` (Context API)
- 페이지별 로컬 상태: `useState`
- 서버 데이터 패칭: `useEffect` + `useState` (별도 캐싱 라이브러리 미사용)

---

## 도메인 모델

```
Category (1) ──── (N) Channel (1) ──── (N) Post (1) ──── (N) Comment
                      │                    │
                      │                    ├── (N) Report
                      │                    └── (N) Scrap
                      │
                   (N) Favorite

Member ──── Channel  (개설자)
Member ──── Post     (작성자)
Member ──── Comment  (작성자)
Member ──── Favorite (즐겨찾기)
Member ──── Scrap    (스크랩)
Member ──── Report   (신고자)
```

### 주요 비즈니스 규칙
- 게시글 신고 3회 누적 시 자동 삭제
- 자신의 게시글은 신고·즐겨찾기 불가
- 채널 삭제 시 하위 게시글·댓글 모두 cascade 삭제
- 이메일·닉네임은 전체 유니크
- 비밀번호 변경 시 현재 비밀번호 일치 확인 필수

---

## 주요 작업 이력

### 버그 수정

| 파일 | 내용 |
|------|------|
| `user/entity/Member.java` | `updateMember()` — 조건부 업데이트가 self-assignment에 의해 무효화되는 버그 수정 |
| `user/filter/JwtAuthenticationProcessingFilter.java` | catch 블록에서 토큰 검증 없이 재시도하여 무효 토큰이 인증 통과하던 버그 수정 |
| `board/controller/PostController.java` 외 다수 | `Optional.get()` → `orElseThrow()` 전환 (NPE 방지) |

### 보안 강화

| 파일 | 내용 |
|------|------|
| `user/service/MemberService.java` | 비밀번호 변경 전 현재 비밀번호 검증 추가 |
| `user/dto/MemberUpdateDTO.java` | `currentPassword` 필드 추가 |
| `board/controller/PostController.java` | 좋아요 API에 인증(`Authentication`) 추가 |
| `user/util/PasswordUtil.java` | `java.util.Random` → `SecureRandom` 교체, `System.out.println` 제거 |
| `user/handler/LoginSuccessHandler.java` | 로그에서 AccessToken 값 노출 제거 |

### 코드 품질 개선

| 파일 | 내용 |
|------|------|
| `board/service/PostService.java` | 하드코딩 URL → `@Value("${app.base-url}")` 외부화, `likePost`에 `@Transactional` 추가 |
| `board/service/ChannelService.java` | 하드코딩 URL → `@Value("${app.base-url}")` 외부화 |
| `comment/repository/CommentRepository.java` | `@EntityGraph(attributePaths = {"member"})` 추가로 N+1 쿼리 제거 |
| `comment/controller/CommentController.java` | `addComment` 중복 DB 쿼리 → 단일 `orElseThrow()` 으로 개선 |

### 프론트엔드 신규 구축 (`petmily-client/`)

React 18 + Vite 기반 SPA 전체 구축. 백엔드의 모든 주요 API와 연동.

---

## 알려진 미해결 이슈 (TODO)

- [ ] 게시글·댓글 좋아요 중복 방지 (별도 Like 엔티티 테이블 필요)
- [ ] 모든 목록 API에 페이지네이션 미적용 (대용량 시 성능 문제)
- [ ] 로그인 시도 횟수 제한 없음 (브루트포스 취약)
- [ ] 이메일 인증 없는 회원가입
- [ ] 파일 업로드 크기/타입 검증 없음
- [ ] API 버전 관리 미적용 (`/v1/` 등)
