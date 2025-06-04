<template>
  <div class="proposal-page">
    <div class="proposal-container">
      <h1>제안서 작성</h1>
      <div class="campaign-info">
        <h2>{{ campaign?.title }}</h2>
        <p class="campaign-meta">
          <span class="price">{{ campaign?.price }}원</span>
          <span class="deadline">마감일: {{ campaign?.deadline }}</span>
        </p>
      </div>

      <form @submit.prevent="submitProposal" class="proposal-form">
        <div class="form-section">
          <h3>기본 정보</h3>
          <div class="form-group">
            <label for="name">이름</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              required
            >
          </div>
          <div class="form-group">
            <label for="email">이메일</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email" 
              required
            >
          </div>
          <div class="form-group">
            <label for="phone">연락처</label>
            <input 
              type="tel" 
              id="phone" 
              v-model="formData.phone" 
              required
            >
          </div>
          <div class="form-group">
            <label for="sns">SNS 계정 주소</label>
            <input 
              type="url" 
              id="sns" 
              v-model="formData.sns" 
              required
              placeholder="예: https://instagram.com/your_account"
            >
          </div>
          <div class="form-group">
            <label for="followers">팔로워 수</label>
            <input 
              type="number" 
              id="followers" 
              v-model="formData.followers" 
              required
            >
          </div>
        </div>

        <div class="form-section">
          <h3>제안 내용</h3>
          <div class="form-group">
            <label for="experience">관련 경험</label>
            <textarea 
              id="experience" 
              v-model="formData.experience" 
              rows="4"
              placeholder="관련 분야의 경험을 자세히 설명해주세요."
              required
            ></textarea>
          </div>
          <div class="form-group">
            <label for="proposal">제안 내용</label>
            <textarea 
              id="proposal" 
              v-model="formData.proposal" 
              rows="6"
              placeholder="캠페인에 대한 구체적인 제안 내용을 작성해주세요."
              required
            ></textarea>
          </div>
        </div>

        <button type="submit" class="submit-button">제안서 제출하기</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const campaign = ref(null)

const formData = ref({
  name: '',
  email: '',
  phone: '',
  sns: '',
  followers: '',
  experience: '',
  proposal: ''
})

onMounted(async () => {
  // 실제로는 API에서 캠페인 정보를 가져와야 함
  campaign.value = {
    id: route.params.id,
    title: '신제품 화장품 리뷰',
    price: 100000,
    deadline: '2024-05-31'
  }
})

const submitProposal = async () => {
  try {
    // 실제로는 API로 데이터를 전송해야 함
    console.log('제출된 제안서:', formData.value)
    
    // 성공 알림
    alert('제안서가 성공적으로 제출되었습니다.')
    
    // 캠페인 상세 페이지로 돌아가기
    router.push(`/campaign/${route.params.id}`)
  } catch (error) {
    alert('제안서 제출 중 오류가 발생했습니다. 다시 시도해주세요.')
  }
}
</script>

<style scoped>
.proposal-page {
  padding: 40px 20px;
}

.proposal-container {
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  font-size: 2rem;
  margin-bottom: 30px;
}

.campaign-info {
  background: #f8f8f8;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.campaign-info h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.campaign-meta {
  color: #7c3aed;
  display: flex;
  gap: 20px;
}

.form-section {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.form-section h3 {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #444;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.submit-button {
  width: 100%;
  padding: 15px;
  background-color: #7c3aed;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
  background-color: #6d28d9;
}

@media (max-width: 768px) {
  .proposal-page {
    padding: 20px 15px;
  }
  
  .form-section {
    padding: 20px;
  }
}
</style> 