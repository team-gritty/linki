<template>
  <div class="subscription-apply-content">
    <section class="subscribe-hero">
      <h1 class="content-title subscribe-title">최고의 광고경험을 누려보세요</h1>
      <p class="subscribe-desc">지금 구독하고 더 많은 혜택을 누려보세요.</p>
    </section>

    <!-- 구독 안내/버튼 카드 -->
    <div class="subscribe-action-card">
      <template v-if="isBillingRegistered">
        <button class="subscribe-btn" @click="startSubscription">지금 바로 구독 시작하기</button>
        <button class="secondary-btn" @click="requestBillingAuth">카드 변경하기</button>
      </template>
      <template v-else>
        <button class="subscribe-btn" @click="requestBillingAuth">카드 등록하고 구독 시작하기</button>
      </template>
    </div>

    <!-- 혜택 아이콘/설명 영역 -->
    <section class="benefit-section">
      <div class="benefit-item" v-for="benefit in benefits" :key="benefit.title">
        <div class="benefit-icon">
          <i :class="benefit.icon"></i>
        </div>
        <div class="benefit-info">
          <h3>{{ benefit.title }}</h3>
          <p>{{ benefit.desc }}</p>
        </div>
      </div>
    </section>

    <!-- 상품/캠페인 나열 (재사용) -->
    <section class="campaign-section">
      <h2 class="section-title">AI 추천 연결 상품</h2>
      <div v-if="loading" class="loading-state">불러오는 중...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else class="campaign-grid">
        <div class="campaign-card" v-for="item in products" :key="item.id">
          <div class="card-img-wrap">
            <img :src="item.img" :alt="item.name" />
          </div>
          <div class="campaign-info">
            <h3>{{ item.name }}</h3>
            <div class="campaign-meta">
              <span class="category">{{ item.category }}</span>
              <span class="price">{{ item.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { getRecommendedProducts } from '@/api/product'
import httpClient from '@/utils/httpRequest'
import { useAccountStore } from '@/stores/account'

export default {
  name: 'MyPageSubscriptionApply',
  data() {
    return {
      benefits: [
        {
          icon: 'fas fa-link',
          title: '브랜드와 직접 연결되는 기회',
          desc: '검증된 광고주와의 제안을 직접 신청하세요. 본인의 SNS에 맞는 광고만 골라 신청할 수 있어요.'
        },
        {
          icon: 'fas fa-shield-alt',
          title: '투명하고 안전한 계약 프로세스',
          desc: '트랜잭션 기반 정산계약, 청약시스템을 통해 실패할 수 없는 광고 환경이 가능해집니다.'
        },
        {
          icon: 'fas fa-coins',
          title: '성과 기반 보상과 자동 정산',
          desc: '광고 노출에 따라 더 많은 혜택과 광고비를 얻으세요.'
        },
        {
          icon: 'fas fa-chart-line',
          title: '나의 성장효율을 한눈에',
          desc: '당신의 영향력을 다양한 분석과 객관적인 지표를 통해 성장률을 이뤄보세요.'
        }
      ],
      products: [],
      loading: false,
      error: null,
      userEmail: '',
      userName: '',
      isBillingRegistered: false
    }
  },
  async mounted() {
    // TossPayments SDK 동적 로드
    if (!document.querySelector('script[src="https://js.tosspayments.com/v2/standard"]')) {
      const script = document.createElement('script');
      script.src = 'https://js.tosspayments.com/v2/standard';
      script.async = true;
      document.head.appendChild(script);
      // 스크립트가 로드될 때까지 기다림 (필요시 Promise로 처리 가능)
    }
    this.fetchProducts()
    this.checkBillingStatus()
    // 로그인 유저 정보 가져오기
    try {
      const res = await httpClient.get('/v1/api/user/email-name')
      this.userEmail = res.data.email
      this.userName = res.data.name
    } catch (e) {
      this.userEmail = ''
      this.userName = ''
    }
  },
  methods: {
    async checkBillingStatus() {
      try {
        const res = await httpClient.get('/v1/subscribe-service/api/subscribe/billing-registered')
        this.isBillingRegistered = res.data.billingRegistered
      } catch (e) {
        console.error('카드 등록 여부를 확인할 수 없습니다.', e)
        this.isBillingRegistered = false // 에러 발생 시 기본값
      }
    },
    startSubscription() {
      // TODO: 카드가 등록된 유저가 실제 구독을 시작하는 로직 구현
      alert('구독을 시작합니다! (실제 API 연동 필요)')
    },
    async fetchProducts() {
      this.loading = true
      this.error = null
      try {
        const res = await getRecommendedProducts()
        this.products = res.data
      } catch (e) {
        this.error = '상품을 불러오지 못했습니다.'
      } finally {
        this.loading = false
      }
    },
    async requestBillingAuth() {
      // Pinia store에서 userId 가져오기
      const accountStore = useAccountStore()
      const customerKey = accountStore.getUser?.userId

      if (!customerKey) {
        // userId가 없으면 로그인 페이지로 이동
        window.alert('로그인이 필요합니다.')
        this.$router.push('/login') // Vue Router 사용 시
        return
      }

      const clientKey = "test_ck_AQ92ymxN342adlxYQPByrajRKXvd";
      if (!window.TossPayments) {
        alert('토스페이먼츠 SDK가 로드되지 않았습니다.');
        return;
      }
      const tossPayments = window.TossPayments(clientKey);
      const payment = tossPayments.payment({ customerKey });
      try {
        await payment.requestBillingAuth({
          method: "CARD",
          successUrl: window.location.origin + "/success",
          failUrl: window.location.origin + "/fail",
          customerEmail: this.userEmail,
          customerName: this.userName,
        });
      } catch (e) {
        console.error(e);
      }
    }
  }
}
</script>

<style scoped>
.subscription-apply-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 20px 0 20px;
}
.subscribe-hero {
  text-align: center;
  margin-bottom: 10px;
}
.subscribe-title {
  font-size: 2.2rem;
  font-weight: 800;
  margin-bottom: 10px;
  letter-spacing: -1px;
}
.subscribe-desc {
  color: #6b7280;
  font-size: 1.1rem;
  margin-bottom: 0;
}
.subscribe-action-card {
  background: #faf7ff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(124,58,237,0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
  padding: 32px 24px 28px 24px;
  margin: 0 auto 48px auto;
  max-width: 500px;
}
.subscribe-input {
  padding: 14px 24px;
  border: 2px solid #a78bfa;
  border-radius: 24px;
  background: #fff;
  color: #a78bfa;
  font-size: 1.05rem;
  width: 100%;
  max-width: 350px;
  outline: none;
  margin-bottom: 0;
}
.subscribe-btn {
  background: linear-gradient(90deg, #a78bfa 0%, #7c3aed 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 14px 36px;
  font-size: 2rem;
  font-weight: 100px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(124,58,237,0.10);
  transition: background 0.2s, transform 0.15s;
}
.subscribe-btn:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #a78bfa 100%);
  transform: translateY(-2px) scale(1.03);
}
.secondary-btn {
  background: transparent;
  color: #7c3aed;
  border: 2px solid #a78bfa;
  border-radius: 20px;
  padding: 12px 28px;
  font-size: 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  max-width: 350px;
}
.secondary-btn:hover {
  background: #faf7ff;
  border-color: #7c3aed;
}
.benefit-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 28px;
  margin-bottom: 56px;
}
.benefit-item {
  background: #fff;
  border-radius: 16px;
  padding: 32px 20px 28px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(124,58,237,0.07);
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s, transform 0.18s;
}
.benefit-item:hover {
  box-shadow: 0 6px 24px rgba(124,58,237,0.13);
  transform: translateY(-3px) scale(1.03);
}
.benefit-icon {
  font-size: 2.7rem;
  color: #a78bfa;
  margin-bottom: 16px;
}
.benefit-info h3 {
  font-size: 1.13rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.benefit-info p {
  color: #6b7280;
  font-size: 1.01rem;
}
.campaign-section {
  margin-top: 32px;
}
.section-title {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 18px;
  color: #7c3aed;
  letter-spacing: -0.5px;
}
.campaign-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}
.campaign-card {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(124,58,237,0.07);
  cursor: pointer;
  transition: box-shadow 0.18s, transform 0.16s;
  background: white;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.campaign-card:hover {
  box-shadow: 0 8px 28px rgba(124,58,237,0.16);
  transform: translateY(-4px) scale(1.03);
}
.card-img-wrap {
  width: 100%;
  height: 180px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.campaign-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-bottom: 1px solid #f3f4f6;
}
.campaign-info {
  padding: 18px 16px 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.campaign-info h3 {
  margin: 0 0 4px 0;
  font-size: 1.08rem;
  color: #111827;
  font-weight: 600;
}
.campaign-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.97rem;
}
.category {
  background: #f3f4f6;
  padding: 4px 10px;
  border-radius: 4px;
  color: #7c3aed;
  font-weight: 500;
}
.price {
  color: #7c3aed;
  font-weight: bold;
  font-size: 1.01rem;
}
@media (max-width: 900px) {
  .benefit-section {
    grid-template-columns: 1fr;
    gap: 18px;
  }
  .subscribe-action-card {
    max-width: 100%;
  }
}
@media (max-width: 768px) {
  .campaign-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .subscription-apply-content {
    padding: 24px 6px 0 6px;
  }
  .benefit-item {
    padding: 22px 10px 18px 10px;
  }
  .campaign-info {
    padding: 12px 8px 10px 8px;
  }
}
</style> 