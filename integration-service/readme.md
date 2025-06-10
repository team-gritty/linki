
### 백엔드 패키지 구조
```
com.Gritty.Linki/
├ LinkiApplication.java
├ common/      ← 공통 컴포넌트 (현재 .gitkeep)
├ config/      ← WebConfig, SecurityConfig 등
├ domain/      ← 기능별 모듈 (admin, auth, user)
│   └ {모듈}/
│       ├ controller/
│       ├ service/
│       └ repository/
│       └ dto/
├ entity/      ← JPA 엔티티
├ exception/   ← 예외 처리
├ queries/     ← sql
├ util/        ← 유틸리티
└ vo/          ← vo
```

### 프론트 패키지 구조
```
└ frontend/            ← 프론트엔드 SPA 묶음
    ├ json-server/     ← 프론트 작업용 목데이터 서버 (db.json)
    ├ linki-admin/     ← 관리자용 Vue SPA
    └ linki-user/      ← 사용자용 Vue SPA
```


## 실행 가이드

### 1. Mock API 서버 실행

```
cd frontend/json-server
npm install
npx json-server --watch data.json
```

### 2. 프론트엔드 (관리자)

```
cd frontend/linki-admin
npm install
npm run dev
```

### 3.프론트엔드 (사용자)
```
cd frontend/linki-user
npm install
npm run dev
```

