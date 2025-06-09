<script setup>
import { ref, computed, onMounted } from 'vue'
import VueECharts from 'vue-echarts'
import * as echarts from 'echarts'
import { getDashboard } from '@/js/Dashboard'

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

// 월간/주간/일간 단위로 데이터 가공
const labels = computed(() => {
  if (viewType.value === 'month') {
    const months = {}
    allData.value.forEach(item => {
      const month = item.date.slice(0, 7)
      if (!months[month]) months[month] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 }
      months[month].newmembers += item.newmembers
      months[month].newAdvertisers += item.newAdvertisers
      months[month].newInfluencers += item.newInfluencers
    })
    return Object.keys(months)
  } else if (viewType.value === 'week') {
    const weekMap = {};
    allData.value.forEach(item => {
      const weekLabel = getMonthWeekLabel(item.date);
      if (!weekMap[weekLabel]) weekMap[weekLabel] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 };
      weekMap[weekLabel].newmembers += item.newmembers;
      weekMap[weekLabel].newAdvertisers += item.newAdvertisers;
      weekMap[weekLabel].newInfluencers += item.newInfluencers;
    });
    return Object.keys(weekMap);
  } else {
    const days = {};
    allData.value.forEach(item => {
      const day = item.date;
      if (!days[day]) days[day] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 };
      days[day].newmembers += item.newmembers;
      days[day].newAdvertisers += item.newAdvertisers;
      days[day].newInfluencers += item.newInfluencers;
    });
    return Object.keys(days);
  }
})

const chartData = computed(() => {
  if (viewType.value === 'month') {
    const months = {}
    allData.value.forEach(item => {
      const month = item.date.slice(0, 7)
      if (!months[month]) months[month] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 }
      months[month].newmembers += item.newmembers
      months[month].newAdvertisers += item.newAdvertisers
      months[month].newInfluencers += item.newInfluencers
    })
    const arr = Object.values(months)
    return {
      newmembers: arr.map(i => i.newmembers),
      newAdvertisers: arr.map(i => i.newAdvertisers),
      newInfluencers: arr.map(i => i.newInfluencers)
    }
  } else if (viewType.value === 'week') {
    const weekMap = {};
    allData.value.forEach(item => {
      const weekLabel = getMonthWeekLabel(item.date);
      if (!weekMap[weekLabel]) weekMap[weekLabel] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 };
      weekMap[weekLabel].newmembers += item.newmembers;
      weekMap[weekLabel].newAdvertisers += item.newAdvertisers;
      weekMap[weekLabel].newInfluencers += item.newInfluencers;
    });
    const arr = Object.values(weekMap);
    return {
      newmembers: arr.map(i => i.newmembers),
      newAdvertisers: arr.map(i => i.newAdvertisers),
      newInfluencers: arr.map(i => i.newInfluencers)
    }
  } else {
    const days = {};
    allData.value.forEach(item => {
      const day = item.date;
      if (!days[day]) days[day] = { newmembers: 0, newAdvertisers: 0, newInfluencers: 0 };
      days[day].newmembers += item.newmembers;
      days[day].newAdvertisers += item.newAdvertisers;
      days[day].newInfluencers += item.newInfluencers;
    });
    const arr = Object.values(days);
    return {
      newmembers: arr.map(i => i.newmembers),
      newAdvertisers: arr.map(i => i.newAdvertisers),
      newInfluencers: arr.map(i => i.newInfluencers)
    };
  }
})

const lineChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['신규 회원', '신규 광고주', '신규 인플루언서'] },
  xAxis: { type: 'category', data: labels.value },
  yAxis: { type: 'value' },
  dataZoom: [
    { type: 'slider', start: 0, end: 100, xAxisIndex: 0 }
  ],
  series: [
    { name: '신규 회원', type: 'line', data: chartData.value.newmembers, smooth: true },
    { name: '신규 광고주', type: 'line', data: chartData.value.newAdvertisers, smooth: true },
    { name: '신규 인플루언서', type: 'line', data: chartData.value.newInfluencers, smooth: true }
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
      label: { show: false, position: 'center' },
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
        { value: contractStatus.value.active, name: '광고 진행중', itemStyle: { color: '#ff5f7b' } },
        { value: contractStatus.value.completed, name: '정산완료', itemStyle: { color: '#5fcfff' } }
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

onMounted(async () => {
  try {
    const res = await getDashboard();
    console.log('대시보드 응답', res.data);
    const data = Array.isArray(res.data) ? res.data[0] : res.data;
    if (data && data.DashboardSummary) {
      dashboardSummary.value = data.DashboardSummary;
    }
    if (data && data.LLM) {
      LLM.value = data.LLM;
    }
    if (data && data.contractStatus) {
      contractStatus.value = data.contractStatus;
    }
    // trendChart 데이터 변환 (json-server와 1:1 매칭)
    if (data && data.trendChart) {
      const newmembers = data.trendChart.newmembers['2024'] || {};
      const newAdvertisers = data.trendChart.newAdvertisers['2024'] || {};
      const newInfluencers = data.trendChart.newInfluencers['2024'] || {};
      const allDates = Array.from(new Set([
        ...Object.keys(newmembers),
        ...Object.keys(newAdvertisers),
        ...Object.keys(newInfluencers)
      ])).sort();
      allData.value = allDates.map(date => ({
        date,
        newmembers: newmembers[date] ?? 0,
        newAdvertisers: newAdvertisers[date] ?? 0,
        newInfluencers: newInfluencers[date] ?? 0
      }));
    }
  } catch (e) {
    console.error('대시보드 데이터 불러오기 실패', e);
  }
})
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
        <div class="stat-label">등록된 제품 수</div>
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
          <div class="chart-title">신규 유입 현황</div>
          <select v-model="viewType" class="chart-select">
            <option value="month">월간</option>
            <option value="week">주간</option>
            <option value="day">일간</option>
          </select>
        </div>
        <v-chart
          :option="lineChartOption"
          autoresize
          style="width: 100%; height: 320px"
        />
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
  background: #ff7b8a;
  color: #fff;
  border-radius: 16px;
  padding: 36px 0 28px 0;
  text-align: center;
  margin-bottom: 40px;
  box-shadow: 0 4px 16px rgba(255,123,138,0.08);
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.trend-title { font-size: 1.7rem; font-weight: bold; }
.trend-desc { margin-top: 10px; font-size: 1.15rem; }

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
  background: #7b5fff;
  color: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  min-width: 150px;
  box-shadow: 0 2px 12px rgba(123,95,255,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
}
.stat-card.sales {
  background: #7b5fff;
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
</style>
