<template>
  <div class="subscription-refund-content">
    <h1 class="content-title refund-title">구독 환불 신청</h1>
    <div class="refund-info-box">
      <p class="refund-desc">
        구독 환불은 결제일로부터 7일 이내에만 신청할 수 있습니다.<br />
        환불 정책을 꼭 확인해 주세요.
      </p>
      <ul class="refund-policy-list">
        <li>환불 신청 시, 서비스 이용이 즉시 중단됩니다.</li>
        <li>환불은 영업일 기준 3~5일 이내 결제수단으로 환급됩니다.</li>
        <li>이벤트/프로모션 등으로 지급된 혜택은 환불 시 회수될 수 있습니다.</li>
        <li>자세한 환불 정책은 <a href="#" class="policy-link">이용약관</a>을 참고해 주세요.</li>
      </ul>
    </div>
    <form class="refund-form" @submit.prevent="onSubmit">
      <label class="refund-label">환불 사유를 선택해 주세요</label>
      <select v-model="reason" class="refund-select" required>
        <option value="" disabled>사유 선택</option>
        <option value="not_needed">서비스가 더 이상 필요 없음</option>
        <option value="price">가격이 부담됨</option>
        <option value="feature">원하는 기능/혜택 부족</option>
        <option value="bug">오류/불편함</option>
        <option value="other">기타</option>
      </select>
      <textarea v-if="reason === 'other'" v-model="detail" class="refund-textarea" placeholder="기타 사유를 입력해 주세요" rows="3" />
      <button class="refund-btn" :disabled="loading">환불 신청하기</button>
    </form>
    <div v-if="loading" class="refund-status loading">환불 신청 처리 중...</div>
    <div v-if="success" class="refund-status success">환불 신청이 완료되었습니다. 영업일 기준 3~5일 내 환급됩니다.</div>
    <div v-if="error" class="refund-status error">{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: 'MyPageSubscriptionRefund',
  data() {
    return {
      reason: '',
      detail: '',
      loading: false,
      success: false,
      error: null
    }
  },
  methods: {
    async onSubmit() {
      if (!this.reason) return
      this.loading = true
      this.success = false
      this.error = null
      try {
        //************************************************** */
        // 실제 환불 API 연동 시 여기에 axios 호출
        //************************************************** */
        await new Promise((resolve) => setTimeout(resolve, 1200))
        this.success = true
        this.reason = ''
        this.detail = ''
      } catch (e) {
        this.error = '환불 신청 처리 중 오류가 발생했습니다.'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.subscription-refund-content {
  max-width: 520px;
  margin: 0 auto;
  padding: 40px 0 32px 0;
}
.refund-title {
  font-size: 1.7rem;
  font-weight: 700;
  margin-bottom: 18px;
  text-align: center;
}
.refund-info-box {
  background: #faf7ff;
  border-radius: 14px;
  padding: 22px 20px 18px 20px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(124,58,237,0.06);
}
.refund-desc {
  color: #7c3aed;
  font-size: 1.05rem;
  margin-bottom: 12px;
  text-align: center;
}
.refund-policy-list {
  color: #6b7280;
  font-size: 0.97rem;
  margin: 0;
  padding-left: 18px;
}
.policy-link {
  color: #a78bfa;
  text-decoration: underline;
}
.refund-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 18px;
}
.refund-label {
  font-weight: 600;
  margin-bottom: 2px;
}
.refund-select {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1.5px solid #a78bfa;
  font-size: 1rem;
  outline: none;
}
.refund-textarea {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1.5px solid #a78bfa;
  font-size: 1rem;
  outline: none;
  resize: vertical;
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
  margin-top: 8px;
}
.refund-btn:hover {
  background-color: #7C3AED;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}
.refund-btn:disabled {
  background-color: #E5E7EB;
  color: #9CA3AF;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
.refund-status {
  text-align: center;
  margin-top: 18px;
  font-size: 1.05rem;
}
.refund-status.success {
  color: #7c3aed;
}
.refund-status.error {
  color: #dc2626;
}
.refund-status.loading {
  color: #a78bfa;
}
</style> 