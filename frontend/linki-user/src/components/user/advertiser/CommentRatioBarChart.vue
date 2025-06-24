<template>
  <div>
    <apexchart
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
  // 내 채널 데이터 찾기. props.channels, props.channelId바뀌면 자동으로 다시 계산됨 - channelId 필드명으로 수정
  console.log('=== CommentRatioBarChart 디버깅 ===')
  console.log('찾아야 하는 채널 아이디 :', props.channelId)
  console.log('채널 목록은:', props.channels?.length || 0, '개 채널')
  
  // 기본값 체크
  if (!props.channelId || !props.channels || props.channels.length === 0) {
    console.warn('channelId 또는 channels 데이터가 없음')
    return 0
  }
  
  // 다양한 방식으로 채널 검색 시도
  let my = props.channels.find(c => String(c.channelId) === String(props.channelId))
  
  if (!my) {
    // channelId 대신 id 필드로 시도
    my = props.channels.find(c => String(c.id) === String(props.channelId))
  }
  
  if (!my) {
    // 다른 가능한 필드명들로 시도
    console.log('첫 번째 채널 객체 구조:', props.channels[0])
    console.warn('내채널 찾지 못함. 0 반환하기---')
    return 0
  }
  
  console.log('내채널 찾음:', my)
  
  if (my) { // 내채널을 찾았다면 
    //  조회수가 0보다 크면 댓글 수 /조회수 계산
    // 0보다 작으면 0 바로 반환 
    console.log('채널 평균 조회수:', my.avgViewCount)
    console.log('채널 평균 댓글 수 :', my.avgCommentCount)
    
    const ratio = my.avgViewCount > 0 ? my.avgCommentCount / my.avgViewCount : 0
    console.log('댓글 비율 계산 완료:', ratio)
    return ratio
  }
  
  return 0
})

// 전체 채널 - 평균 댓글 비율 계산
const overallCommentRatio = computed(() => {
    // props.channels가 undefined이거나 null 일때 []빈배열 사용 - 데이터가 로딩중일때 에러 방지 
  const validChannels = (props.channels || []).filter(c => c.avgViewCount > 0)
  if (validChannels.length === 0) return 0
  // 댓글 수 합계 계산
  const totalComment = validChannels.reduce((sum, c) => sum + c.avgCommentCount, 0)
  // 조회수 합계 계산
  const totalView = validChannels.reduce((sum, c) => sum + c.avgViewCount, 0)
  // 조회수가 0보다 크면 댓글 수 /조회수 계산
  // 0보다 작으면 0 바로 반환 
  return totalView > 0 ? totalComment / totalView : 0
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
</script>

<script>
export default {
  components: {
    apexchart: VueApexCharts
  }
}
</script> 