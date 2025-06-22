<template>
  <div class="subscription-manage-container">
    <h2 class="subscription-manage-title">나의 구독 관리</h2>
    <div class="subscription-manage-form">  
      <div v-if="loading" class="manage-loading">구독 정보를 불러오는 중...</div>
      <div v-else-if="error" class="manage-error">{{ error }}</div>
      <div v-else class="manage-info-wrap">
        <div class="manage-info-row">
          <span class="manage-label">구독 시작일</span>
          <span class="manage-value">{{ info.startDate }}</span>
        </div>
        <div class="manage-info-row">
          <span class="manage-label">다음 결제일</span>
          <span class="manage-value">{{ info.nextBillingDate }}</span>
        </div>
        <div class="manage-info-row">
          <span class="manage-label">결제수단</span>
          <span class="manage-value">{{ info.paymentMethod }}</span>
        </div>
        <div class="manage-info-row">
          <span class="manage-label">구독 상태</span>
          <span class="manage-value status-{{ info.status }}">{{ statusText }}</span>
        </div>
        <div class="manage-info-row contract-count-row">
          <span class="manage-label">구독 서비스로 성사한 계약</span>
          <span class="manage-value contract-count">{{ info.contractCount }}건</span>
        </div>
        <div class="button-group">
         <button class="refund-btn" @click="goRefund">구독 환불 신청</button>
        </div>
      </div>
    </div>

    <div class="benefit-section">
      <h2 class="benefit-title">구독 혜택</h2>
      <ul class="benefit-list">
        <li v-for="benefit in benefits" :key="benefit.title" class="benefit-item">
          <span class="benefit-icon"><i :class="benefit.icon"></i></span>
          <span class="benefit-content">
            <span class="benefit-title-text">{{ benefit.title }}</span>
            <span class="benefit-desc">{{ benefit.desc }}</span>
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { getMySubscriptionInfo } from '@/api/subscription'

export default {
  name: 'MyPageSubscriptionManage',
  data() {
    return {
      info: {},
      loading: false,
      error: null,
      benefits: [
        {
          icon: 'fas fa-bullhorn',
          title: '무제한 캠페인 등록',
          desc: '구독 회원은 원하는 만큼 캠페인을 자유롭게 등록할 수 있습니다.'
        },
        {
          icon: 'fas fa-users',
          title: '무제한 인플루언서 제안',
          desc: '원하는 모든 인플루언서에게 제한 없이 광고를 제안할 수 있습니다.'
        },
        {
          icon: 'fas fa-chart-bar',
          title: 'Linki 분석시스템 이용 가능',
          desc: '캠페인 성과와 인플루언서 분석을 Linki의 고급 분석 시스템으로 확인할 수 있습니다.'
        }
      ]
    }
  },
  computed: {
    statusText() {
      switch (this.info.status) {
        case 'active': return '이용중'
        case 'paused': return '일시정지'
        case 'canceled': return '해지됨'
        default: return '-'
      }
    }
  },
  async mounted() {
    this.fetchInfo()
  },
  methods: {
    async fetchInfo() {
      this.loading = true
      this.error = null
      try {
        const res = await getMySubscriptionInfo()
        this.info = res.data
      } catch (e) {
        this.error = '구독 정보를 불러오지 못했습니다.'
      } finally {
        this.loading = false
      }
    },
    goRefund() {
      this.$emit('update:currentMenu', 'subscription.refund')
    }
  }
}
</script>

<style scoped>
.subscription-manage-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.subscription-manage-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #333;
  text-align: center;
}

.subscription-manage-form {
  background: #faf7ff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(124,58,237,0.06);
  padding: 32px 28px 28px 28px;
  margin-bottom: 2rem;
}

.manage-info-wrap {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.manage-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.08rem;
  padding-bottom: 4px;
  border-bottom: 1px solid #f3f4f6;
}
.contract-count-row {
  background: #ede9fe;
  border-radius: 8px;
  padding: 8px 12px;
  margin-top: 8px;
  font-weight: 600;
  border: 1px solid #a78bfa22;
}
.contract-count {
  color: #7c3aed;
  font-weight: bold;
}
.manage-label {
  color: #7c3aed;
  font-weight: 600;
}
.manage-value {
  color: #333;
  font-weight: 500;
}
.status-active {
  color: #7c3aed;
}
.status-paused {
  color: #f59e42;
}
.status-canceled {
  color: #dc2626;
}
.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}
.refund-btn {
  padding: 8px 24px;
  background-color: #8B5CF6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}
.refund-btn:hover {
  background-color: #7C3AED;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}
.manage-loading, .manage-error {
  text-align: center;
  color: #a78bfa;
  font-size: 1.05rem;
  padding: 24px 0;
}
.manage-error {
  color: #dc2626;
}
.benefit-section {
  margin: 38px auto 0 auto;
  max-width: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(124,58,237,0.07);
  padding: 28px 24px 18px 24px;
}
.benefit-title {
  font-size: 1.18rem;
  font-weight: 700;
  color: #7c3aed;
  margin-bottom: 18px;
  text-align: left;
}
.benefit-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: #f5f3ff;
  border-radius: 10px;
  padding: 14px 16px;
  box-shadow: 0 1px 4px rgba(124,58,237,0.04);
}
.benefit-icon {
  font-size: 1.5rem;
  color: #a78bfa;
  margin-top: 2px;
}
.benefit-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.benefit-title-text {
  font-weight: 600;
  color: #7c3aed;
  font-size: 1.05rem;
}
.benefit-desc {
  color: #6b7280;
  font-size: 0.97rem;
}
</style> 