<template>
  <div class="subscription-apply-content">
    <section class="subscribe-hero">
      <h1 class="content-title subscribe-title">최고의 광고주 경험을 누려보세요</h1>
      <p class="subscribe-desc">지금 구독하고 더 많은 혜택을 누려보세요.</p>
    </section>

    <!-- 구독 안내/버튼 카드 -->
    <div class="subscribe-action-card">
      <button class="subscribe-btn">지금 바로 구독하고 신청하기</button>
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
  </div>
</template>

<script>
export default {
  name: 'MyPageSubscriptionApply',
  data() {
    return {
      benefits: [
        {
          icon: 'fas fa-users',
          title: '다양한 인플루언서와 연결',
          desc: '검증된 인플루언서와의 제안을 직접 신청하세요. 브랜드에 맞는 인플루언서만 골라 신청할 수 있어요.'
        },
        {
          icon: 'fas fa-shield-alt',
          title: '투명하고 안전한 계약 프로세스',
          desc: '트랜잭션 기반 정산계약, 청약시스템을 통해 실패할 수 없는 광고 환경이 가능해집니다.'
        },
        {
          icon: 'fas fa-chart-line',
          title: '성과 기반 광고 관리',
          desc: '광고 노출과 성과를 실시간으로 모니터링하고 관리하세요.'
        },
        {
          icon: 'fas fa-chart-bar',
          title: '상세한 분석 리포트',
          desc: '브랜드의 영향력과 성장을 다양한 분석과 객관적인 지표를 통해 확인하세요.'
        }
      ]
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
.content-title {
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
  .subscription-apply-content {
    padding: 24px 6px 0 6px;
  }
  .benefit-item {
    padding: 22px 10px 18px 10px;
  }
}
</style> 