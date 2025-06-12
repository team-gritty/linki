<template>
  <div class="subscription-manage-content">
    <h1 class="content-title">나의 구독 관리</h1>
    <div class="content-box">
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
        <button class="refund-btn" @click="goRefund">구독 환불 신청</button>
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
import { getMySubscriptionInfo } from '@/api/subscribeInfo'

export default {
  name: 'MyPageSubscriptionManage',
  data() {
    return {
      info: {},
      loading: false,
      error: null,
      benefits: [
        {
          icon: 'fas fa-infinity',
          title: '무제한 제품 게시',
          desc: '구독 회원은 원하는 만큼 제품을 자유롭게 등록할 수 있습니다.'
        },
        {
          icon: 'fas fa-bullhorn',
          title: '무제한 광고신청',
          desc: '참여하고 싶은 모든 캠페인에 제한 없이 광고를 신청할 수 있습니다.'
        },
        {
          icon: 'fas fa-chart-bar',
          title: 'Linki 분석시스템 이용 가능',
          desc: '내 채널/캠페인 성과를 Linki의 고급 분석 시스템으로 확인할 수 있습니다.'
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
.content-box {
  background: #faf7ff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(124,58,237,0.06);
  padding: 32px 28px 28px 28px;
  max-width: 420px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
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
.refund-btn {
  margin-top: 18px;
  background: linear-gradient(90deg, #a78bfa 0%, #7c3aed 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  padding: 13px 0;
  font-size: 1.08rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s;
}
.refund-btn:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #a78bfa 100%);
  transform: translateY(-2px) scale(1.03);
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