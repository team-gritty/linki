<template>
  <div class="campaign-list">
    <div class="category-tabs">
      <div 
        v-for="category in categories" 
        :key="category.id"
        :class="['category-tab', { active: selectedCategory === category.id }]"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
      </div>
    </div>

    <h2 class="section-title">인기 캠페인</h2>
    <div class="campaign-grid">
      <div 
        v-for="campaign in popularCampaigns" 
        :key="campaign.id"
        class="campaign-card"
        @click="goToCampaignDetail(campaign.id)"
      >
        <img :src="campaign.image" :alt="campaign.title">
        <div class="campaign-info">
          <h3>{{ campaign.title }}</h3>
          <p>{{ campaign.description }}</p>
          <div class="campaign-meta">
            <span class="price">{{ campaign.price }}원</span>
            <span class="deadline">마감일: {{ campaign.deadline }}</span>
          </div>
        </div>
      </div>
    </div>

    <h2 class="section-title">전체 캠페인</h2>
    <div class="campaign-grid">
      <div 
        v-for="campaign in filteredCampaigns" 
        :key="campaign.id"
        class="campaign-card"
        @click="goToCampaignDetail(campaign.id)"
      >
        <img :src="campaign.image" :alt="campaign.title">
        <div class="campaign-info">
          <h3>{{ campaign.title }}</h3>
          <p>{{ campaign.description }}</p>
          <div class="campaign-meta">
            <span class="price">{{ campaign.price }}원</span>
            <span class="deadline">마감일: {{ campaign.deadline }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = [
  { id: 'all', name: '전체' },
  { id: 'beauty', name: '뷰티' },
  { id: 'fashion', name: '패션' },
  { id: 'sports', name: '스포츠' },
  { id: 'food', name: '음식' },
  { id: 'electronics', name: '전자기기' },
  { id: 'vlog', name: 'VLOG/라이프스타일' },
  { id: 'travel', name: '여행' },
  { id: 'music', name: '음악' },
  { id: 'education', name: '교육' },
  { id: 'pets', name: '동물/펫' }
]

const selectedCategory = ref('all')

// 임시 데이터 - 실제로는 API에서 가져와야 함
const campaigns = ref([
  {
    id: 1,
    title: '신제품 화장품 리뷰',
    description: '새로 출시된 화장품의 상세 리뷰를 진행해주실 뷰티 인플루언서를 찾습니다.',
    image: 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80',
    price: 100000,
    deadline: '2024-05-31',
    category: 'beauty'
  },
  {
    id: 2,
    title: '스포츠웨어 착용 리뷰',
    description: '신규 출시된 기능성 스포츠웨어 착용 후기 및 운동 영상 제작',
    image: 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?auto=format&fit=crop&w=800&q=80',
    price: 150000,
    deadline: '2024-05-15',
    category: 'sports'
  },
  {
    id: 3,
    title: '신규 레스토랑 방문 리뷰',
    description: '강남역 인근 새로 오픈한 레스토랑 방문 후기 및 메뉴 리뷰',
    image: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80',
    price: 80000,
    deadline: '2024-05-20',
    category: 'food'
  },
  {
    id: 4,
    title: '여행 브이로그 제작',
    description: '제주도 신규 관광지 방문 및 숙소 리뷰 브이로그 제작',
    image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',
    price: 200000,
    deadline: '2024-06-10',
    category: 'travel'
  },
  {
    id: 5,
    title: '무선이어폰 리뷰',
    description: '신규 출시된 프리미엄 무선이어폰 언박싱 및 사용 후기',
    image: 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80',
    price: 120000,
    deadline: '2024-05-25',
    category: 'electronics'
  }
])

const popularCampaigns = computed(() => {
  return campaigns.value.slice(0, 4) // 상위 4개만 표시
})

const filteredCampaigns = computed(() => {
  if (selectedCategory.value === 'all') {
    return campaigns.value
  }
  return campaigns.value.filter(campaign => campaign.category === selectedCategory.value)
})

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
}

const goToCampaignDetail = (campaignId) => {
  router.push(`/campaign/${campaignId}`)
}
</script>

<style scoped>
.campaign-list {
  padding: 20px;
}

.category-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.category-tab {
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #f5f5f5;
  cursor: pointer;
  white-space: nowrap;
}

.category-tab.active {
  background-color: #7c3aed;
  color: white;
}

.section-title {
  margin: 20px 0;
  font-size: 1.5rem;
  font-weight: bold;
}

.campaign-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.campaign-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.campaign-card:hover {
  transform: translateY(-4px);
}

.campaign-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.campaign-info {
  padding: 15px;
}

.campaign-info h3 {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
}

.campaign-info p {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 0.9rem;
}

.campaign-meta {
  display: flex;
  justify-content: space-between;
  color: #7c3aed;
  font-size: 0.9rem;
}
</style> 