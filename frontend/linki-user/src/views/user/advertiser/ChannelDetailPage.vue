<template>
  <div class="channel-detail-page">
    <div v-if="loading">로딩 중...</div>
    <div v-else-if="error">{{ error }}</div>
    <template v-else>
      <nav class="breadcrumb">
        <span>Home</span>
        <span class="breadcrumb-sep">/</span>
        <span>About</span>
      </nav>
      <div class="banner" :style="{ backgroundImage: channel?.bannerUrl ? `url(${channel.bannerUrl})` : 'none' }"></div>
      <div class="profile-section">
        <div class="profile-img" :style="{ backgroundImage: channel?.thumbnailUrl ? `url(${channel.thumbnailUrl})` : 'none' }"></div>
        <div class="profile-main">
          <div class="profile-header-row">
            <h1 class="channel-name">{{ channel.name }}</h1>
            <span class="platform-badge youtube-badge">
              <a :href="channel?.youtubeUrl" target="_blank" rel="noopener noreferrer" class="youtube-link">
                <svg class="youtube-icon" width="28" height="20" viewBox="0 0 28 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect width="28" height="20" rx="8" fill="#FF0000"/>
                  <path d="M19.5 10.0001C19.5 10.0001 19.5 7.83341 19.2 6.66675C19.0333 6.00008 18.5333 5.50008 17.8667 5.33341C16.7 5.00008 14 5.00008 14 5.00008C14 5.00008 11.3 5.00008 10.1333 5.33341C9.46667 5.50008 8.96667 6.00008 8.8 6.66675C8.5 7.83341 8.5 10.0001 8.5 10.0001C8.5 10.0001 8.5 12.1667 8.8 13.3334C8.96667 14.0001 9.46667 14.5001 10.1333 14.6667C11.3 15.0001 14 15.0001 14 15.0001C14 15.0001 16.7 15.0001 17.8667 14.6667C18.5333 14.5001 19.0333 14.0001 19.2 13.3334C19.5 12.1667 19.5 10.0001 19.5 10.0001ZM12.5 12.5V7.50008L16.5 10.0001L12.5 12.5Z" fill="white"/>
                </svg>
              </a>
            </span>
           </div>
          <div class="profile-meta-row">
            <div class="channel-category">{{ channel.category }}</div>
            <span class="star-rating">
              <template v-if="reviewCount > 0">
                <span v-for="n in 5" :key="n">
                  <svg v-if="reviewAvg >= n" width="20" height="20" viewBox="0 0 20 20" fill="#FFC107"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                  <svg v-else-if="reviewAvg >= n-0.5" width="20" height="20" viewBox="0 0 20 20"><defs><linearGradient :id="'half'+n"><stop offset="50%" stop-color="#FFC107"/><stop offset="50%" stop-color="#eee"/></linearGradient></defs><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5" :fill="'url(#half'+n+')'"/></svg>
                  <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="#eee"><polygon points="10,2 12,7.5 18,7.5 13.5,11.5 15,18 10,14.5 5,18 6.5,11.5 2,7.5 8,7.5"/></svg>
                </span>
                <span class="review-avg">{{ reviewAvg.toFixed(1) }}</span>
              </template>
            </span>
            <span class="review-count" v-if="reviewCount > 0">({{ reviewCount }} Reviews)</span>

          </div>
          <div class="profile-info-row">
            <span>구독자 수 <b>{{ channel.subscribers }}</b></span>
            <span>가입일 <b>{{ formatJoinDate(channel.createdAt) }}</b></span>
            <span>총영상수 <b>{{ channel.videoCount }}개</b></span>
            <span>국가 <b>{{ formatCountry(channel.country) }}</b></span>
          </div>
        </div>
      </div>
      <div class="video-stats-box">
        <div class="video-stats-title">
          ✔️ 최근 30개 영상 통계 데이터
          <div class="info-icon-container">
            <div class="info-icon" @click="showDataInfoTooltip = !showDataInfoTooltip">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="10" cy="10" r="9" stroke="#8C30F5" stroke-width="2" fill="white"/>
                <path d="M10 7V13M10 4.5V5.5" stroke="#8C30F5" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <div v-if="showDataInfoTooltip" class="data-info-tooltip">
              <div class="tooltip-content">
                <div class="tooltip-title">데이터 수집 정보</div>
                <div class="tooltip-text">
                  {{ formatDataCollectionDate(channel?.collectedAt) }} 기준으로 수집된 데이터입니다.
                </div>
                <div class="tooltip-note">
                  * 30개 영상의 평균 통계를 기반으로 계산됩니다.
                </div>
              </div>
              <div class="tooltip-arrow"></div>
            </div>
          </div>
        </div>
        <div class="video-stats-list">
          <div class="video-stat-item">
            <div class="stat-label">영상 수</div>
            <div class="stat-value">{{ channel.videoCount }}</div>
          </div>
          <div class="video-stat-item">
            <div class="stat-label">영상별 평균 조회수</div>
            <div class="stat-value">{{ channel.avgViewCount }}</div>
          </div>
          <div class="video-stat-item">
            <div class="stat-label">평균 댓글수</div>
            <div class="stat-value">{{ channel.avgCommentCount }}</div>
          </div>
          <div class="video-stat-item">
            <div class="stat-label">평균 좋아요 수</div>
            <div class="stat-value">{{ channel.avgLikeCount }}</div>
          </div>
        </div>
      </div>
      <div class="like-ratio-bar-chart-box">
        <div class="bar-charts-row">
          <div class="bar-chart-item">
            <h2>조회수 대비 좋아요 비율</h2>
            <LikeRatioBarChart v-if="id && channels.length > 0" :channels="channels" :channelId="id" />
            <div v-else>차트 로딩 중...</div>
          </div>
          <div class="bar-chart-item">
            <h2>조회수 대비 댓글 비율</h2>
            <CommentRatioBarChart v-if="id && channels.length > 0" :channels="channels" :channelId="id" />
            <div v-else>차트 로딩 중...</div>
          </div>
        </div>
      </div>
      <div class="subscriber-graph-box">
        <div class="subscriber-graph-header">
          <span class="graph-title">채널 구독자 수 변화량</span>
          <div class="graph-tabs">
            <button class="graph-tab active">7일</button>
          </div>
        </div>
        <div class="graph-rate-row">
          <span class="graph-rate">{{ subscriberGrowthRate }}</span>
          <span class="graph-rate-label">상승률</span>
        </div>
        <SubscriberHistoryChart v-if="id" :channelId="id" :period="'7일'" />
        <div v-else>구독자 차트 로딩 중...</div>
      </div>
      
      <div class="tab-section">
        <button class="tab" :class="{ active: tab === 'intro' }" @click="tab = 'intro'">채널 소개</button>
        <button class="tab" :class="{ active: tab === 'review' }" @click="tab = 'review'">리뷰<span v-if="reviewCount"> ({{ reviewCount }})</span></button>

      </div>
      <div v-if="tab === 'intro'">
        <div class="intro-section">
          <h2>소개</h2>
          <p class="intro-text">
            {{ channel.influencerIntro || '인플루언서 소개글이 등록되지 않았습니다.' }}
          </p>
        </div>
      </div>
      <div v-else>
        <ReviewTab v-if="id" :channelId="id" @review-stats="onReviewStats" />
        <div v-else>리뷰 탭 로딩 중...</div>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import SubscriberHistoryChart from '@/components/user/advertiser/SubscriberHistoryChart.vue'
import LikeRatioBarChart from '@/components/user/advertiser/LikeRatioBarChart.vue'
import CommentRatioBarChart from '@/components/user/advertiser/CommentRatioBarChart.vue'
import ReviewTab from '@/components/user/advertiser/ReviewTab.vue'
import { reviewApi } from '@/api/advertiser/advertiser-review'
import channelApi from '@/api/advertiser/advertiser-channel'

const route = useRoute()
const channel = ref(null)
const loading = ref(true)
const error = ref(null)
const channels = ref([])
const subscriberHistory = ref([])

// channelId는 String 타입이므로 숫자 변환하지 않음
const id = computed(() => {
  const channelId = route.params.id
  console.log('현재 라우트 파라미터 id:', channelId)
  console.log('route.params 전체:', route.params)
  return channelId
})

console.log('초기 id 값:', id.value)

const reviewCount = ref(0)
const reviewAvg = ref(0)

// 구독자 상승률 - 7일간 데이터 기반 계산
const subscriberGrowthRate = computed(() => {
  if (!subscriberHistory.value || subscriberHistory.value.length < 2) {
    return '데이터 없음'
  }
  
  // 모든 구독자 히스토리 데이터를 날짜순으로 정렬
  const sortedHistory = [...subscriberHistory.value]
    .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
  
  if (sortedHistory.length < 2) {
    return '데이터 부족'
  }
  
  // 가장 오래된 데이터와 가장 최신 데이터 사용
  const earliest = sortedHistory[0]
  const latest = sortedHistory[sortedHistory.length - 1]
  
  if (!latest || !earliest || earliest.subscriberCount === 0) {
    return '데이터 없음'
  }
  
  // 구독자 수 증가율 계산
  const growthRate = ((latest.subscriberCount - earliest.subscriberCount) / earliest.subscriberCount) * 100
  const sign = growthRate >= 0 ? '+' : ''
  
  console.log('7일간 구독자 상승률 계산:', {
    totalDataCount: sortedHistory.length,
    earliest: {
      count: earliest.subscriberCount,
      date: earliest.collectedAt
    },
    latest: {
      count: latest.subscriberCount,
      date: latest.collectedAt
    },
    difference: latest.subscriberCount - earliest.subscriberCount,
    growthRate: growthRate,
    formatted: `${sign}${growthRate.toFixed(1)}%`
  })
  
  return `${sign}${growthRate.toFixed(1)}%`
})

async function fetchReviewStatsOnEnter() {
  const { avg, count } = await reviewApi.getReviewStats(id.value)
  reviewCount.value = count
  reviewAvg.value = avg
}

function onReviewStats({ count, avg }) {
  reviewCount.value = count
  reviewAvg.value = avg
}

// 구독자 히스토리 데이터 가져오기
async function fetchSubscriberHistory() {
  try {
    if (!id.value) return
    
    console.log('구독자 히스토리 조회 시작:', id.value)
    const data = await channelApi.getSubscriberHistory(id.value)
    console.log('구독자 히스토리 응답:', data)
    
    if (Array.isArray(data)) {
      // 날짜순으로 정렬
      const sortedHistory = data
        .filter(item => String(item.channelId) === String(id.value))
        .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
      
      subscriberHistory.value = sortedHistory
      console.log('정렬된 구독자 히스토리:', subscriberHistory.value)
    } else {
      subscriberHistory.value = []
    }
  } catch (error) {
    console.error('구독자 히스토리 조회 중 오류:', error)
    subscriberHistory.value = []
  }
}

onMounted(async () => {
  loading.value = true
  try {
    console.log('=== ChannelDetailPage onMounted 시작 ===')
    console.log('채널 ID:', id.value)
    
    // 먼저 id가 존재하는지 확인
    if (!id.value) {
      throw new Error('채널 ID가 존재하지 않습니다.')
    }
    
    // 1. 모든 채널 데이터 가져오기 - LikeRatioBarChart전달용(전체 채널 평균) 
    console.log('전체 채널 목록 조회 시작...')
    const channelsData = await channelApi.getAllChannels(0, 50) // limit을 50으로 증가
    console.log('Raw API response:', channelsData)
    
    // API 응답이 페이지네이션 구조인지 확인
    if (channelsData && channelsData.channels) {
      channels.value = channelsData.channels
      console.log('페이지네이션 응답에서 채널 목록 추출:', channels.value.length, '개')
    } else if (Array.isArray(channelsData)) {
      channels.value = channelsData
      console.log('직접 배열 응답 사용:', channels.value.length, '개')
    } else {
      channels.value = []
      console.warn('예상하지 못한 응답 구조:', channelsData)
    }
    
    // 2. 채널 Id로 해당 채널 데이터 가져오기
    console.log('채널 상세 데이터 조회 시작 - channelId:', id.value)
    channel.value = await channelApi.getChannelById(id.value)
    console.log('Channel detail data:', channel.value)
    console.log('Channel collectedAt:', channel.value?.collectedAt)
    console.log('Channel collectedAt type:', typeof channel.value?.collectedAt)
    
    // 3. 현재 채널이 전체 목록에 있는지 확인하고 없으면 추가
    const currentChannelExists = channels.value.some(c => 
      String(c.channelId) === String(id.value) || String(c.id) === String(id.value)
    )
    
    if (!currentChannelExists && channel.value) {
      console.log('현재 채널이 전체 목록에 없어서 추가합니다.')
      // 현재 채널을 차트용 데이터 형식으로 변환하여 추가
      const currentChannelForChart = {
        channelId: channel.value.channelId || id.value,
        id: channel.value.channelId || id.value,
        channelName: channel.value.name,
        avgViewCount: channel.value.avgViewCount || 0,
        avgLikeCount: channel.value.avgLikeCount || 0,
        avgCommentCount: channel.value.avgCommentCount || 0,
        subscriberCount: channel.value.subscribers || 0,
        category: channel.value.category,
        description: channel.value.description
      }
      
      channels.value = [currentChannelForChart, ...channels.value]
      console.log('현재 채널 추가 완료. 총 채널 수:', channels.value.length)
    } else {
      console.log('현재 채널이 전체 목록에 이미 존재합니다.')
    }
    
    // 첫 번째 채널의 구조 확인
    if (channels.value.length > 0) {
      console.log('첫 번째 채널 데이터 구조:', channels.value[0])
      console.log('사용 가능한 필드들:', Object.keys(channels.value[0]))
    }
    
    // 현재 채널이 목록에 있는지 최종 확인
    const foundChannel = channels.value.find(c => 
      String(c.channelId) === String(id.value) || String(c.id) === String(id.value)
    )
    console.log('현재 채널 검색 결과:', foundChannel ? '찾음' : '못찾음')
    if (foundChannel) {
      console.log('찾은 채널 데이터:', foundChannel)
    }
    
    // 4. 리뷰 통계도 진입시 바로 fetch
    console.log('리뷰 통계 조회 시작...')
    await fetchReviewStatsOnEnter()
    
    // 5. 구독자 히스토리 데이터 가져오기
    console.log('구독자 히스토리 조회 시작...')
    await fetchSubscriberHistory()
    
    // 6. 차트 렌더링 확인
    console.log('=== 차트 렌더링 조건 확인 ===')
    console.log('id 존재:', !!id.value)
    console.log('channels 길이:', channels.value.length)
    console.log('차트 렌더링 조건:', id.value && channels.value.length > 0)
    console.log('=== ChannelDetailPage onMounted 완료 ===')
    
  } catch (err) {
    console.error('Error in onMounted:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
})

// 7일로 고정
const period = '7일'

const tab = ref('intro')

const showDataInfoTooltip = ref(false)

function formatDataCollectionDate(dateStr) {
  console.log('formatDataCollectionDate 호출됨:', dateStr, typeof dateStr)
  
  if (!dateStr) {
    console.log('dateStr이 없음')
    return '데이터 수집일 정보 없음'
  }
  
  try {
    let date
    
    // 이미 Date 객체인 경우
    if (dateStr instanceof Date) {
      date = dateStr
    }
    // 문자열인 경우 Date 객체로 변환
    else if (typeof dateStr === 'string') {
      date = new Date(dateStr)
    }
    // 숫자(timestamp)인 경우
    else if (typeof dateStr === 'number') {
      date = new Date(dateStr)
    }
    else {
      console.log('알 수 없는 날짜 형식:', dateStr)
      return '데이터 수집일 정보 없음'
    }
    
    // 유효한 날짜인지 확인
    if (isNaN(date.getTime())) {
      console.log('유효하지 않은 날짜:', dateStr)
      return '데이터 수집일 정보 없음'
    }
    
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    
    const formattedDate = `${year}년 ${month}월 ${day}일`
    console.log('포맷된 날짜:', formattedDate)
    
    return formattedDate
  } catch (error) {
    console.error('날짜 포맷팅 오류:', error)
    return '데이터 수집일 정보 없음'
  }
}

// 툴팁 외부 클릭 시 닫기
function handleClickOutside(event) {
  const tooltipContainer = event.target.closest('.info-icon-container')
  if (!tooltipContainer) {
    showDataInfoTooltip.value = false
  }
}

// 국가 코드를 한국어로 변환하는 함수
function formatCountry(countryCode) {
  if (!countryCode) {
    return '한국'
  }
  
  const country = countryCode.toUpperCase()
  
  switch (country) {
    case 'KR':
      return '한국'
    case 'US':
      return '미국'
    case 'JP':
      return '일본'
    case 'CN':
      return '중국'
    case 'GB':
      return '영국'
    case 'DE':
      return '독일'
    case 'FR':
      return '프랑스'
    case 'UNKNOWN':
      return '한국'
    default:
      return '한국'
  }
}

// 가입일을 YYYY년 MM월 DD일 형태로 포맷하는 함수
function formatJoinDate(dateStr) {
  if (!dateStr) {
    return '가입일 정보 없음'
  }
  
  try {
    let date
    
    // 이미 Date 객체인 경우
    if (dateStr instanceof Date) {
      date = dateStr
    }
    // 문자열인 경우 Date 객체로 변환
    else if (typeof dateStr === 'string') {
      date = new Date(dateStr)
    }
    // 숫자(timestamp)인 경우
    else if (typeof dateStr === 'number') {
      date = new Date(dateStr)
    }
    else {
      return '가입일 정보 없음'
    }
    
    // 유효한 날짜인지 확인
    if (isNaN(date.getTime())) {
      return '가입일 정보 없음'
    }
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    
    return `${year}년 ${month}월 ${day}일`
  } catch (error) {
    console.error('가입일 포맷팅 오류:', error)
    return '가입일 정보 없음'
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

</script>

<style scoped>
:global(body) {
  overflow-x: hidden !important;
}
.channel-detail-page {
  position: relative;
  width: 100%;
  margin: 0;
  background: #fff;
  padding: 100px;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
  font-size: 19px;
  box-sizing: border-box;
  min-height: 100vh;
  overflow-x: hidden;
}
.breadcrumb {
  font-size: 15px;
  color: #888;
  margin: 32px 0 18px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.breadcrumb-sep {
  color: #bbb;
}
.banner {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 450px;
  background: #8C30F5;
  border-radius: 48px 48px 0 0;
  z-index: 1;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.profile-section,
.stats-section,
.intro-section {
  width: 100%;
  min-width: 0;
  margin: 0;
  box-sizing: border-box;
  padding: 0;
  overflow-x: hidden;
}
.profile-section {
  display: flex;
  align-items: flex-end;
  gap: 36px;
  margin-top: 200px;
  margin-bottom: 32px;
  position: relative;
  z-index: 2;
}
.profile-img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: #ccc;
  border: 6px solid #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  margin-left: 32px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
.profile-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.profile-header-row {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 6px;
}
.channel-name {
  font-size: 2.6rem;
  font-weight: 700;
  color: #f8f5f5;
  margin: 0;
}
.platform-badge {
  background: #FF0000;
  color: #fff;
  font-weight: 700;
  border-radius: 5px;
  padding: 4px 18px;
  margin-left: 8px;
  margin-top:20px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.platform-badge .category {
  background: none;
  color: #fff;
  font-weight: 700;
  border-radius: 0;
  padding: 0;
  font-size: 1.1rem;
  margin: 0;
}
.youtube-icon {
  display: inline-block;
  vertical-align: middle;
}
.profile-meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
  font-size: 1.25rem;
}
.channel-category{
  background-color: #fffeff ;
  color: #8C30F5;
  border: 1px solid #8C30F5;
  font-weight: 700;
  border-radius: 8px;
  padding: 7px 28px;
  font-size: 1.1rem;
}
.star-rating {
  color: #FFC107;
  font-size: 1.1rem;
  margin-left: 10px;
}
.review-count {
  color: #888;
  font-size: 1rem;
  margin-left: 4px;
}
.profile-info-row {
  display: flex;
  gap: 24px;
  color: #444;
  font-size: 1.25rem;
  margin-top: 6px;
}
.profile-info-row b {
  color: #222;
  font-weight: 700;
  margin-left: 2px;
}
.stats-section {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  margin: 48px 0 0 0;
}
.stat-box {
  flex: 1 1 0;
  min-width: 240px;
  max-width: 100%;
  background: #fafaff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(140,48,245,0.04);
  padding: 28px 24px 18px 24px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.stat-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 1rem;
  color: #888;
  margin-bottom: 2px;
}
.stat-value {
  font-size: 2.1rem;
  font-weight: 800;
  color: #2D3A8C;
  margin-bottom: 2px;
}
.stat-diff {
  font-size: 1rem;
  color: #ff4d4f;
  margin-bottom: 2px;
}
.stat-diff .down {
  color: #ff4d4f;
  font-weight: 700;
}
.stat-desc {
  font-size: 0.98rem;
  color: #888;
  margin-bottom: 8px;
}
.stat-graph {
  width: 100%;
  height: 80px;
  margin-top: 8px;
  display: flex;
  align-items: flex-end;
}
.graph-placeholder {
  width: 100%;
  height: 80px;
  background: linear-gradient(90deg, #e0e7ff 0%, #f5f0ff 100%);
  border-radius: 8px;
}
.tab-section {
  display: flex;
  gap: 16px;
  margin: 48px 0 0 0;
  border-bottom: 2px solid #eee;
  }
.tab {
  background: none;
  border: none;
  font-size: 1.3rem;
  font-weight: 700;
  color: #888;
  padding: 10px 32px 12px 32px;
  border-radius: 16px 16px 0 0;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: color 0.2s, border 0.2s;
}
.tab.active {
  color: #8C30F5;
  border-bottom: 3px solid #8C30F5;
  background: #F5F0FF;
  font-weight: 700;
}
.intro-section {
  margin-top: 32px;
  padding: 0 60px;
  width: 100%;
  max-width: 1440px;
  min-width: 0;
  margin-left: auto;
  margin-right: auto;
  overflow-x: hidden;
  margin-bottom : 50px;
}
.intro-section h2 {
  font-size: 1.7rem;
  font-weight: 800;
  color: #222;
  margin-bottom: 18px;
}
.intro-text {
  font-size: 1.45rem;
  color: #444;
  margin-bottom: 18px;
  line-height: 1.7;
}
.intro-list {
  list-style: disc inside;
  color: #888;
  font-size: 1.2rem;
  margin-left: 18px;
  margin-top: 8px;
}
.intro-list li {
  margin-bottom: 4px;
}
@media (max-width: 900px) {
  .channel-detail-page {
    max-width: 100vw;
    width: 100vw;
    padding: 0;
    overflow-x: hidden;
  }
  .profile-section,
  .stats-section,
  .intro-section {
    padding: 0;
  }
  .profile-section {
    flex-direction: column;
    align-items: center;
    gap: 18px;
    margin-top: 180px;
    margin-bottom: 24px;
  }
  .profile-img {
    margin-left: 0;
  }
}
.subscriber-graph-box {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 32px 24px 24px 24px;
  margin: 32px 0;
}
.subscriber-graph-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.graph-title {
  font-size: 2rem;
  font-weight: 700;
}
.graph-tabs {
  display: flex;
  gap: 8px;
}
.graph-tab {
  background: #fff;
  border: 1.5px solid #FF0050;
  color: #FF0050;
  border-radius: 8px;
  padding: 6px 18px;
  font-weight: 700;
  font-size: 1.3rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.graph-tab.active {
  background: #FF0050;
  color: #fff;
}
.graph-rate-row {
  margin-bottom: 8px;
  font-size: 1.5rem;
  color: #FF0050;
  font-weight: 700;
}
.graph-rate-label {
  color: #888;
  font-weight: 400;
  margin-left: 8px;
}
.video-stats-box {
  background: #fafaff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
  padding: 32px 0 18px 0;
  margin: 32px 0 0 0;
  max-width: 100%;
}
.video-stats-title {
  font-size: 2rem;
  font-weight: 700;
  color: #0d0d0d;
  margin-bottom: 18px;
  padding-left: 40px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.video-stats-list {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  gap: 0;
  border-top: 1.5px solid #eee;
  border-bottom: 1.5px solid #eee;
  background: #fff;
}
.video-stat-item {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 0 18px 0;
  border-right: 1.5px solid #eee;
}
.video-stat-item:last-child {
  border-right: none;
}
.stat-label {
  font-size: 1.05rem;
  color: #888;
  margin-bottom: 8px;
  font-weight: 600;
}
.stat-value {
  font-size: 2.1rem;
  font-weight: 900;
  color: #8C30F5;
  letter-spacing: -1px;
}
@media (max-width: 900px) {
  .video-stats-list {
    flex-direction: column;
    border-top: none;
    border-bottom: none;
    gap: 0;
  }
  .video-stat-item {
    border-right: none;
    border-bottom: 1.5px solid #eee;
    padding: 18px 0 12px 0;
  }
  .video-stat-item:last-child {
    border-bottom: none;
  }
  .video-stats-title {
    padding-left: 18px;
  }
}
.review-section {
  margin-top: 36px;
  padding: 0 8px;
}
.review-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 28px;
}
.review-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
  padding: 28px 24px 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}
.review-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  margin-bottom: 8px;
}
.review-stars {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 1.1rem;
  color: #FFC107;
}
.review-score {
  font-weight: 700;
  color: #8C30F5;
  margin-left: 8px;
  font-size: 1.1rem;
}
.review-date {
  color: #888;
  font-size: 1rem;
  font-weight: 500;
  margin-top: 2px;
}
.review-text {
  font-size: 1.08rem;
  color: #23262F;
  line-height: 1.7;
  margin-top: 2px;
}
@media (max-width: 900px) {
  .review-list {
    grid-template-columns: 1fr;
    gap: 18px;
  }
  .review-section {
    padding: 0 2px;
  }
}
.video-stats-charts {
  display: flex;
  gap: 32px;
  margin: 32px 0 0 0;
}
.video-stats-chart {
  flex: 1 1 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(140,48,245,0.06);
  padding: 24px 18px 12px 18px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-width: 0;
}
.video-stats-chart-title {
  font-size: 1.13rem;
  font-weight: 800;
  color: #23262F;
  margin-bottom: 12px;
}
@media (max-width: 900px) {
  .video-stats-charts {
    flex-direction: column;
    gap: 18px;
  }
}
.like-ratio-bar-chart-box {
  margin-top: 32px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.bar-charts-row {
  display: flex;
  gap: 32px;
}
.bar-chart-item {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.bar-chart-item h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 18px;
}
@media (max-width: 900px) {
  .bar-charts-row {
    flex-direction: column;
    gap: 18px;
  }
}
.youtube-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
}
.youtube-link:hover {
  opacity: 0.8;
  transition: opacity 0.2s;
}
.info-icon-container {
  position: relative;
  display: inline-block;
}
.info-icon {
  cursor: pointer;
  transition: transform 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}
.info-icon:hover {
  transform: scale(1.1);
}
.data-info-tooltip {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fff;
  border: 2px solid #8C30F5;
  border-radius: 12px;
  padding: 16px;
  width: 280px;
  box-shadow: 0 8px 24px rgba(140, 48, 245, 0.15);
  z-index: 1000;
  margin-top: 8px;
  animation: fadeInUp 0.3s ease;
}
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
.tooltip-content {
  text-align: center;
}
.tooltip-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #8C30F5;
  margin-bottom: 8px;
}
.tooltip-text {
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}
.tooltip-note {
  font-size: 0.85rem;
  color: #666;
  font-style: italic;
}
.tooltip-arrow {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 8px solid #8C30F5;
}
</style> 