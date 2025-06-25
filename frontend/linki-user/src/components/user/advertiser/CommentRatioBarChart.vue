<template>
  <div>
    <div v-if="!chartData.hasValidData" class="no-data-message">
      차트 데이터를 로딩 중이거나 데이터가 없습니다.
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
import { computed, watchEffect } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

const props = defineProps({
  channels: { type: Array, required: true },
  channelId: { type: [Number, String], required: true }
})

watchEffect(() => {
  console.log('CommentRatioBarChart - channelId:', props.channelId)
  console.log('CommentRatioBarChart - channels:', props.channels)
})

// 내 채널 - 평균 댓글 비율 계산
const myChannelCommentRatio = computed(() => {
  console.log('=== CommentRatioBarChart 디버깅 ===')
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
  
  // 댓글 및 조회수 필드 확인
  const commentFields = ['avgCommentCount', 'avgComments', 'commentCount', 'comments', 'avg_comment_count']
  const viewFields = ['avgViewCount', 'avgViews', 'viewCount', 'views', 'avg_view_count']
  
  let commentCount = 0
  let viewCount = 0
  
  // 댓글 수 찾기
  for (const field of commentFields) {
    if (myChannel[field] !== undefined && myChannel[field] !== null) {
      commentCount = Number(myChannel[field]) || 0
      console.log(`댓글 수 찾음 (${field}):`, commentCount)
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
  
  console.log('최종 댓글 수:', commentCount)
  console.log('최종 조회수:', viewCount)
  
  const ratio = viewCount > 0 ? commentCount / viewCount : 0
  console.log('계산된 댓글 비율:', ratio)
  console.log('=== CommentRatioBarChart 디버깅 끝 ===')
  
  return ratio
})

// 전체 채널 - 평균 댓글 비율 계산
const overallCommentRatio = computed(() => {
  console.log('=== 전체 채널 댓글 비율 계산 ===')
  // props.channels가 undefined이거나 null 일때 []빈배열 사용 - 데이터가 로딩중일때 에러 방지 
  const allChannels = props.channels || []
  console.log('전체 채널 수:', allChannels.length)
  
  if (allChannels.length === 0) {
    console.log('전체 채널 데이터 없음')
    return 0
  }

  // 댓글 및 조회수 필드 확인
  const commentFields = ['avgCommentCount', 'avgComments', 'commentCount', 'comments', 'avg_comment_count']
  const viewFields = ['avgViewCount', 'avgViews', 'viewCount', 'views', 'avg_view_count']
  
  let totalComments = 0
  let totalViews = 0
  let validChannelCount = 0
  
  allChannels.forEach((channel, index) => {
    let channelComments = 0
    let channelViews = 0
    
    // 댓글 수 찾기
    for (const field of commentFields) {
      if (channel[field] !== undefined && channel[field] !== null) {
        channelComments = Number(channel[field]) || 0
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
      totalComments += channelComments
      totalViews += channelViews
      validChannelCount++
    }
  })
  
  console.log('유효한 채널 수:', validChannelCount)
  console.log('총 댓글 수:', totalComments)
  console.log('총 조회수:', totalViews)
  
  const ratio = totalViews > 0 ? totalComments / totalViews : 0
  console.log('전체 평균 댓글 비율:', ratio)
  console.log('=== 전체 채널 댓글 비율 계산 끝 ===')
  
  return ratio
})

// 차트를 위한 데이터 계산
const series = computed(() => [
  {
    name: '댓글 비율',
    data: [myChannelCommentRatio.value, overallCommentRatio.value]
  }
])

// 차트 옵션 계산
const chartOptions = computed(() => ({ // computed: 내부 값 바뀌면 자동으로 옵션 갱신됨. 
    // 차트에 대한 전반적 설정 
  chart: { id: 'comment-ratio-bar', toolbar: { show: false } },
  //  x축  설정
  xaxis: { categories: ['내 채널', '전체'] },
  // y축 설정
  yaxis: {
    min: 0, // y축 최소값 0 설정 - 막대가 아래로 내려가지 않게 
    // y축 - 소수점 2자리까지 표시
    labels: { formatter: val => val.toFixed(2) }
  },
  // 첫번째 막대 색상, 두번째 막대 생상 
  colors: ['#6B46C1', '#9F7AEA'],
  // 막대 위에 값 표시 여부 
  dataLabels: { enabled: false },
  grid: { borderColor: '#eee' },
  // 마우스를 올렸을때 나오는 툴팁 설정 - 퍼센트로, 소수점 둘째자리까지 표시 
  tooltip: {
    y: { formatter: val => (val * 100).toFixed(2) + '%' }
  },
  // 막대 그래프의 스타일 설정 
  plotOptions: {
    bar: {
      borderRadius: 5,
      // 막대 너비 설정
      columnWidth: '40%',
      // 각 막대가 다른 색깔 가지는지 여부 
      distributed: true
    }
  }
}))

const chartKey = computed(() => `${props.channelId}-${myChannelCommentRatio.value}-${overallCommentRatio.value}`)

// 차트 데이터 유효성 검증
const chartData = computed(() => {
  const hasChannels = props.channels && props.channels.length > 0
  const hasChannelId = props.channelId
  const hasValidData = hasChannels && hasChannelId
  
  console.log('CommentRatioBarChart 유효성 검증:', {
    hasChannels,
    hasChannelId,
    hasValidData,
    myRatio: myChannelCommentRatio.value,
    overallRatio: overallCommentRatio.value
  })
  
  return {
    hasValidData,
    hasChannels,
    hasChannelId
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
</style> 