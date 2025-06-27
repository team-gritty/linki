<template>
  <div>
    <div v-if="!chartData.hasValidData" class="no-data-message">
      <div class="no-data-icon">📊</div>
      <div class="no-data-title">좋아요 데이터 부족</div>
      <div class="no-data-description">
        현재 채널과 상위 50개 채널 모두 <br>
        평균 좋아요 수 데이터가 부족하여 비율 분석이 불가능합니다.
      </div>
      <div class="no-data-note">
        * 충분한 영상 데이터 수집 후 분석 결과를 확인하실 수 있습니다.
      </div>
    </div>
    <apexchart 
      v-else
      type="bar" 
      height="320" 
      :options="chartOptions" 
      :series="series" 
      :key="chartKey" 
    />
  </div>
</template>

<script setup>
// 필요한 vue 함수 import
import { ref, watch, computed, watchEffect } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

// reactive한 차트 key를 computed로 정의
const chartKey = computed(() => `${props.channelId}-${myChannelLikeRatio.value}-${overallLikeRatio.value}`)

// 부모 컴포넌트에서 channels와 channelId를 props로 받음
const props = defineProps({
  channels: {
    type: Array,
    required: true
  },
  channelId: {
    type: [Number, String], // 숫자 또는 문자열 모두 허용
    required: true
  }
})

// props가 바뀔 때마다 콘솔에 확인 로그 찍기 (디버깅용)
watchEffect(() => {
  console.log('LikeRatioBarChart - channelId:', props.channelId)
  console.log('LikeRatioBarChart - channels:', props.channels)
})

// 내 채널의 좋아요/조회수 비율을 계산하는 computed
const myChannelLikeRatio = computed(() => {
  console.log('=== LikeRatioBarChart 디버깅 ===')
  console.log('찾아야 하는 채널 ID:', props.channelId)
  console.log('채널 목록 길이:', props.channels?.length || 0)
  
  // 기본값 체크
  if (!props.channelId || !props.channels || props.channels.length === 0) {
    console.warn('channelId 또는 channels 데이터가 없음')
    return 0
  }
  
  // 첫 번째 채널의 구조 출력
  if (props.channels.length > 0) {
    console.log('첫 번째 채널 객체 구조:', props.channels[0])
    console.log('사용 가능한 필드들:', Object.keys(props.channels[0]))
  }
  
  // 다양한 방식으로 채널 검색 시도
  let myChannel = null
  
  // 1. channelId 필드로 검색
  myChannel = props.channels.find(c => String(c.channelId) === String(props.channelId))
  if (myChannel) {
    console.log('channelId 필드로 채널 찾음:', myChannel)
  }
  
  // 2. id 필드로 검색
  if (!myChannel) {
    myChannel = props.channels.find(c => String(c.id) === String(props.channelId))
    if (myChannel) {
      console.log('id 필드로 채널 찾음:', myChannel)
    }
  }
  
  // 3. 다른 가능한 ID 필드들로 검색
  if (!myChannel) {
    const possibleIdFields = ['channel_id', 'Channel_id', 'CHANNEL_ID']
    for (const field of possibleIdFields) {
      myChannel = props.channels.find(c => c[field] && String(c[field]) === String(props.channelId))
      if (myChannel) {
        console.log(`${field} 필드로 채널 찾음:`, myChannel)
        break
      }
    }
  }
  
  if (!myChannel) {
    console.warn('채널을 찾지 못했습니다.')
    console.log('전체 채널 ID 목록:', props.channels.map(c => ({
      channelId: c.channelId,
      id: c.id,
      name: c.channelName || c.name
    })))
    return 0
  }
  
  // 좋아요 및 조회수 필드 확인
  const likeFields = ['avgLikeCount', 'avgLikes', 'likeCount', 'likes', 'avg_like_count']
  const viewFields = ['avgViewCount', 'avgViews', 'viewCount', 'views', 'avg_view_count']
  
  let likeCount = 0
  let viewCount = 0
  
  // 좋아요 수 찾기
  for (const field of likeFields) {
    if (myChannel[field] !== undefined && myChannel[field] !== null) {
      likeCount = Number(myChannel[field]) || 0
      console.log(`좋아요 수 찾음 (${field}):`, likeCount)
      break
    }
  }
  
  // 조회수 찾기
  for (const field of viewFields) {
    if (myChannel[field] !== undefined && myChannel[field] !== null) {
      viewCount = Number(myChannel[field]) || 0
      console.log(`조회수 찾음 (${field}):`, viewCount)
      break
    }
  }
  
  console.log('최종 좋아요 수:', likeCount)
  console.log('최종 조회수:', viewCount)
  
  const ratio = viewCount > 0 ? likeCount / viewCount : 0
  console.log('계산된 좋아요 비율:', ratio)
  console.log('=== LikeRatioBarChart 디버깅 끝 ===')
  
  return ratio
})

// 전체 채널의 평균 좋아요/조회수 비율 계산
const overallLikeRatio = computed(() => {
  console.log('=== 상위 50개 채널 좋아요 비율 계산 ===')
  const allChannels = props.channels || []
  console.log('상위 50개 채널 수:', allChannels.length)
  
  if (allChannels.length === 0) {
    console.log('상위 50개 채널 데이터 없음')
    return 0
  }

  // 좋아요 및 조회수 필드 확인
  const likeFields = ['avgLikeCount', 'avgLikes', 'likeCount', 'likes', 'avg_like_count']
  const viewFields = ['avgViewCount', 'avgViews', 'viewCount', 'views', 'avg_view_count']
  
  let totalLikes = 0
  let totalViews = 0
  let validChannelCount = 0
  
  allChannels.forEach((channel, index) => {
    let channelLikes = 0
    let channelViews = 0
    
    // 좋아요 수 찾기
    for (const field of likeFields) {
      if (channel[field] !== undefined && channel[field] !== null) {
        channelLikes = Number(channel[field]) || 0
        break
      }
    }
    
    // 조회수 찾기
    for (const field of viewFields) {
      if (channel[field] !== undefined && channel[field] !== null) {
        channelViews = Number(channel[field]) || 0
        break
      }
    }
    
    if (channelViews > 0) {
      totalLikes += channelLikes
      totalViews += channelViews
      validChannelCount++
    }
  })
  
  console.log('유효한 채널 수:', validChannelCount)
  console.log('총 좋아요 수:', totalLikes)
  console.log('총 조회수:', totalViews)
  
  const ratio = totalViews > 0 ? totalLikes / totalViews : 0
  console.log('전체 평균 좋아요 비율:', ratio)
  console.log('=== 상위 50개 채널 좋아요 비율 계산 끝 ===')
  
  return ratio
})

// 차트 series 데이터 (항상 최신 computed 값 반영)
const series = computed(() => [
  {
    name: '좋아요 비율', // 데이터 라벨 
    data: [myChannelLikeRatio.value, overallLikeRatio.value]  // 내 채널과 전체 평균 비율
  }
])

// ApexCharts의 옵션을 computed로 정의
const chartOptions = computed(() => ({
  chart: {
    type: 'bar',
    height: 320,
    toolbar: { show: false }
  },
  xaxis: {
    categories: ['내 채널', '상위 50개 채널 평균'],
    labels: {
      style: {
        fontSize: '12px',
        fontWeight: 500
      }
    }
  },
  yaxis: {
    min: 0, // y축 최소값 0 설정 - 막대가 아래로 내려가지 않게 
    max: Math.max(Math.max(myChannelLikeRatio.value, overallLikeRatio.value) * 1.5, 0.01), // y축 최대값을 동적으로 설정
    tickAmount: 5, // y축 간격을 5개로 유지
    // y축 - 소수점 3자리까지 표시 (더 정밀하게)
    labels: { formatter: val => val.toFixed(3) }
  },
  colors: ['#6B46C1', '#9F7AEA'],                          // 색상 배열
  dataLabels: { enabled: false },                          // 데이터 라벨 숨김
  grid: { borderColor: '#eee' },                           // 차트 격자선 색
  tooltip: {
    y: { formatter: val => (val * 100).toFixed(3) + '%' }  // 툴크 숫자 % 형식 (더 정밀하게)
  },
  plotOptions: {
    bar: {
      borderRadius: 5,                                      // 막대 둥근 정도
      columnWidth: '40%',                                   // 막대 너비
      distributed: true                                     // 각 막대에 다른 색
    }
  }
}))

// 차트 데이터 유효성 검증
const chartData = computed(() => {
  const hasChannels = props.channels && props.channels.length > 0
  const hasChannelId = props.channelId
  
  // 현재 채널의 좋아요 데이터 확인
  const hasMyChannelLikeData = myChannelLikeRatio.value > 0
  
  // 전체 채널의 좋아요 데이터 확인
  const hasOverallLikeData = overallLikeRatio.value > 0
  
  // 좋아요 데이터가 하나라도 있으면 차트 표시
  const hasValidData = hasChannels && hasChannelId && (hasMyChannelLikeData || hasOverallLikeData)
  
  console.log('LikeRatioBarChart 유효성 검증:', {
    hasChannels,
    hasChannelId,
    hasMyChannelLikeData,
    hasOverallLikeData,
    hasValidData,
    myRatio: myChannelLikeRatio.value,
    overallRatio: overallLikeRatio.value
  })
  
  return {
    hasValidData,
    hasChannels,
    hasChannelId
  }
})
</script>

<script>
// apexchart 컴포넌트를 전역 등록
export default {
  components: {
    apexchart: VueApexCharts
  }
}
</script>

<style scoped>
.no-data-message {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 320px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  color: #6c757d;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  margin: 0;
}

.no-data-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-data-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.no-data-description {
  font-size: 14px;
  margin-bottom: 16px;
}

.no-data-note {
  font-size: 12px;
  color: #6c757d;
}
</style> 