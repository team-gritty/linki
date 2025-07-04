<template>
  <div class="proposal-page">
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>처리 중입니다...</p>
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else class="proposal-container">
      <h1>제안서 작성</h1>
      
      <div v-if="campaign" class="campaign-info">
        <div class="campaign-header">
          <h2>{{ campaign.campaignName }}</h2>
        </div>
        <div class="campaign-details">
          <span class="category">{{ campaign.campaignCategory }}</span>
          <span class="deadline">마감일: {{ formatDate(campaign.campaignDeadline) }}</span>
        </div>
      </div>

      <form @submit.prevent="submitProposal" class="proposal-form">
        <div class="form-section">
          <h3>제안 내용</h3>
          <div class="form-group">
            <label for="contents">제안 내용</label>
            <textarea 
              id="contents" 
              v-model="formData.contents" 
              rows="10"
              placeholder="캠페인에 대한 구체적인 제안 내용을 작성해주세요."
              required
            ></textarea>
          </div>
        </div>

        <div class="button-group">
          <button type="button" class="cancel-button" @click="router.back()">취소</button>
          <button type="submit" class="submit-button" :disabled="loading">제안서 제출하기</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { campaignAPI } from '@/api/campaign'
import { proposalAPI } from '@/api/proposal'
import { useAccountStore } from '@/stores/account'

const route = useRoute()
const router = useRouter()
const accountStore = useAccountStore()
const campaign = ref(null)
const loading = ref(false)
const error = ref(null)

const formData = ref({
  contents: '', // 제안 내용
})

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}

onMounted(async () => {
  try {
    const campaignData = await campaignAPI.getCampaignDetail(route.params.id)
    campaign.value = campaignData
  } catch (err) {
    error.value = '캠페인 정보를 불러오는데 실패했습니다.'
    console.error(err)
  }
})

const submitProposal = async () => {
  try {
    loading.value = true
    error.value = null
    
    // 로그인 상태 확인
    if (!accountStore.isLoggedIn) {
      alert('제안서 작성은 로그인 후 이용 가능합니다.')
      router.push('/login')
      return
    }

    // 사용자 권한 확인
    console.log('현재 사용자 정보:', accountStore.user)
    console.log('사용자 권한:', accountStore.user?.userRole)
    
    if (accountStore.user?.userRole !== 'ROLE_INFLUENCER') {
      alert('제안서 작성은 인플루언서 회원만 가능합니다.')
      return
    }

    console.log('제안서 제출 시작:', {
      campaignId: route.params.id,
      contents: formData.value.contents
    })

    await proposalAPI.submitProposal(route.params.id, formData.value.contents)

    // 성공 알림
    alert('제안서가 성공적으로 제출되었습니다. 광고주의 승낙 후 채팅이 가능합니다.')

    // 캠페인 상세 페이지로 돌아가기
    router.push(`/campaign/${route.params.id}`)
  } catch (err) {
    console.error('제안서 제출 에러:', err)

    // 결제 미완료 에러 처리 (403 Forbidden) - 가장 먼저 확인
    if (err.response?.status === 403 && 
       (err.response?.data?.message?.includes('결제 완료한 사용자만') ||
        err.response?.data?.message?.includes('결제') && err.response?.data?.message?.includes('완료'))) {
      alert('제안서 제출은 구독 결제를 완료한 인플루언서만 이용할 수 있습니다.\n구독 신청 페이지로 이동합니다.')
      // 구독 신청 페이지로 이동
      router.push('/mypage/influencer?currentMenu=subscription.apply')
    }
    // 인증 에러인 경우 (일반적인 로그인 필요)
    else if (err.response?.status === 401 || 
             (err.response?.status === 403 && !err.response?.data?.message?.includes('결제'))) {
      error.value = '로그인이 필요합니다. 다시 로그인해주세요.'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } 
    // 중복 제출 에러인 경우 처리
    else if (err.response?.status === 500 && err.response?.data?.message === 'Server error') {
      // 500 에러이면서 'Server error' 메시지인 경우, 중복 제출일 가능성이 높음
      alert('이미 이 캠페인에 제출한 제안서가 있을 수 있습니다.\n마이페이지에서 제출한 제안서를 확인해주세요.\n\n※ 거절된 제안서가 있다면 해당 제안서 상세 페이지에서 재제출할 수 있습니다.')
      // 인플루언서 마이페이지의 제안서 관리 탭으로 이동
      router.push('/mypage/influencer?currentMenu=campaign.proposals')
    }
    // Entity not found 에러 처리 (캠페인이나 인플루언서를 찾을 수 없는 경우)
    else if ((err.response?.status === 404 || err.response?.status === 500) && 
             (err.response?.data?.message?.includes('Entity not found') ||
              err.response?.data?.message?.includes('찾을 수 없습니다') ||
              err.response?.data?.includes('Entity not found'))) {
      alert('캠페인 정보를 찾을 수 없습니다. 캠페인이 삭제되었거나 접근 권한이 없을 수 있습니다.')
      router.push('/campaigns')
    }
    // 중복 제출 에러 처리 (409 Conflict)
    else if (err.response?.status === 409 && 
             (err.response?.data?.message?.includes('이미') && err.response?.data?.message?.includes('제안서'))) {
      alert('이미 이 캠페인에 제출한 제안서가 있습니다.\n마이페이지에서 제출한 제안서를 확인할 수 있습니다.\n\n※ 거절된 제안서가 있다면 해당 제안서 상세 페이지에서 재제출할 수 있습니다.')
      // 인플루언서 마이페이지의 제안서 관리 탭으로 이동
      router.push('/mypage/influencer?currentMenu=campaign.proposals')
    }
    // 기타 에러
    else {
      // 백엔드에서 온 구체적인 에러 메시지가 있으면 표시, 없으면 기본 메시지
      const errorMessage = err.response?.data?.message || '제안서 제출 중 오류가 발생했습니다. 다시 시도해주세요.'
      error.value = errorMessage
    }
  } finally {
    loading.value = false
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
  padding: 20px 24px;
  border-radius: 8px;
  margin-bottom: 30px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.campaign-header {
  margin-bottom: 16px;
}

.campaign-info h2 {
  font-size: 1.5rem;
  margin: 0;
  font-weight: 700;
}

.campaign-details {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.category {
  background: #ede9fe;
  color: #7c3aed;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.95rem;
  font-weight: 500;
}

.deadline {
  color: #7c3aed;
  font-size: 0.95rem;
  font-weight: 500;
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

.button-group {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.cancel-button {
  flex: 1;
  padding: 15px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-button:hover {
  background-color: #e5e7eb;
}

.submit-button {
  flex: 2;
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

.submit-button:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.loading-state {
  text-align: center;
  padding: 40px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #7c3aed;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  color: #dc2626;
  padding: 20px;
  background: #fee2e2;
  border-radius: 8px;
  margin: 20px 0;
}

@media (max-width: 768px) {
  .proposal-page {
    padding: 20px 15px;
  }
  
  .form-section {
    padding: 20px;
  }
  
  .campaign-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .campaign-info h2 {
    font-size: 1.3rem;
  }
}
</style> 