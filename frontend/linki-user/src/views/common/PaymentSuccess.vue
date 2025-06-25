<template>
  <div class="success-container">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>카드 정보를 등록하고 있습니다. 잠시만 기다려주세요...</p>
    </div>
    <div v-else-if="error" class="error-card">
      <div class="error-icon">
        <i class="fas fa-times-circle"></i>
      </div>
      <h1 class="error-title">오류 발생</h1>
      <p class="error-message">{{ error }}</p>
      <div class="button-group">
        <button @click="goToHome" class="btn-home">홈으로 이동</button>
      </div>
    </div>
    <div v-else class="success-card">
      <div class="success-icon">
        <i class="fas fa-check-circle"></i>
      </div>
      <h1 class="success-title">카드 등록 완료</h1>
      <p class="success-message">
        회원님의 카드가 정상적으로 등록되었습니다.
      </p>
      <div class="button-group">
        <button @click="goToHome" class="btn-home">홈으로 이동</button>
        <button @click="goToMyPage" class="btn-mypage">마이페이지로 이동</button>
      </div>
    </div>
  </div>
</template>

<script>
import httpClient from '@/utils/httpRequest'

export default {
  name: 'PaymentSuccess',
  data() {
    return {
      loading: true,
      error: null
    }
  },
  async mounted() {
    const { customerKey, authKey } = this.$route.query

    if (!customerKey || !authKey) {
      this.error = '잘못된 접근입니다. 인증 정보가 없습니다.'
      this.loading = false
      return
    }

    try {
      const requestBody = { customerKey, authKey }
      await httpClient.post(`/v1/payment-service/api/billing/success`, requestBody)
      this.loading = false
    } catch (e) {
      console.error('빌링키 확인 요청 실패:', e)
      this.error = '카드 등록 과정에서 오류가 발생했습니다. 다시 시도해주세요.'
      this.loading = false
    }
  },
  methods: {
    goToHome() {
      this.$router.push('/')
    },
    goToMyPage() {
      // 역할에 따라 다른 마이페이지로 이동
      // 이 부분은 실제 로직에 맞게 수정이 필요할 수 있습니다.
      this.$router.push('/mypage/influencer')
    }
  }
}
</script>

<style scoped>
.success-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background-color: #f8f9fa;
  padding: 20px;
}

.success-card {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(124, 58, 237, 0.12);
  padding: 48px 40px;
  text-align: center;
  max-width: 550px;
  width: 100%;
  border: 1px solid #e9d5ff;
}

.success-icon {
  font-size: 5rem;
  color: #8b5cf6;
  margin-bottom: 24px;
}

.success-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #4c1d95;
  margin-bottom: 16px;
}

.success-message {
  font-size: 1.1rem;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 32px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 16px;
}

button {
  border-radius: 12px;
  padding: 12px 28px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease-in-out;
}

.btn-home {
  background-color: #8b5cf6;
  color: white;
  box-shadow: 0 4px 14px rgba(139, 92, 246, 0.25);
}

.btn-home:hover {
  background-color: #7c3aed;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.3);
}

.btn-mypage {
  background-color: #f3f4f6;
  color: #4c1d95;
  border: 1px solid #e5e7eb;
}

.btn-mypage:hover {
  background-color: #e5e7eb;
  border-color: #d1d5db;
  transform: translateY(-2px);
}

.loading-state {
  text-align: center;
  color: #4c1d95;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #8b5cf6;
  animation: spin 1s ease infinite;
  margin: 0 auto 20px auto;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-card {
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(239, 68, 68, 0.12);
  padding: 48px 40px;
  text-align: center;
  max-width: 550px;
  width: 100%;
  border: 1px solid #fecaca;
}

.error-icon {
  font-size: 5rem;
  color: #ef4444;
  margin-bottom: 24px;
}

.error-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #b91c1c;
  margin-bottom: 16px;
}

.error-message {
  font-size: 1.1rem;
  color: #7f1d1d;
  line-height: 1.6;
  margin-bottom: 32px;
}
</style> 