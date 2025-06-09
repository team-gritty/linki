<template>
  <div>
    <apexchart type="bar" height="320" :options="chartOptions" :series="series" :key="chartKey" />
  </div>
</template>

<script setup>
// 필요한 vue 함수 import
import { ref, watch, computed, watchEffect } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

// 차트 키 계산
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

// 디버깅: channelId와 channels 배열을 watchEffect로 출력
watchEffect(() => {
  console.log('LikeRatioBarChart - channelId:', props.channelId)
  console.log('LikeRatioBarChart - channels:', props.channels)
})

// 내 채널 비율 계산 및 디버깅 로그 추가
const myChannelLikeRatio = computed(() => {
  const my = (props.channels || []).find(c => String(c.id) === String(props.channelId))
  if (my) {
    const ratio = my.avgViewCount > 0 ? my.avgLikeCount / my.avgViewCount : 0
    console.log('내 채널:', my)
    console.log('내 채널 비율:', ratio)
    return ratio
  }
  return 0
})

// 전체 평균 좋아요/조회수 비율 계산
const overallLikeRatio = computed(() => {
  const validChannels = (props.channels || []).filter(c => c.avgViewCount > 0)
  if (validChannels.length === 0) return 0
  const totalLike = validChannels.reduce((sum, c) => sum + c.avgLikeCount, 0)
  const totalView = validChannels.reduce((sum, c) => sum + c.avgViewCount, 0)
  return totalView > 0 ? totalLike / totalView : 0
})

// series를 computed로 변경하여 항상 최신 비율을 반영
const series = computed(() => [
  {
    name: '좋아요 비율',
    data: [myChannelLikeRatio.value, overallLikeRatio.value]
  }
])

const chartOptions = computed(() => ({
  chart: { id: 'like-ratio-bar', toolbar: { show: false } },
  xaxis: { categories: ['내 채널', '전체'] },
  yaxis: {
    min: 0,
    labels: { formatter: val => val.toFixed(2) }
  },
  colors: ['#6B46C1', '#9F7AEA'],
  dataLabels: { enabled: false },
  grid: { borderColor: '#eee' },
  tooltip: {
    y: { formatter: val => (val * 100).toFixed(2) + '%' }
  },
  plotOptions: {
    bar: {
      borderRadius: 5,
      columnWidth: '40%',
      distributed: true
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