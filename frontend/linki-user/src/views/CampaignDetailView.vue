<template>
  <div class="campaign-detail" v-if="campaign">
    <div class="campaign-header">
      <h1>{{ campaign.title }}</h1>
      <div class="campaign-meta">
        <span class="price">{{ campaign.price }}원</span>
        <span class="deadline">마감일: {{ campaign.deadline }}</span>
      </div>
      <button class="apply-button" @click="goToProposal">캠페인 신청하기</button>
    </div>

    <div class="campaign-content">
      <div class="campaign-images">
        <img :src="campaign.image" :alt="campaign.title">
      </div>

      <div class="campaign-info">
        <section class="info-section">
          <h2>캠페인 설명</h2>
          <p>{{ campaign.description }}</p>
        </section>

        <section class="info-section">
          <h2>요구사항</h2>
          <ul>
            <li v-for="(requirement, index) in campaign.requirements" :key="index">
              {{ requirement }}
            </li>
          </ul>
        </section>

        <section class="info-section">
          <h2>광고주 정보</h2>
          <div class="advertiser-info">
            <img :src="campaign.advertiser.avatar" :alt="campaign.advertiser.name" class="advertiser-avatar">
            <div class="advertiser-details">
              <h3>{{ campaign.advertiser.name }}</h3>
              <div class="rating">
                <span class="stars">★★★★★</span>
                <span class="rating-value">{{ campaign.advertiser.rating }}/5.0</span>
              </div>
            </div>
          </div>
        </section>

        <section class="info-section">
          <h2>광고주 리뷰</h2>
          <div class="reviews">
            <div v-for="review in campaign.reviews" :key="review.id" class="review-card">
              <div class="review-header">
                <img :src="review.influencer.avatar" :alt="review.influencer.name" class="reviewer-avatar">
                <div class="reviewer-info">
                  <h4>{{ review.influencer.name }}</h4>
                  <div class="rating">
                    <span class="stars">★★★★★</span>
                    <span class="rating-value">{{ review.rating }}/5.0</span>
                  </div>
                </div>
              </div>
              <p class="review-content">{{ review.content }}</p>
              <span class="review-date">{{ review.date }}</span>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import httpClient from '@/utils/httpRequest'

const route = useRoute()
const router = useRouter()
const campaign = ref(null)

onMounted(async () => {
  try {
    // 실제 API 연동 시 사용할 코드
    // const response = await httpClient.get(`/v1/api/campaigns/${route.params.id}`)
    // campaign.value = response.data

    // 임시 데이터
    campaign.value = {
      id: route.params.id,
      title: '신제품 화장품 리뷰',
      description: '새로 출시된 화장품의 상세 리뷰를 진행해주실 뷰티 인플루언서를 찾습니다. 제품의 특징과 사용감을 상세히 전달해주시면 됩니다. 메이크업 전/후 비교 사진과 함께 상세한 사용 후기를 작성해주세요.',
      image: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80',
      price: 100000,
      deadline: '2024-05-31',
      category: 'beauty',
      requirements: [
        '팔로워 1만명 이상',
        '뷰티 컨텐츠 제작 경험 필수',
        '최근 3개월 이내 뷰티 제품 리뷰 3개 이상',
        '연령대: 20-35세',
        '메이크업 전/후 사진 촬영 필수',
        '제품 사용 영상 제작 필수'
      ],
      advertiser: {
        name: '코스메틱스 주식회사',
        avatar: 'https://images.unsplash.com/photo-1579664531470-e531e6b2e03f?auto=format&fit=crop&w=200&q=80',
        rating: 4.8
      },
      reviews: [
        {
          id: 1,
          influencer: {
            name: '뷰티블로거',
            avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80'
          },
          rating: 5,
          content: '광고주님과의 소통이 원활했고, 제품도 좋았습니다. 캠페인 진행 과정에서 피드백도 빠르고 명확해서 작업하기 좋았어요. 다음에도 함께 작업하고 싶습니다!',
          date: '2024-03-15'
        },
        {
          id: 2,
          influencer: {
            name: '메이크업아티스트',
            avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80'
          },
          rating: 4.5,
          content: '친절하게 캠페인 진행해주셔서 감사합니다. 제품 퀄리티도 훌륭했고, 리뷰 작성에 필요한 자료도 잘 준비해주셨어요. 특히 제품의 특장점을 잘 설명해주셔서 컨텐츠 제작이 수월했습니다.',
          date: '2024-03-10'
        }
      ]
    }
  } catch (error) {
    console.error('캠페인 정보 로딩 실패:', error)
  }
})

const goToProposal = () => {
  router.push({
    name: 'campaign-proposal',
    params: { 
      id: campaign.value.id 
    }
  })
}
</script>

<style scoped>
.campaign-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.campaign-header {
  margin-bottom: 30px;
}

.campaign-header h1 {
  font-size: 2rem;
  margin-bottom: 10px;
}

.campaign-meta {
  display: flex;
  gap: 20px;
  color: #7c3aed;
  font-size: 1.1rem;
}

.campaign-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.campaign-images img {
  width: 100%;
  border-radius: 12px;
}

.info-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.info-section h2 {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: #333;
}

.advertiser-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.advertiser-avatar {
  width: 60px;
  height: 60px;
  border-radius: 30px;
  object-fit: cover;
}

.rating {
  color: #ffd700;
}

.rating-value {
  color: #666;
  margin-left: 5px;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-card {
  padding: 15px;
  border-radius: 8px;
  background: #f8f8f8;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  object-fit: cover;
}

.review-content {
  color: #444;
  margin-bottom: 10px;
}

.review-date {
  color: #888;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .campaign-content {
    grid-template-columns: 1fr;
  }
}

.apply-button {
  margin-top: 20px;
  padding: 15px 30px;
  background-color: #7c3aed;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.apply-button:hover {
  background-color: #6d28d9;
}
</style> 