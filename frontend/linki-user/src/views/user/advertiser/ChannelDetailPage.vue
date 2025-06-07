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
      <div class="banner"></div>
      <div class="profile-section">
        <div class="profile-img"></div>
        <div class="profile-main">
          <div class="profile-header-row">
            <h1 class="channel-name">{{ channel.name }}</h1>
            <span class="platform-badge youtube-badge">
              <svg class="youtube-icon" width="28" height="20" viewBox="0 0 28 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="28" height="20" rx="8" fill="#FF0000"/>
                <path d="M19.5 10.0001C19.5 10.0001 19.5 7.83341 19.2 6.66675C19.0333 6.00008 18.5333 5.50008 17.8667 5.33341C16.7 5.00008 14 5.00008 14 5.00008C14 5.00008 11.3 5.00008 10.1333 5.33341C9.46667 5.50008 8.96667 6.00008 8.8 6.66675C8.5 7.83341 8.5 10.0001 8.5 10.0001C8.5 10.0001 8.5 12.1667 8.8 13.3334C8.96667 14.0001 9.46667 14.5001 10.1333 14.6667C11.3 15.0001 14 15.0001 14 15.0001C14 15.0001 16.7 15.0001 17.8667 14.6667C18.5333 14.5001 19.0333 14.0001 19.2 13.3334C19.5 12.1667 19.5 10.0001 19.5 10.0001ZM12.5 12.5V7.50008L16.5 10.0001L12.5 12.5Z" fill="white"/>
              </svg>
            </span>
          </div>
          <div class="profile-meta-row">
            <button class="ad-btn">광고 제안</button>
            <span class="category">{{ channel.category }}</span>
            
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
            <span>가입일 <b>{{ channel.createdAt }}</b></span>
            <span>총영상수 <b>{{ channel.videoCount }}개</b></span>
            <span>국가 <b>{{ channel.country || '한국' }}</b></span>
          </div>
        </div>
      </div>
      <div class="video-stats-box">
        <div class="video-stats-title">최근 90일 영상 통계 데이터</div>
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
            <h3>조회수 대비 좋아요 비율</h3>
            <LikeRatioBarChart :channels="channels" :channelId="id" />
          </div>
          <div class="bar-chart-item">
            <h3>조회수 대비 댓글 비율</h3>
            <CommentRatioBarChart :channels="channels" :channelId="id" />
          </div>
        </div>
      </div>
      <div class="subscriber-graph-box">
        <div class="subscriber-graph-header">
          <span class="graph-title">채널 구독자 수 변화량</span>
          <div class="graph-tabs">
            <button
              v-for="p in ['30일', '15일']"
              :key="p"
              :class="['graph-tab', { active: period === p }]"
              @click="selectPeriod(p)"
            >{{ p }}</button>
          </div>
        </div>
        <div class="graph-rate-row">
          <span class="graph-rate">{{ chartData[period].rate }}</span>
          <span class="graph-rate-label">상승률</span>
        </div>
        <SubscriberHistoryChart :channelId="id" />
      </div>
      
      <div class="tab-section">
        <button class="tab" :class="{ active: tab === 'intro' }" @click="tab = 'intro'">채널 소개</button>
        <button class="tab" :class="{ active: tab === 'review' }" @click="tab = 'review'">리뷰<span v-if="reviewCount"> ({{ reviewCount }})</span></button>

      </div>
      <div v-if="tab === 'intro'">
        <div class="intro-section">
          <h2>소개</h2>
          <p class="intro-text">
            안녕하세요, 저는 2030대 여성을 타겟으로 뷰티와 라이프스타일 콘텐츠를 제작하고 있는 유튜버입니다. 솔직한 사용 후기와 자연스러운 생활 속 뷰티 꿀팁으로 유용한 제품을 추천하며, 광고보다는 일상 속 진정성 있는 리뷰를 선호합니다. 평균 조회수는 약 3만5천이며, 뷰티 제품의 경우 시청자 참여율이 높고, 브랜드의 핵심 메시지가 시청자의 언어로 잘 전달되는 데 집중하고 있으며, 구독자와의 신뢰를 바탕으로 한 광고를 진행합니다.
          </p>
          <ul class="intro-list">
            <li>Type Of Packing: Bottle</li>
            <li>Color: Green, Pink, Powder Blue, Purple</li>
            <li>Quantity Per Case: 100ml</li>
            <li>Ethyl Alcohol: 70%</li>
            <li>Piece In One: Carton</li>
          </ul>
        </div>
      </div>
      <div v-else>
        <ReviewTab :channelId="id" @review-stats="onReviewStats" />
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import VueApexCharts from 'vue3-apexcharts'
import SubscriberHistoryChart from './components/SubscriberHistoryChart.vue'
import LikeRatioBarChart from './components/LikeRatioBarChart.vue'

import CommentRatioBarChart from './components/CommentRatioBarChart.vue'
import ReviewTab from './components/ReviewTab.vue'
import { getReviewStats } from './useReviewStats.js'


const route = useRoute()
const channel = ref(null)
const loading = ref(true)
const error = ref(null)
const channels = ref([])

// 항상 숫자로 변환된 id 사용
const id = computed(() => Number(route.params.id))

const reviewCount = ref(0)
const reviewAvg = ref(0)

async function fetchReviewStatsOnEnter() {
  const { avg, count } = await getReviewStats(id.value)
  reviewCount.value = count
  reviewAvg.value = avg
}

function onReviewStats({ count, avg }) {
  reviewCount.value = count
  reviewAvg.value = avg
}


onMounted(async () => {
  loading.value = true
  try {
    // 1. 모든 채널 데이터 가져오기 - LikeRatioBarChart전달용(전체 채널 평균) 
    const response = await axios.get('http://localhost:3001/channels')
    channels.value = response.data
    // 2. 채널 Id로 해당 채널 데이터 가져오기
    const channelRes = await axios.get(`http://localhost:3001/channels/${id.value}`)
    channel.value = channelRes.data

    // 3. 리뷰 통계도 진입시 바로 fetch
    await fetchReviewStatsOnEnter()

  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
})

const period = ref('30일')
const chartOptions = ref({
  chart: { type: 'line', height: 320, toolbar: { show: false } },
  xaxis: { categories: [] },
  stroke: { curve: 'smooth', width: 3 },
  colors: ['#FF0050'],
  grid: { borderColor: '#eee' },
  tooltip: { enabled: true }
})

const chartData = ref({
  '30일': {
    categories: ['03-01', '03-05', '03-10', '03-15', '03-20', '03-25', '03-30'],
    data: [9000000, 9200000, 9500000, 9700000, 10000000, 10500000, 11000000],
    rate: '4%'
  },
  '15일': {
    categories: ['03-16', '03-18', '03-20', '03-22', '03-24', '03-26', '03-30'],
    data: [9700000, 9800000, 9900000, 10000000, 10200000, 10500000, 11000000],
    rate: '2%'
  }
})

const series = ref([
  {
    name: '구독자 수',
    data: chartData.value[period.value].data
  }
])

function selectPeriod(p) {
  period.value = p
  series.value[0].data = chartData.value[p].data
  chartOptions.value.xaxis.categories = chartData.value[p].categories
}
chartOptions.value.xaxis.categories = chartData.value[period.value].categories

const tab = ref('intro')


const likeBarOptions = ref({
  chart: { type: 'bar', toolbar: { show: false }, height: 320 },
  plotOptions: { bar: { borderRadius: 6, columnWidth: '40%' } },
  xaxis: { categories: ['내 채널', '전체'] },
  yaxis: { min: 0, max: 4, tickAmount: 4 },
  grid: { borderColor: '#eee' },
  dataLabels: { enabled: false },
  colors: ['#FF0050', '#7ED321'],
  tooltip: { enabled: true }
})
const likeBarSeries = ref([{ data: [1.2, 3.2] }])

const commentBarOptions = ref({
  chart: { type: 'bar', toolbar: { show: false }, height: 320 },
  plotOptions: { bar: { borderRadius: 6, columnWidth: '40%' } },
  xaxis: { categories: ['내 채널', '전체'] },
  yaxis: { min: 0, max: 0.7, tickAmount: 3 },
  grid: { borderColor: '#eee' },
  dataLabels: { enabled: false },
  colors: ['#FF0050', '#7ED321'],
  tooltip: { enabled: true }
})
const commentBarSeries = ref([{ data: [0.12, 0.52] }])
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
  padding: 0;
  font-family: 'Pretendard', 'Noto Sans KR', Arial, sans-serif;
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
  height: 200px;
  background: #8C30F5;
  border-radius: 48px 48px 0 0;
  z-index: 1;
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
  margin-top: 220px;
  margin-bottom: 32px;
  position: relative;
  z-index: 2;
}
.profile-img {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: #ccc;
  border: 6px solid #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  margin-left: 32px;
}
.profile-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.profile-header-row {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 6px;
}
.channel-name {
  font-size: 2.1rem;
  font-weight: 800;
  color: #222;
  margin: 0;
}
.platform-badge {
  background: #FF0000;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  border-radius: 16px;
  padding: 4px 18px;
  margin-left: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
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
}
.ad-btn {
  background: #8C30F5;
  color: #fff;
  font-weight: 700;
  border: none;
  border-radius: 8px;
  padding: 7px 28px;
  font-size: 1.1rem;
  margin-right: 12px;
  cursor: pointer;
}
.category {
  background: #f5f0ff;
  color: #8C30F5;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 14px;
  font-size: 1rem;
  margin-right: 4px;
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
  font-size: 1.05rem;
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
  font-size: 1.15rem;
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
}
.intro-section h2 {
  font-size: 1.3rem;
  font-weight: 800;
  color: #222;
  margin-bottom: 18px;
}
.intro-text {
  font-size: 1.08rem;
  color: #444;
  margin-bottom: 18px;
  line-height: 1.7;
}
.intro-list {
  list-style: disc inside;
  color: #888;
  font-size: 1.05rem;
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
  font-size: 1.2rem;
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
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.graph-tab.active {
  background: #FF0050;
  color: #fff;
}
.graph-rate-row {
  margin-bottom: 8px;
  font-size: 1.1rem;
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
  font-size: 1.18rem;
  font-weight: 800;
  color: #6B21E8;
  margin-bottom: 18px;
  padding-left: 40px;
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
</style> 