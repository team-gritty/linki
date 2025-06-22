<template>
    <apexchart
      type="line"
      height="350"
      :options="chartOptions"
      :series="series"
    />
  </template>
  
  <script setup>
  import { computed, onMounted, ref } from 'vue'
  import VueApexCharts from 'vue3-apexcharts'
  import channelApi from '@/api/advertiser/advertiser-channel'
  
  // props : 부모 컴포넌트로부터 데이터를 전달받기 위한 뷰 기능.
  // defineProps는 Vue 3의 Composition API에서 props를 정의하는 함수
  // 부모 컴포넌트로부터 channelId를 전달받아 구독자 수 변화 그래프를 그리는데 사용
  const props = defineProps({
    channelId: {
      type: [Number, String],
      required: true
    }
  })
  
  // 구독자 수 변화 그래프를 그리기 위한 데이터를 저장할 배열
  const history = ref([])
  
  // 컴포넌트가 마운트되면, 구독자 수 변화 그래프를 그리기 위한 데이터를 조회하는 함수
  onMounted(async () => {
    try {
      console.log('=== SubscriberHistoryChart 디버그 ===')
      console.log('Fetching subscriber history for channelId:', props.channelId)
      
      // channelId 존재 여부 확인
      if (!props.channelId) {
        console.error('channelId가 존재하지 않습니다')
        history.value = []
        return
      }
      
      const data = await channelApi.getSubscriberHistory(props.channelId)
      console.log('구독자 히스토리 응답 데이터 :', data)
      
      if (Array.isArray(data)) {
        const filteredData = data
          .filter(item => String(item.channelId) === String(props.channelId))
          .sort((a, b) => new Date(a.collectedAt) - new Date(b.collectedAt))
        
        history.value = filteredData
        console.log('필터된 구독자 히스토리:', history.value)
      } else {
        console.error('배열이 아님 -----', typeof data, data)
        history.value = []
      }
      
    } catch (error) {
      console.error('구독자 히스토리 데이터 가져오는 중 에러 ', error)
      history.value = []
    }
  })
  
  const series = computed(() => {
    console.log('Computing series with history:', history.value)
    return [{
      name: '구독자 수',
      data: history.value.map(item => item.subscriberCount)
    }]
  })
  
  
  const chartOptions = computed(() => ({
    chart: {
      id: 'subscriber-history',
      toolbar: { show: false }
    },
    xaxis: {
      categories: history.value.map(item => {
        const date = new Date(item.collectedAt)
        return `${date.getMonth() + 1}/${date.getDate()}`
      }),
      labels: { rotate: -45 }
    },
    yaxis: {
      labels: {
        formatter: val => val.toLocaleString()
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