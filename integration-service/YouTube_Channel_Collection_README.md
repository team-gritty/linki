# YouTube 채널 데이터 수집 시스템

YouTube Data API를 사용하여 키워드 기반으로 채널을 검색하고 수집하는 시스템입니다.

## 📋 주요 기능

1. **키워드 검색으로 채널 수집**: YouTube Data API를 통해 키워드로 채널 검색 및 저장
2. **구독자 수 추이 기록**: 수집된 채널들의 구독자 수 변화를 시간순으로 기록
3. **자동 업데이트**: 매일 자정에 기존 채널들의 구독자 수 자동 업데이트
4. **다양한 조회 기능**: 카테고리별, 키워드별, 구독자 수 범위별 채널 조회

## 🏗️ 데이터베이스 구조

### 1. collected_channels 테이블

수집된 채널의 기본 정보를 저장

```sql
- collected_channel_id: 수집된 채널 ID (PK)
- youtube_channel_id: YouTube 채널 ID
- channel_name: 채널명
- channel_url: 채널 URL
- channel_description: 채널 설명
- channel_thumbnail_url: 썸네일 이미지 URL
- channel_category: 카테고리
- channel_country: 국가
- subscriber_count: 구독자 수
- video_count: 동영상 수
- view_count: 총 조회수
- channel_created_at: 채널 생성일
- collected_at: 수집일시
- search_keyword: 검색 키워드
```

### 2. subscriber_history 테이블

구독자 수 추이를 기록

```sql
- history_id: 히스토리 ID (PK)
- collected_channel_id: 수집된 채널 ID (FK)
- subscriber_count: 구독자 수
- video_count: 동영상 수
- view_count: 총 조회수
- collected_at: 수집일시
```

## 🚀 API 엔드포인트

### 1. 채널 검색 및 저장

**POST** `/api/youtube/search-and-save`

**요청 파라미터:**

- `keyword` (required): 검색 키워드
- `category` (required): 카테고리
- `maxResults` (optional, default=10): 최대 결과 수

**예시:**

```bash
curl -X POST "http://localhost:8080/api/youtube/search-and-save?keyword=뷰티&category=BEAUTY&maxResults=15"
```

### 2. 수집된 채널 조회

**GET** `/api/youtube/collected-channels`

모든 수집된 채널을 최신순으로 조회

### 3. 카테고리별 채널 조회

**GET** `/api/youtube/collected-channels/category/{category}`

특정 카테고리의 채널들을 조회

### 4. 구독자 히스토리 조회

**GET** `/api/youtube/subscriber-history/{channelId}`

특정 채널의 구독자 수 추이를 조회

### 5. 구독자 수 수동 업데이트

**POST** `/api/youtube/update-subscriber-counts`

모든 수집된 채널의 구독자 수를 즉시 업데이트

### 6. 구독자 수 범위로 조회

**GET** `/api/youtube/collected-channels/subscribers?min=1000&max=100000`

구독자 수 범위로 채널 필터링

### 7. 수집 통계 조회

**GET** `/api/youtube/collection-stats`

전체 수집 통계 정보 조회

## 📝 사용 예시 (Postman)

### 1. 뷰티 관련 채널 15개 수집

```
POST http://localhost:8080/api/youtube/search-and-save
Query Parameters:
- keyword: 메이크업
- category: BEAUTY
- maxResults: 15
```

### 2. 게임 관련 채널 10개 수집

```
POST http://localhost:8080/api/youtube/search-and-save
Query Parameters:
- keyword: 게임
- category: ENTERTAINMENT
- maxResults: 10
```

### 3. 요리 관련 채널 수집

```
POST http://localhost:8080/api/youtube/search-and-save
Query Parameters:
- keyword: 요리
- category: FOOD
- maxResults: 20
```

## 🛠️ 설정

### application.yml 설정

```yaml
# YouTube Data API 설정
youtube:
  api:
    key: YOUR_YOUTUBE_API_KEY
  scheduler:
    enabled: true

# 스케줄링 활성화
spring:
  task:
    scheduling:
      enabled: true
```

### 카테고리 목록

- BEAUTY: 뷰티/화장품
- FASHION: 패션/스타일
- SPORTS: 스포츠/운동
- FOOD: 요리/먹방
- VLOG: 일상/브이로그
- TRAVEL: 여행
- MUSIC: 음악
- EDUCATION: 교육
- ANIMAL: 동물/펫
- ELECTRONICS: 전자기기/리뷰
- ENTERTAINMENT: 엔터테인먼트

## 🔄 자동 스케줄러

매일 자정(00:00)에 자동으로 모든 수집된 채널의 구독자 수가 업데이트됩니다.

스케줄러 비활성화:

```yaml
youtube:
  scheduler:
    enabled: false
```

## 📊 구독자 수 추이 그래프

각 채널의 구독자 수 변화를 시간순으로 추적할 수 있습니다:

1. 채널 최초 수집 시 첫 번째 구독자 수 기록
2. 매일 자정 자동 업데이트로 새로운 구독자 수 기록
3. `/api/youtube/subscriber-history/{channelId}` API로 추이 데이터 조회
4. 프론트엔드에서 차트 라이브러리로 그래프 시각화

## 🎯 주의사항

1. **YouTube API 할당량**: YouTube Data API는 일일 할당량이 있으므로 과도한 요청은 피해주세요.
2. **중복 수집 방지**: 이미 수집된 채널은 자동으로 중복 수집되지 않습니다.
3. **API 키 보안**: YouTube API 키는 환경변수나 설정 파일로 안전하게 관리하세요.

## 🔧 개발 팁

### 테스트용 스케줄러 활성화

개발 중에는 10분마다 업데이트하도록 설정할 수 있습니다:

```java
// SubscriberUpdateScheduler.java에서 주석 해제
@Scheduled(fixedRate = 600000) // 10분마다
public void updateSubscriberCountsForTesting() {
    // ...
}
```

### 디버깅

로그 레벨을 DEBUG로 설정하면 더 자세한 정보를 볼 수 있습니다:

```yaml
logging:
  level:
    com.Gritty.Linki.service.YouTubeChannelCollectionService: DEBUG
```
