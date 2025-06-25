<template>
  <div class="channel-change-container">
    <h2 class="channel-change-title">채널 변경</h2>
    
    <div class="tab-buttons">
      <button 
        :class="['tab-button', { active: selectedTab === 'influencer' }]"
        @click="selectedTab = 'influencer'"
        :disabled="isLoading"
      >
        인플루언서 등록
      </button>
      <button 
        :class="['tab-button', { active: selectedTab === 'advertiser' }]"
        @click="selectedTab = 'advertiser'"
        :disabled="isLoading"
      >
        광고주 등록
      </button>
    </div>

    <div v-show="selectedTab === 'influencer'" class="form-container">
      <div class="auth-section">
        <button class="auth-button" @click="initiateYoutubeAuth" :disabled="isLoading">
          유튜브 인증
        </button>
      </div>
      <div v-if="influencerData.thumbnail" class="info-group">
        <label>프로필 이미지</label>
        <img :src="influencerData.thumbnail" alt="프로필 이미지" style="width: 100px; height: 100px; object-fit: cover; border-radius: 50%;" />
      </div>
      <div class="info-group">
        <label>채널명</label>
        <span>{{ influencerData.channelName || '없음' }}</span>
      </div>
      <div class="info-group">
        <label>채널 ID</label>
        <span>{{ influencerData.channelId || '없음' }}</span>
      </div>
      <div class="info-group">
        <label>채널 URL</label>
        <span>{{ influencerData.channelUrl || '없음' }}</span>
      </div>
<!--      <div class="info-group">-->
<!--        <label>이름</label>-->
<!--        <span>{{ influencerData.name || '없음' }}</span>-->
<!--      </div>-->
<!--      <div class="info-group">-->
<!--        <label>이메일</label>-->
<!--        <span>{{ influencerData.email || '없음' }}</span>-->
<!--      </div>-->
<!--      <div class="info-group">-->
<!--        <label>생성 날짜</label>-->
<!--        <span>{{ influencerData.creationDate || '없음' }}</span>-->
<!--      </div>-->
      <div class="info-group">
        <label>설명</label>
        <span>{{ influencerData.description || '없음' }}</span>
      </div>
      <div class="info-group">
        <label>생성일</label>
        <span>{{ influencerData.publishedAt || '없음' }}</span>
      </div>
      <div class="info-group">
        <label>사용자 지정 URL</label>
        <span>{{ influencerData.customUrl || '없음' }}</span>
      </div>
      <div class="info-group">
        <label>국가</label>
        <span>{{ influencerData.country || '없음' }}</span>
      </div>
      <div class="info-group">
        <label for="category">카테고리</label>
        <select 
          id="category" 
          v-model="influencerData.category" 
          class="category-select"
          :disabled="isLoading"
        >
          <option value="">카테고리를 선택하세요</option>
          <option value="패션">패션</option>
          <option value="뷰티">뷰티</option>
          <option value="푸드/먹방">푸드/먹방</option>
          <option value="엔터테이먼트">엔터테이먼트</option>
          <option value="여행">여행</option>
          <option value="스포츠">스포츠</option>
          <option value="전자기기">전자기기</option>
          <option value="Vlog/라이프스타일">Vlog/라이프스타일</option>
          <option value="교육">교육</option>
          <option value="동물/펫">동물/펫</option>
        </select>
      </div>
      <div class="button-group" v-if="influencerData.channelName">
        <button class="submit-button" @click="handleRegistration" :disabled="isLoading">
          등록
        </button>
      </div>

    </div>

    <div v-if="selectedTab === 'advertiser'" class="form-container">
      <div class="info-group">
        <label for="businessNumber">사업자 번호</label>
        <input
          id="businessNumber"
          type="text"
          v-model="advertiserData.businessNumber"
          placeholder="사업자 번호를 입력하세요"
        />
      </div>
      <div class="info-group">
        <label for="businessCert">사업자등록증</label>
        <input
          id="businessCert"
          type="file"
          @change="handleFileUpload"
        />
      </div>
      <div class="button-group">
        <button class="submit-button" @click="handleRegistration" :disabled="isLoading">
          등록
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import httpClient from '@/utils/httpRequest'
import { useAlert } from '@/composables/alert'
import { useRoute } from 'vue-router'

const { showAlert } = useAlert()
const selectedTab = ref('influencer')
const isLoading = ref(false)
const selectedFileName = ref('')
const route = useRoute()

const influencerData = ref({
  channelId: '',
  channelName: '',
  channelUrl: '',
  name: '',
  email: '',
  creationDate: '',
  description: '',
  publishedAt: '',
  customUrl: '',
  country: '',
  thumbnail: '',
  category: ''
})

const advertiserData = ref({
  businessNumber: ''
})

const fetchData = async () => {
  try {
    isLoading.value = true

    // URL의 code 파라미터와 라우터 쿼리의 code 파라미터 모두 확인
    const urlCode = new URLSearchParams(window.location.search).get('code')
    const routeCode = route.query.code
    const code = urlCode || routeCode

    if (!code) {
      console.warn('code 파라미터가 없습니다.')
      return
    }

    const response = await httpClient.get(`v1/api/user/youtube/callback?code=${code}`)
    if (response.data.success) {
      const data = response.data.data
      console.log('받은 채널 데이터:', data)
      console.log('채널 ID:', data.channelId)
      
      // If backend always returns influencer channel data now, update accordingly:
      if (selectedTab.value === 'influencer') {
        influencerData.value = {
          channelId: data.channelId || '',
          channelName: data.title || '',
          channelUrl: data.channelId ? `https://www.youtube.com/channel/${data.channelId}` : '',
          name: '', // You can fill this in later if you retrieve it
          email: '', // Likewise for email
          creationDate: '', // Likewise
          description: data.description || '',
          publishedAt: data.publishedAt || '',
          customUrl: data.customUrl || '',
          country: data.country || '',
          thumbnail: data.thumbnail || '',
          category: data.category || ''
        };
        selectedTab.value = 'influencer'
      } else if (selectedTab.value === 'advertiser') {
        advertiserData.value = {
          businessNumber: data.businessNumber || ''
        }
        selectedFileName.value = data.businessCertName || ''
        selectedTab.value = 'advertiser'
      }
    }
  } catch (error) {
    console.error('데이터 로딩 실패:', error)
    showAlert('데이터를 불러오는데 실패했습니다.', 'error')
  } finally {
    isLoading.value = false
  }
}

const initiateYoutubeAuth = () => {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  const redirectUri = 'http://localhost:3002/google-callback';
  const scope = 'https://www.googleapis.com/auth/youtube.readonly';
  const authUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&response_type=code&access_type=offline`;
  window.location.href = authUrl;
}

const handleRegistration = async () => {
  if (selectedTab.value === 'influencer') {
    if (!influencerData.value.category) {
      showAlert('카테고리를 선택해주세요.', 'error')
      return
    }
    if (!influencerData.value.channelName) {
      showAlert('유튜브 인증을 먼저 진행해주세요.', 'error')
      return
    }

    const payload = {
      id: influencerData.value.channelId,
      channelId: influencerData.value.channelId,
      snippet: {
        title: influencerData.value.channelName,
        thumbnails: {
          default: {
            url: influencerData.value.thumbnail
          }
        },
        description: influencerData.value.description,
        publishedAt: influencerData.value.publishedAt,
        customUrl: influencerData.value.customUrl,
        country: influencerData.value.country,
        category: influencerData.value.category
      }
    }

    try {
      await httpClient.post('v1/api/user/youtube/register', payload)
      showAlert('등록이 완료되었습니다.', 'success')
    } catch (error) {
      console.error('채널 등록 실패:', error)
      showAlert('채널 등록 중 오류가 발생했습니다.', 'error')
    }
  }
}

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFileName.value = file.name;
    // 파일 자체를 저장하려면 FormData 등에 append 해서 사용
  }
};

onMounted(() => {
  fetchData()
})

</script>

<style scoped>
.channel-change-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.channel-change-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 2rem;
  color: #333;
  text-align: center;
}

.tab-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 20px;
}

.tab-button {
  padding: 12px 24px;
  font-size: 18px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.3s ease;
}

.tab-button.active {
  color: #7b21e8;
  font-weight: 600;
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #7b21e8;
}

.form-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.info-group {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  color: #333;
  display: flex;
  flex-direction: column;
}

.info-group label {
  font-weight: 600;
  color: #555;
  margin-bottom: 0.5rem;
}

.info-group input {
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.auth-section {
  display: flex;
  justify-content: flex-start;
  margin-top: -20px;
}

.auth-button {
  padding: 10px 20px;
  background-color: #7b21e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.auth-button:hover {
  background-color: #6618c4;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.submit-button {
  padding: 10px 20px;
  background-color: #7b21e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #6618c4;
}

.tab-button:disabled,
.auth-button:disabled,
.submit-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.category-select {
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.category-select:focus {
  outline: none;
  border-color: #7b21e8;
}

.category-select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}
</style>