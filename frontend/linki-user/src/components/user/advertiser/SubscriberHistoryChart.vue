<template>
  <div class="subscriber-chart-container">
    <!-- 30일, 15일 데이터가 부족시 메시지 표시 -->
    <div v-if="showDataCollectionMessage" class="data-collection-message">
      <div class="data-collection-icon">📈</div>
      <div class="data-collection-title">데이터 수집 중</div>
      <div class="data-collection-description">
        7일간 구독자 변화 데이터를 수집하고 있습니다.<br>
        충분한 데이터가 모이면 그래프를 표시해드리겠습니다.
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

// API에서 온 실제 히스토리 데이터를 모두 사용 (7일 고정)
const filteredHistory = computed(() => {
  if (!history.value || history.value.length === 0) {
    return []
  }
  
  console.log('=== filteredHistory 계산 ===')
  console.log('원본 history 데이터:', history.value)
  
  // API에서 온 데이터를 날짜순으로 정렬하여 모두 사용
  const sortedData = history.value
    .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
  
  console.log('정렬된 데이터:', sortedData)
  console.log('filteredHistory 결과 길이:', sortedData.length)
  
  return sortedData
})

// 데이터 수집 중 메시지 표시 여부 (7일 고정)
const showDataCollectionMessage = computed(() => {
  // 7일 데이터가 없는 경우에만 메시지 표시
  return history.value.length === 0
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
      console.log('데이터 개수:', filteredData.length)
    } else {
      // 실제 데이터가 없는 경우
      console.log('구독자 히스토리 데이터가 없음')
      history.value = []
      hasRealData.value = false
    }
    
  } catch (error) {
    console.error('구독자 히스토리 데이터 가져오는 중 에러 ', error)
    history.value = []
    hasRealData.value = false
  }
})



// period가 변경될 때 처리 (현재는 7일 고정이므로 실행되지 않음)
watch(() => props.period, () => {
  console.log('Period 변경됨:', props.period)
  // 7일 고정이므로 특별한 처리 불필요
})

const series = computed(() => {
  console.log('Computing series with filteredHistory:', filteredHistory.value)
  const subscriberData = filteredHistory.value.map(item => item.subscriberCount)
  console.log('실제 구독자 수 데이터:', subscriberData)
  console.log('최소값:', Math.min(...subscriberData))
  console.log('최대값:', Math.max(...subscriberData))
  
  return [{
    name: '구독자 수',
    data: subscriberData
  }]
})

const chartOptions = computed(() => {
  // 실제 구독자 수 데이터 추출
  const subscriberData = filteredHistory.value.map(item => item.subscriberCount)
  const minValue = subscriberData.length > 0 ? Math.min(...subscriberData) : 0
  const maxValue = subscriberData.length > 0 ? Math.max(...subscriberData) : 100
  
  const range = maxValue - minValue
  console.log('Chart Y축 설정:', { 
    minValue, 
    maxValue, 
    range,
    rangePercentage: minValue > 0 ? ((range / minValue) * 100).toFixed(3) + '%' : '0%',
    subscriberData 
  })
  
  return {
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
        
        // 7일 고정: 상대적 날짜 표시
        if (diffDays === 0) {
          return '오늘'
        } else if (diffDays === 1) {
          return '어제'
        } else if (diffDays <= 6) {
          return `${diffDays}일 전`
        }
        
        // 7일을 넘으면 월/일 형식
        return `${date.getMonth() + 1}/${date.getDate()}`
      }),
      labels: { 
        rotate: 0, // 7일 고정이므로 회전 없이 표시
        style: {
          fontSize: '12px'
        }
      },
      title: {
        text: '',
        style: {
          fontSize: '14px',
          fontWeight: 600
        }
      }
    },
    yaxis: {
      labels: {
        formatter: val => Math.round(val).toLocaleString()
      },
      title: {
        text: '', // Y축 라벨 제거로 깔끔하게 처리
      },
      // 구독자 수 변화를 더 잘 보이도록 Y축 범위 조정
      min: (() => {
        if (subscriberData.length === 0) return 0;
        const range = maxValue - minValue;
        // 변화폭이 작으면 더 좁은 범위로 설정
        if (range < minValue * 0.01) { // 1% 미만 변화인 경우
          return Math.floor(minValue - range * 2); // 변화량의 2배 여유
        }
        return Math.max(0, Math.floor(minValue * 0.98));
      })(),
      max: (() => {
        if (subscriberData.length === 0) return 100;
        const range = maxValue - minValue;
        // 변화폭이 작으면 더 좁은 범위로 설정
        if (range < minValue * 0.01) { // 1% 미만 변화인 경우
          return Math.ceil(maxValue + range * 2); // 변화량의 2배 여유
        }
        return Math.ceil(maxValue * 1.02);
      })(),
      forceNiceScale: false,
      decimalsInFloat: 0,
      tickAmount: 8 // Y축 눈금 개수 증가
    },
    stroke: {
      curve: 'smooth',
      width: 4 // 선 두께 증가
    },
    colors: ['#ff4d67'],
    dataLabels: { 
      enabled: false // 데이터 라벨 비활성화로 Y축 레이블과 겹치는 문제 해결
    },
    tooltip: {
      y: {
        formatter: (val, { series, seriesIndex, dataPointIndex, w }) => {
          let result = `${val.toLocaleString()}명`
          
          // 이전 날짜와 비교해서 변화량 표시
          if (dataPointIndex > 0) {
            const prevVal = subscriberData[dataPointIndex - 1]
            const change = val - prevVal
            const changePercent = ((change / prevVal) * 100).toFixed(2)
            const changeSign = change >= 0 ? '+' : ''
            const changeColor = change >= 0 ? '#22c55e' : '#ef4444'
            
            result += `<br/><span style="color: ${changeColor}; font-weight: bold;">
              전일 대비: ${changeSign}${change.toLocaleString()}명 (${changeSign}${changePercent}%)
            </span>`
          }
          
          return result
        }
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
        top: 10,
        right: 20,
        bottom: 10,
        left: 80 // 왼쪽 패딩 증가로 Y축 레이블 공간 확보
      }
    },
    markers: {
      size: 6, // 마커 크기 증가
      colors: ['#ff4d67'],
      strokeColors: '#fff',
      strokeWidth: 3, // 테두리 두께 증가
      hover: {
        size: 8 // 호버 시 크기 증가
      },
      discrete: [] // 모든 포인트에 마커 표시
    }
  }
})
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