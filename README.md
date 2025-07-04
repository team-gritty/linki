<div align=“center”>
  <img 
    src=“https://capsule-render.vercel.app/api?type=soft&color=7b21e8&height=120&text=LINKI%20PROJECT&animation=fadeIn&fontColor=ffffff&fontSize=60” 
    width=“100%”
  />
</div>

# Linki - 인플루언서 마케팅 플랫폼

## :memo: 프로젝트 개요

Linki는 인플루언서와 광고주를 연결하는 종합 마케팅 플랫폼입니다.   
인플루언서는 브랜드 캠페인에 참여하고, 광고주는 적합한 인플루언서를 찾아 마케팅을 진행할 수 있습니다.   
전자계약, 실시간 채팅, 자동 정산 등의 기능을 통해 안전하고 투명한 광고 생태계를 제공합니다.

### [ERD 바로가기](https://www.erdcloud.com/d/tHnS9EZLguhoSFaMD)
### [회원 페이지 바로가기](https://www.linki.kr)
### [관리자 페이지 바로가기](https://www.admin.linki.kr)
### [TEAM NOTION 바로가기 ](https://shorturl.at/dwkOo)
## :building_construction: 시스템 아키텍처

### 마이크로서비스 구조
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │   Discovery     │
│   (Vue.js)      │◄──►│   (Port: 8000)  │◄──►│   (Port: 8761)  │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
        │Integration   │ │Chat Service │ │Payment     │
        │Service       │ │             │ │Service     │
        │              │ │             │ │            │
        └──────────────┘ └─────────────┘ └────────────┘
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
        │Subscribe     │ │Admin        │ │Chatbot     │
        │Service       │ │Integration  │ │Service     │
        │              │ │Service      │ │            │
        └──────────────┘ └─────────────┘ └────────────┘
```



## :hammer_and_wrench: 기술 스택

### Backend
- **Framework**: Spring Boot 3.4.5+
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate + MyBatis
- **Security**: Spring Security + JWT
- **Message Queue**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Cache**: Redis
- **External APIs**: 
  - YouTube Data API v3
  - 유캔사인 전자계약 API
  - Toss Payments API
  - OpenAI GPT API

### Frontend
- **Framework**: Vue.js 3
- **State Management**: Pinia
- **Router**: Vue Router 4
- **HTTP Client**: Axios
- **Charts**: ApexCharts, ECharts
- **Real-time Communication**: WebSocket + STOMP

### Infrastructure
- **Cloud Storage**: Naver NCloud
- **Build Tool**: Gradle
- **Development Tools**: Vite, ESLint, Prettier

## :dart: 주요 기능

### :bust_in_silhouette: 사용자 관리
- **인플루언서**: 채널 등록, 캠페인 제안서 제출, 계약 관리
- **광고주**: 캠페인 생성, 인플루언서 선택, 계약서 작성
- **관리자**: 사용자 관리, 계약 승인, 정산 관리
- **OAuth 로그인**: Google 소셜 로그인 지원

### :bar_chart: 캠페인 & 계약 관리
- 캠페인 생성 및 관리
- 제안서 제출 및 검토
- 전자계약서 생성 (유캔사인 연동)
- 계약 상태 자동 업데이트
- 광고 이행 확인 시스템

### :speech_balloon: 실시간 커뮤니케이션
- WebSocket 기반 실시간 채팅
- 계약 진행 상황 알림
- SSE(Server-Sent Events) 기반 알림 시스템
- 이메일 알림 기능

### :credit_card: 결제 & 정산
- Toss Payments 연동 결제
- 구독 서비스 (인플루언서 전용)
- 자동 정산 시스템
- 환불 처리

### :chart_with_upwards_trend: 분석 & 리포팅
- YouTube 채널 통계 수집
- 캠페인 성과 분석
- 대시보드 및 차트 제공
- 인플루언서/광고주 평가 시스템

### :robot_face: AI 기능
- GPT 기반 챗봇 서비스
- 캠페인 추천 시스템
- 콘텐츠 분석

## :file_folder: 프로젝트 구조

```
linki/
├── backend/
│   ├── discovery-service/          # Eureka 서버
│   ├── apigateway-service/         # API 게이트웨이
│   ├── integration-service/        # 메인 비즈니스 로직
│   ├── admin-integration-service/  # 관리자 기능
│   ├── chat-service/              # 채팅 서비스
│   ├── payment-service/           # 결제 서비스
│   ├── subscribe-service/         # 구독 서비스
│   └── chatbot-service/           # 챗봇 서비스
├── frontend/
│   ├── linki-user/               # 사용자 웹 앱
│   ├── linki-admin/              # 관리자 웹 앱
│   └── json-server/              # 개발용 Mock API
└── quries/                       # 데이터베이스 스키마 & 더미데이터
```

## :rocket: 실행 방법

### 1. 사전 요구사항
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Apache Kafka 2.8+

### 2. 데이터베이스 설정
```sql
mysql -u root -p quries/talbeInit.sql
mysql -u root -p linkiDB < quries/dummydata.sql
```

### 3. 백엔드 서비스 실행 순서

#### 3.1 Discovery Service (Eureka Server)
```bash
cd discovery-service
./gradlew bootRun
# http://localhost:8761
```

#### 3.2 API Gateway
```bash
cd apigateway-service
./gradlew bootRun
# http://localhost:8000
```

#### 3.3 Core Services
```bash
# Integration Service (메인 서비스)
cd integration-service
./gradlew bootRun

# Chat Service
cd chat-service
./gradlew bootRun

# Payment Service
cd payment-service
./gradlew bootRun

# Subscribe Service
cd subscribe-service
./gradlew bootRun

# Admin Integration Service
cd admin-integration-service
./gradlew bootRun

# Chatbot Service
cd chatbot-service
./gradlew bootRun
```

### 4. 프론트엔드 실행


#### 4.1 사용자 웹 앱
```bash
cd frontend/linki-user
npm install
npm run dev
# http://localhost:3001
```
persistedstate API 추가
```bash
cd frontend/linki-user
npm install pinia-plugin-persistedstate@3
```

#### 4.2 관리자 웹 앱
```bash
cd frontend/linki-admin
npm install
npm run dev
# http://localhost:3002
```

## :wrench: 환경 설정

### Backend 설정 파일
각 서비스의 `application.yml` 또는 `application.properties`에서 다음 설정들을 확인하세요:

```yml
# 공통 설정
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/linkiDB
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
```

### 환경변수 설정
```bash
# JWT Secret Key
export SECRET_HS256=your_jwt_secret_key

# YouTube API Key
export YOUTUBE_API_KEY=your_youtube_api_key

# 유캔사인 API
export UCAN_SIGN_API_KEY=your_ucan_sign_key

# Toss Payments
export TOSS_CLIENT_KEY=your_toss_client_key
export TOSS_SECRET_KEY=your_toss_secret_key

# OpenAI API
export OPENAI_API_KEY=your_openai_api_key
```

## :bar_chart: 데이터베이스 스키마

### 주요 테이블
- **user**: 사용자 기본 정보
- **influencer**: 인플루언서 정보
- **advertiser**: 광고주 정보
- **channel**: YouTube 채널 정보
- **campaign**: 캠페인 정보
- **proposal**: 제안서 정보
- **contract**: 계약 정보
- **settlement**: 정산 정보
- **chat**: 채팅방 정보
- **message**: 메시지 정보

## :mag: API 문서

### 주요 API 엔드포인트

#### 사용자 관리
- `POST /v1/api/auth/login` - 로그인
- `POST /v1/api/auth/register` - 회원가입
- `GET /v1/api/user/profile` - 프로필 조회

#### 캠페인 관리
- `GET /v1/api/campaigns` - 캠페인 목록
- `POST /v1/api/campaigns` - 캠페인 생성
- `POST /v1/api/proposals` - 제안서 제출

#### 계약 관리
- `POST /v1/api/contracts` - 계약서 생성
- `GET /v1/api/contracts/{id}` - 계약서 조회
- `PUT /v1/api/contracts/{id}/complete` - 계약 완료

#### 채팅
- `GET /v1/chat-service/api/chats` - 채팅방 목록
- `WebSocket /ws/chat` - 실시간 채팅

#### 결제
- `POST /v1/payment-service/api/billing` - 빌링키 등록
- `POST /v1/subscribe-service/api/subscribe` - 구독 신청

## :closed_lock_with_key: 보안

- JWT 기반 인증/인가
- Spring Security 설정
- CORS 정책 적용
- API Rate Limiting
- 민감 정보 암호화

## :chart_with_upwards_trend: 성능 최적화

- Redis 캐싱 적용
- 데이터베이스 인덱스 최적화 (54개 인덱스 적용)
- Keyset 페이지네이션
- 비동기 처리 (Kafka 메시징)
- CDN 적용 (이미지/정적 파일)

## :handshake: 기여 방법

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m ‘Add some AmazingFeature’`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## :memo: 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## :busts_in_silhouette: 팀

- [신민혁](https://github.com/minhyeokshin)(팀장) : 데이터 분석/ 플렛폼 관리자 기능
- [고윤아](https://github.com/kya9505)(부팀장) : notion 및 문서관리 , 채팅
- [이정섭](https://github.com/dlwjdtjq001) : 네트워크 관리 , 결제 /환불
- [김성준](https://github.com/kimsj18) : 시큐리티 / 회원 / 챗봇
- [김선민](https://github.com/seonmin12) : 인플루언서 기능
- [정난희](https://github.com/Eveieve) : 광고주 기능


## :telephone_receiver: 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록하거나 이메일로 연락주세요.

---

**Linki** - 인플루언서와 브랜드를 연결하는 혁신적인 마케팅 플랫폼 :rocket:
