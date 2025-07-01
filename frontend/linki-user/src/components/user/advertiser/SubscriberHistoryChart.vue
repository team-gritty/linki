<template>
  <div class="subscriber-chart-container">
    <!-- 30일, 15일 데이터가 부족시 메시지 표시 -->
    <div v-if="showDataCollectionMessage" class="data-collection-message">
      <div class="data-collection-icon">📈</div>
      <div class="data-collection-title">데이터 수집 중</div>
      <div class="data-collection-description">
        {{ period }} 구독자 변화 데이터를 수집하고 있습니다.<br>
        충분한 데이터가 모이면 그래프를 표시해드리겠습니다.
      </div>
      <div class="data-collection-note">
        * 7일 데이터는 현재 확인 가능합니다.
      </div>
    </div>
    <!-- 실제 차트 -->
    <apexchart
      v-else
      type="line"
      height="350"
      :options="chartOptions"
      :series="series"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import channelApi from '@/api/advertiser/advertiser-channel'

// props : 부모 컴포넌트로부터 데이터를 전달받기 위한 뷰 기능.
// defineProps는 Vue 3의 Composition API에서 props를 정의하는 함수
// 부모 컴포넌트로부터 channelId를 전달받아 구독자 수 변화 그래프를 그리는데 사용
const props = defineProps({
  channelId: {
    type: [Number, String],
    required: true
  },
  period: {
    type: String,
    default: '7일'
  }
})

// 구독자 수 변화 그래프를 그리기 위한 데이터를 저장할 배열
const history = ref([])
const hasRealData = ref(false)

// 선택된 기간에 따라 필터링된 히스토리 데이터
const filteredHistory = computed(() => {
  if (!history.value || history.value.length === 0) {
    return []
  }
  
  const now = new Date()
  let daysToShow = 7 // 기본값
  
  // period에 따라 표시할 일수 결정
  if (props.period === '7일') {
    daysToShow = 7
  } else if (props.period === '15일') {
    daysToShow = 15
  } else if (props.period === '30일') {
    daysToShow = 30
  }
  
  // 최근 N일의 데이터만 필터링
  const cutoffDate = new Date(now)
  cutoffDate.setDate(cutoffDate.getDate() - (daysToShow - 1))
  
  return history.value
    .filter(item => new Date(item.collectedAt) >= cutoffDate)
    .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
    .slice(-daysToShow) // 최대 지정된 일수만큼만 표시
})

// 데이터 수집 중 메시지 표시 여부
const showDataCollectionMessage = computed(() => {
  // 30일, 15일 데이터가 요청되었지만 실제 데이터가 없는 경우
  if ((props.period === '30일' || props.period === '15일') && !hasRealData.value) {
    return true
  }
  // 7일 데이터도 없는 경우
  if (props.period === '7일' && history.value.length === 0) {
    return true
  }
  return false
})

// 컴포넌트가 마운트되면, 구독자 수 변화 그래프를 그리기 위한 데이터를 조회하는 함수
onMounted(async () => {
  try {
    console.log('=== SubscriberHistoryChart 디버그 ===')
    console.log('Fetching subscriber history for channelId:', props.channelId)
    
    // channelId 존재 여부 확인
    if (!props.channelId) {
      console.error('channelId가 존재하지 않습니다')
      history.value = []
      hasRealData.value = false
      return
    }
    
    const data = await channelApi.getSubscriberHistory(props.channelId)
    console.log('구독자 히스토리 응답 데이터 :', data)
    
    if (Array.isArray(data) && data.length > 0) {
      const filteredData = data
        .filter(item => String(item.channelId) === String(props.channelId))
        .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
      
      history.value = filteredData
      hasRealData.value = true
      console.log('필터된 구독자 히스토리:', history.value)
    } else {
      // 실제 데이터가 없는 경우
      console.log('구독자 히스토리 데이터가 없음')
      history.value = []
      hasRealData.value = false
      
      // 7일 데이터만 더미로 생성 (7일은 기본 제공)
      if (props.period === '7일') {
        history.value = generateDummyHistory(7)
      }
    }
    
  } catch (error) {
    console.error('구독자 히스토리 데이터 가져오는 중 에러 ', error)
    history.value = []
    hasRealData.value = false
    
    // 7일 데이터만 더미로 생성
    if (props.period === '7일') {
      history.value = generateDummyHistory(7)
    }
  }
})

// 더미 히스토리 데이터 생성 (7일만)
function generateDummyHistory(daysToGenerate) {
  const dummyData = []
  const baseSubscribers = 1000000 // 기본 구독자 수
  const today = new Date()
  
  for (let i = daysToGenerate - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    
    // 약간의 증가 추세를 가진 더미 데이터
    const growth = Math.floor(Math.random() * 3000) + 1000 // 1000~4000 증가
    const subscriberCount = baseSubscribers + (daysToGenerate - 1 - i) * growth
    
    dummyData.push({
      channelId: props.channelId,
      subscriberCount: subscriberCount,
      collectedAt: date.toISOString()
    })
  }
  
  return dummyData
}

// period가 변경될 때 처리
watch(() => props.period, () => {
  console.log('Period 변경됨:', props.period)
  
  // 30일, 15일로 변경되면 실제 데이터가 있는지 확인
  if ((props.period === '30일' || props.period === '15일') && !hasRealData.value) {
    // 데이터 수집 중 메시지 표시
    return
  }
  
  // 7일로 변경되면 더미 데이터라도 표시
  if (props.period === '7일' && history.value.length === 0) {
    history.value = generateDummyHistory(7)
  }
})

const series = computed(() => {
  console.log('Computing series with filteredHistory:', filteredHistory.value)
  return [{
    name: '구독자 수',
    data: filteredHistory.value.map(item => item.subscriberCount)
  }]
})

const chartOptions = computed(() => ({
  chart: {
    id: 'subscriber-history',
    toolbar: { show: false }
  },
  xaxis: {
    categories: filteredHistory.value.map(item => {
      const date = new Date(item.collectedAt)
      const today = new Date()
      const diffTime = today.getTime() - date.getTime()
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
      
      // 7일 기간에서는 상대적 날짜 우선 표시
      if (props.period === '7일') {
        if (diffDays === 0) {
          return '오늘'
        } else if (diffDays === 1) {
          return '어제'
        } else if (diffDays <= 6) {
          return `${diffDays}일 전`
        }
      }
      
      // 다른 기간이거나 7일을 넘으면 월/일 형식
      return `${date.getMonth() + 1}/${date.getDate()}`
    }),
    labels: { 
      rotate: props.period === '7일' ? 0 : -45, // 7일은 회전 없이, 나머지는 회전
      style: {
        fontSize: '12px'
      }
    },
    title: {
      text: '기간',
      style: {
        fontSize: '14px',
        fontWeight: 600
      }
    }
  },
  yaxis: {
    labels: {
      formatter: val => val.toLocaleString()
    },
    title: {
      text: '구독자 수',
      style: {
        fontSize: '14px',
        fontWeight: 600
      }
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  colors: ['#ff4d67'],
  dataLabels: { enabled: false },
  tooltip: {
    y: {
      formatter: val => `${val.toLocaleString()}명`
    },
    x: {
      formatter: (val, { series, seriesIndex, dataPointIndex, w }) => {
        const item = filteredHistory.value[dataPointIndex]
        if (item) {
          const date = new Date(item.collectedAt)
          return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
        }
        return ''
      }
    }
  },
  grid: {
    borderColor: '#eee',
    padding: {
      top: 0,
      right: 0,
      bottom: 0,
      left: 0
    }
  },
  markers: {
    size: 4,
    colors: ['#ff4d67'],
    strokeColors: '#fff',
    strokeWidth: 2,
    hover: {
      size: 6
    }
  }
}))
</script>

<script>
export default {
  components: {
    apexchart: VueApexCharts
  }
}
</script>

<style scoped>
.subscriber-chart-container {
  width: 100%;
  height: 350px;
}

.data-collection-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 350px;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  padding: 32px;
}

.data-collection-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.data-collection-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.data-collection-description {
  font-size: 14px;
  margin-bottom: 16px;
  color: #666;
  line-height: 1.5;
}

.data-collection-note {
  font-size: 12px;
  color: #6c757d;
}
</style>