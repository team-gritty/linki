<script setup>
import { ref, computed, onMounted, nextTick, watch, onUnmounted } from 'vue'
import VueECharts from 'vue-echarts'
import * as echarts from 'echarts'
import { getDashboard } from '@/js/Dashboard'
console.log('accessToken:', localStorage.getItem('accessToken'))
const allData = ref([])

const viewType = ref('month') // 'month' 또는 'week'

// ISO week(월요일~일요일) 기준 주차 구하기
function getWeekKey(dateStr) {
  const date = new Date(dateStr);
  const temp = new Date(date.getTime());
  temp.setHours(0, 0, 0, 0);
  temp.setDate(temp.getDate() + 3 - ((temp.getDay() + 6) % 7));
  const week1 = new Date(temp.getFullYear(), 0, 4);
  return (
    temp.getFullYear() +
    '-W' +
    String(
      1 +
        Math.round(
          ((temp.getTime() - week1.getTime()) / 86400000 - 3 + ((week1.getDay() + 6) % 7)) / 7
        )
    ).padStart(2, '0')
  );
}

// '2024-01-08' → '2024년 1월 2주차'로 변환 (월별 1주차부터 시작)
function getMonthWeekLabel(dateStr) {
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const firstDay = new Date(year, month - 1, 1);
  const firstDayOfWeek = firstDay.getDay() === 0 ? 7 : firstDay.getDay(); // 일요일=7
  const day = date.getDate();
  const week = Math.ceil((day + firstDayOfWeek - 1) / 7);
  return `${year}년 ${month}월 ${week}주차`;
}

const labels = computed(() => {
  const months = {}
  allData.value.forEach(item => {
    const month = item.date
    if (!months[month]) months[month] = { newMembers: 0, newAdvertisers: 0, newInfluencers: 0 }
    months[month].newMembers += item.newMembers
    months[month].newAdvertisers += item.newAdvertisers
    months[month].newInfluencers += item.newInfluencers
  })
  return Object.keys(months)
})

const chartData = computed(() => {
  const months = {}
  allData.value.forEach(item => {
    const month = item.date
    if (!months[month]) months[month] = { newMembers: 0, newAdvertisers: 0, newInfluencers: 0 }
    months[month].newMembers += item.newMembers
    months[month].newAdvertisers += item.newAdvertisers
    months[month].newInfluencers += item.newInfluencers
  })
  const arr = Object.values(months)
  return {
    newMembers: arr.map(i => i.newMembers),
    newAdvertisers: arr.map(i => i.newAdvertisers),
    newInfluencers: arr.map(i => i.newInfluencers)
  }
})

const lineChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: '40px',
    containLabel: true
  },
  xAxis: { 
    type: 'category', 
    data: labels.value,
    axisLabel: {
      formatter: (value) => {
        const [year, month] = value.split('-');
        return `${year}년 ${parseInt(month)}월`;
      },
      interval: 0,
      rotate: 30
    }
  },
  yAxis: { type: 'value' },
  series: [
    { name: '신규 회원', type: 'line', data: chartData.value.newMembers, smooth: true, lineStyle: { color: '#7b5fff', width: 3 }, itemStyle: { color: '#7b5fff' } },
    { name: '신규 광고주', type: 'line', data: chartData.value.newAdvertisers, smooth: true, lineStyle: { color: '#ffd600', width: 3 }, itemStyle: { color: '#ffd600' } },
    { name: '신규 인플루언서', type: 'line', data: chartData.value.newInfluencers, smooth: true, lineStyle: { color: '#43a047', width: 3 }, itemStyle: { color: '#43a047' } }
  ]
}))

// 도넛 차트 옵션 (예시)
const LLM = ref({ msg: '' })
const contractStatus = ref({ pending: 0, active: 0, completed: 0 })

const doughnutChartOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', left: 'left', bottom: 10 },
  series: [
    {
      name: '계약 상태',
      type: 'pie',
      radius: ['60%', '85%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        position: 'inside',
        formatter: '{c}',
        fontSize: 16,
        color: '#ffffff',
        fontWeight: 'bold'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold',
          formatter: '{b}: {c}'
        }
      },
      labelLine: { show: false },
      data: [
        { value: contractStatus.value.pending, name: '광고 진행 예정', itemStyle: { color: '#7b5fff' } },
        { value: contractStatus.value.active, name: '광고 진행중', itemStyle: { color: '#ffd600' } },
        { value: contractStatus.value.completed, name: '정산완료', itemStyle: { color: '#43a047' } }
      ]
    }
  ]
}))

// 대시보드 요약 데이터
const dashboardSummary = ref({
  totalAdvertisers: 0,
  totalInfluencers: 0,
  activeCampaigns: 0,
  ongoingContracts: 0,
  currentSubscribers: 0,
  MonthlyRevenue: 0
})

const chartContainer = ref(null)
const showLeftButton = ref(false)
const showRightButton = ref(true)

// 스크롤 버튼 표시 여부 체크
const checkScrollButtons = () => {
  if (!chartContainer.value) return;
  
  const { scrollLeft, scrollWidth, clientWidth } = chartContainer.value;
  showLeftButton.value = scrollLeft > 0;
  showRightButton.value = scrollLeft < (scrollWidth - clientWidth - 1); // 1px 오차 허용
}

// 스크롤 함수 수정
const scrollChart = (direction) => {
  if (!chartContainer.value) return;
  
  const scrollAmount = 300;
  const currentScroll = chartContainer.value.scrollLeft;
  
  if (direction === 'left') {
    chartContainer.value.scrollTo({
      left: currentScroll - scrollAmount,
      behavior: 'smooth'
    });
  } else {
    chartContainer.value.scrollTo({
      left: currentScroll + scrollAmount,
      behavior: 'smooth'
    });
  }
}

onMounted(async () => {
  try {
    const res = await getDashboard();
    console.log('대시보드 응답', res.data);
    const data = Array.isArray(res.data) ? res.data[0] : res.data;
    if (data && data.dashboardSummary) {
      dashboardSummary.value = {
        ...data.dashboardSummary,
        MonthlyRevenue: data.dashboardSummary.monthlyRevenue
      };
    }
    if (data && data.llm) {
      LLM.value = data.llm;
    }
    if (data && data.contractStatus) {
      contractStatus.value = data.contractStatus;
    }
    // trendChart가 null이 아닐 때만 처리
    if (data && data.trendChart && data.trendChart !== null) {
      console.log('trendChart 원본 데이터:', data.trendChart);
      
      // 모든 년도를 동적으로 추출
      const allYears = new Set();
      
      // newMembers에서 년도 추출
      if (data.trendChart.newMembers) {
        Object.keys(data.trendChart.newMembers).forEach(year => allYears.add(year));
      }
      
      // newAdvertisers에서 년도 추출
      if (data.trendChart.newAdvertisers) {
        Object.keys(data.trendChart.newAdvertisers).forEach(year => allYears.add(year));
      }
      
      // newInfluencers에서 년도 추출
      if (data.trendChart.newInfluencers) {
        Object.keys(data.trendChart.newInfluencers).forEach(year => allYears.add(year));
      }
      
      const years = Array.from(allYears).sort();
      console.log('추출된 년도들:', years);
      
      const tempData = {};
      
      years.forEach(year => {
        const membersData = data.trendChart.newMembers?.[year] || {};
        const advertisersData = data.trendChart.newAdvertisers?.[year] || {};
        const influencersData = data.trendChart.newInfluencers?.[year] || {};

        // 각 카테고리별로 월 데이터 처리
        Object.keys(membersData).forEach(month => {
          const key = `${year}-${month.padStart(2, '0')}`;
          if (!tempData[key]) tempData[key] = { newMembers: 0, newAdvertisers: 0, newInfluencers: 0 };
          tempData[key].newMembers = membersData[month];
        });

        Object.keys(advertisersData).forEach(month => {
          const key = `${year}-${month.padStart(2, '0')}`;
          if (!tempData[key]) tempData[key] = { newMembers: 0, newAdvertisers: 0, newInfluencers: 0 };
          tempData[key].newAdvertisers = advertisersData[month];
        });

        Object.keys(influencersData).forEach(month => {
          const key = `${year}-${month.padStart(2, '0')}`;
          if (!tempData[key]) tempData[key] = { newMembers: 0, newAdvertisers: 0, newInfluencers: 0 };
          tempData[key].newInfluencers = influencersData[month];
        });
      });

      // 날짜순으로 정렬
      const sortedDates = Object.keys(tempData).sort((a, b) => {
        const [yearA, monthA] = a.split('-').map(Number);
        const [yearB, monthB] = b.split('-').map(Number);
        return yearA === yearB ? monthA - monthB : yearA - yearB;
      });

      allData.value = sortedDates.map(date => ({
        date,
        newMembers: tempData[date].newMembers,
        newAdvertisers: tempData[date].newAdvertisers,
        newInfluencers: tempData[date].newInfluencers
      }));

      console.log('처리된 데이터:', allData.value);
    } else {
      allData.value = [];
    }

    // 차트 데이터가 로드된 후 스크롤 위치 설정
    nextTick(() => {
      setTimeout(() => {
        if (chartContainer.value) {
          chartContainer.value.scrollLeft = chartContainer.value.scrollWidth;
          checkScrollButtons();
        }
      }, 100);
    });
  } catch (e) {
    console.error('대시보드 데이터 불러오기 실패', e);
  }
})

// 스크롤 이벤트 리스너 추가
onMounted(() => {
  if (chartContainer.value) {
    chartContainer.value.addEventListener('scroll', checkScrollButtons);
  }
})

onUnmounted(() => {
  if (chartContainer.value) {
    chartContainer.value.removeEventListener('scroll', checkScrollButtons);
  }
})

// 차트가 업데이트될 때마다 스크롤 위치 재설정
watch(() => allData.value, () => {
  nextTick(() => {
    setTimeout(() => {
      if (chartContainer.value) {
        chartContainer.value.scrollLeft = chartContainer.value.scrollWidth;
      }
    }, 100);
  });
});
</script>

<template>
  <div class="dashboard">
    <div class="trend-box">
      <div class="trend-title">오늘의 광고 트렌드</div>
      <div class="trend-desc">{{ LLM.msg }}</div>
    </div>
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.totalAdvertisers }}</div>
        <div class="stat-label">전체 광고주 수</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.totalInfluencers }}</div>
        <div class="stat-label">전체 인플루언서 수</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.activeCampaigns }}</div>
        <div class="stat-label">등록된 캠페인 수</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.ongoingContracts }}</div>
        <div class="stat-label">진행중인 계약수</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.currentSubscribers }}</div>
        <div class="stat-label">전체 구독 수</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ dashboardSummary.MonthlyRevenue?.toLocaleString() }}원</div>
        <div class="stat-label">이번달 매출</div>
      </div>
    </div>
    <div class="charts-row">
      <div class="chart-box">
        <div class="chart-title-row">
          <div class="chart-title">월간 신규 유입 현황</div>
        </div>
        <div class="chart-legend">
          <span class="legend-item member">
            <span class="legend-dot"></span>
            신규 회원
          </span>
          <span class="legend-item advertiser">
            <span class="legend-dot"></span>
            신규 광고주
          </span>
          <span class="legend-item influencer">
            <span class="legend-dot"></span>
            신규 인플루언서
          </span>
        </div>
        <div class="chart-scroll-container">
          <button class="scroll-button left" @click="scrollChart('left')" :disabled="!showLeftButton">&lt;</button>
          <div class="chart-container" ref="chartContainer">
            <v-chart
              :option="lineChartOption"
              autoresize
            />
          </div>
          <button class="scroll-button right" @click="scrollChart('right')" :disabled="!showRightButton">&gt;</button>
        </div>
      </div>
      <div class="chart-box">
        <div class="chart-title">이번달 계약 상태 분포</div>
        <v-chart
          :option="doughnutChartOption"
          autoresize
          style="width: 100%; height: 320px"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard { 
  padding: 32px; 
  background: #f8f8fa;
  min-height: 100vh;
}
.trend-box {
  background: #7b5fff;
  color: #fff;
  border-radius: 16px;
  padding: 36px 0 28px 0;
  text-align: center;
  margin-bottom: 40px;
  box-shadow: 0 6px 24px rgba(123,95,255,0.18);
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.trend-title { font-size: 2rem; font-weight: 800; letter-spacing: -1px; }
.trend-desc { margin-top: 14px; font-size: 1.18rem; }

.stats-row {
  display: flex; 
  gap: 32px; 
  margin-bottom: 48px; 
  justify-content: center;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.stat-card {
  background: #222;
  color: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  min-width: 150px;
  box-shadow: 0 2px 12px rgba(34,34,34,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stat-card.sales {
  background: #222;
}
.stat-value {
  font-size: clamp(1rem, 1vw, 2.5rem);
  font-weight: bold;
  white-space: nowrap;
  width: 100%;
  text-align: center;
  line-height: 1.1;
}
.stat-label {
  margin-top: 12px; font-size: 1.08rem;
  white-space: nowrap;
}

.charts-row {
  display: flex; 
  gap: 40px; 
  justify-content: center;
  margin-top: 10px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.chart-box { 
  background: #fff; 
  border-radius: 16px; 
  padding: 32px 24px 24px 24px; 
  flex: 1;
  min-width: 320px;
  max-width: 600px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-x: auto;
}

.chart-box v-chart {
  min-width: 800px; /* 최소 너비 설정 */
}

.chart-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
  text-align: center;
}

.chart-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  min-height: 32px;
}
.chart-title {
  text-align: left;
}
.chart-select {
  padding: 6px 18px;
  border-radius: 16px;
  border: 1px solid #7b5fff;
  color: #7b5fff;
  font-weight: bold;
  background: #f0f0ff;
  outline: none;
  cursor: pointer;
  transition: border 0.2s;
  margin-left: 16px;
}
.chart-select:focus {
  border: 2px solid #6c3fff;
}

@media (max-width: 1200px) {
  .trend-box, .stats-row, .charts-row { max-width: 100%; }
}
@media (max-width: 1100px) {
  .stats-row { flex-wrap: wrap; gap: 20px; }
  .stat-card { min-width: 120px; padding: 24px 16px; }
  .charts-row { flex-direction: column; gap: 32px; align-items: center; }
  .chart-box { width: 100%; min-width: 0; max-width: 100%; }
}
@media (max-width: 600px) {
  .dashboard { padding: 8px; }
  .trend-box { padding: 20px 0 16px 0; border-radius: 10px; }
  .stats-row { flex-direction: column; gap: 12px; }
  .stat-card { min-width: 0; width: 100%; border-radius: 10px; }
  .charts-row { gap: 16px; }
  .chart-box { padding: 12px; border-radius: 10px; }
}

.chart-scroll-container {
  position: relative;
  width: 100%;
}

.chart-container {
  width: 100%;
  overflow-x: auto;
  padding: 0 20px;
}

.chart-container :deep(.echarts) {
  min-width: 800px;
  height: 320px !important;
}

.scroll-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #7b5fff;
  color: white;
  border: none;
  cursor: pointer;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.2s;
  opacity: 1;
}

.scroll-button:disabled {
  opacity: 0;
  cursor: default;
  pointer-events: none;
}

.scroll-button:not(:disabled):hover {
  background: #6c3fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.scroll-button.left {
  left: -16px;
}

.scroll-button.right {
  right: -16px;
}

.chart-legend {
  margin-bottom: 16px;
  text-align: center;
}

.legend-item {
  margin: 0 8px;
  display: inline-block;
}

.legend-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 4px;
}

.legend-item.member .legend-dot {
  background-color: #7b5fff;
}

.legend-item.advertiser .legend-dot {
  background-color: #ffd600;
}

.legend-item.influencer .legend-dot {
  background-color: #43a047;
}
</style>
