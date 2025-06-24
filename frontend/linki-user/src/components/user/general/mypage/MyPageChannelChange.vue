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

    <div v-if="selectedTab === 'influencer'" class="form-container">
      <div class="auth-section">
        <button class="auth-button" @click="initiateYoutubeAuth" :disabled="isLoading">
          유튜브 인증
        </button>
      </div>
      <div class="info-group">
        <label>채널명</label>
        <input type="text" :value="influencerData.channelName" readonly />
      </div>
      <div class="info-group">
        <label>채널 URL</label>
        <input type="text" :value="influencerData.channelUrl" readonly />
      </div>
      <div class="info-group">
        <label>이름</label>
        <input type="text" :value="influencerData.name" readonly />
      </div>
      <div class="info-group">
        <label>이메일</label>
        <input type="text" :value="influencerData.email" readonly />
      </div>
      <div class="info-group">
        <label>생성 날짜</label>
        <input type="text" :value="influencerData.creationDate" readonly />
      </div>
      <div class="info-group">
        <label>설명</label>
        <input type="text" :value="influencerData.description" readonly />
      </div>
      <div class="info-group">
        <label>생성일</label>
        <input type="text" :value="influencerData.publishedAt" readonly />
      </div>
      <div class="info-group">
        <label>사용자 지정 URL</label>
        <input type="text" :value="influencerData.customUrl" readonly />
      </div>
      <div class="info-group">
        <label>국가</label>
        <input type="text" :value="influencerData.country" readonly />
      </div>
      <div class="button-group" v-if="influencerData.channelName">
        <button class="submit-button" @click="handleRegistration" :disabled="isLoading">
          등록
        </button>
      </div>
      <div class="button-group">
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
  channelName: '',
  channelUrl: '',
  name: '',
  email: '',
  creationDate: '',
  description: '',
  publishedAt: '',
  customUrl: '',
  country: ''
})

const advertiserData = ref({
  businessNumber: ''
})

const fetchData = async () => {
  try {
    isLoading.value = true

    const code = new URLSearchParams(window.location.search).get('code')
    if (!code) {
      console.warn('code 파라미터가 없습니다.')
      return
    }

    const response = await httpClient.get(`v1/api/user/youtubecallback?code=${code}`)
    if (response.data.success) {
      const data = response.data.data
      if (data.type === 'influencer') {
        influencerData.value = {
          channelName: data.channelName || '',
          channelUrl: data.channelUrl || '',
          name: data.name || '',
          email: data.email || '',
          creationDate: data.creationDate || '',
          description: data.description || '',
          publishedAt: data.publishedAt || '',
          customUrl: data.customUrl || '',
          country: data.country || ''
        }
        selectedTab.value = 'influencer'
      } else if (data.type === 'advertiser') {
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

const handleRegistration = () => {
  showAlert('등록이 완료되었습니다.', 'success');
  // Add registration logic here if needed
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
}

.channel-change-title {
  font-size: 1.5rem;
  font-weight: 600;
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
}

.info-group {
  margin-bottom: 1rem;
  font-size: 1.2rem; /* increased from 1rem */
  color: #333;
}

.info-group input {
  font-size: 1.1rem;
  padding: 10px 12px;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 6px;
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
</style>