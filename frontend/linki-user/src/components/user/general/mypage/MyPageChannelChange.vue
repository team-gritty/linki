<template>
  <div class="channel-change-container">
    <div class="tab-buttons">
      <button 
        :class="['tab-button', { active: selectedTab === 'influencer' }]"
        @click="selectedTab = 'influencer'"
      >
        인플루언서 등록
      </button>
      <button 
        :class="['tab-button', { active: selectedTab === 'advertiser' }]"
        @click="selectedTab = 'advertiser'"
      >
        광고주 등록
      </button>
    </div>

    <!-- 인플루언서 등록 폼 -->
    <div v-if="selectedTab === 'influencer'" class="form-container">
      <div class="form-group">
        <div class="form-row">
          <div class="input-group">
            <label>채널명</label>
            <input 
              type="text" 
              v-model="influencerData.channelName"
              placeholder="Md"
              class="input-field"
            />
          </div>
          <div class="input-group">
            <label>채널 URL</label>
            <input 
              type="text" 
              v-model="influencerData.channelUrl"
              placeholder="Rimel"
              class="input-field"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="input-group">
            <label>소유자</label>
            <input 
              type="text" 
              v-model="influencerData.owner"
              placeholder="rimel1111@gmail.com"
              class="input-field"
            />
          </div>
          <div class="input-group">
            <label>e-mail</label>
            <input 
              type="email" 
              v-model="influencerData.email"
              placeholder="Kingston, 5236, United State"
              class="input-field"
            />
          </div>
        </div>

        <div class="form-row-full">
          <div class="input-group">
            <label>계좌번호</label>
            <input 
              type="text" 
              v-model="influencerData.accountNumber"
              placeholder="Kingston, 5236, United State"
              class="input-field"
            />
          </div>
        </div>

        <div class="auth-section">
          <button class="auth-button" @click="handleAuth">본인 인증</button>
        </div>

        <div class="category-section">
          <div class="category-label">채널 카테고리 선택</div>
          <div class="category-select">
            <span class="category-value">{{ influencerData.category }}</span>
            <div class="category-arrows">
              <span class="arrow up">▲</span>
              <span class="arrow down">▼</span>
            </div>
          </div>
        </div>

        <div class="button-group">
          <button class="cancel-button" @click="handleCancel">Cancel</button>
          <button class="submit-button" @click="handleInfluencerSubmit">등록 신청</button>
        </div>
      </div>
    </div>

    <!-- 광고주 등록 폼 -->
    <div v-if="selectedTab === 'advertiser'" class="form-container">
      <div class="form-group">
        <div class="form-row-full">
          <div class="input-group">
            <label>사업자 번호</label>
            <input 
              type="text" 
              v-model="advertiserData.businessNumber"
              placeholder="000-00-00000"
              class="input-field"
            />
          </div>
        </div>

        <div class="form-row-full">
          <div class="input-group">
            <label>사업자등록증</label>
            <input 
              type="text" 
              v-model="advertiserData.businessCert"
              placeholder="파일업로드"
              class="input-field"
            />
          </div>
        </div>

        <div class="button-group">
          <button class="cancel-button" @click="handleCancel">Cancel</button>
          <button class="submit-button" @click="handleAdvertiserSubmit">등록 신청</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'MyPageChannelChange',
  setup() {
    const selectedTab = ref('influencer')
    
    const influencerData = ref({
      channelName: '',
      channelUrl: '',
      owner: '',
      email: '',
      accountNumber: '',
      category: '01'
    })

    const advertiserData = ref({
      businessNumber: '',
      businessCert: ''
    })

    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost:3000/users/1')
        if (response.data.type === 'influencer') {
          influencerData.value = {
            channelName: response.data.channelName || '',
            channelUrl: response.data.channelUrl || '',
            owner: response.data.owner || '',
            email: response.data.email || '',
            accountNumber: response.data.accountNumber || '',
            category: response.data.category || '01'
          }
          selectedTab.value = 'influencer'
        } else if (response.data.type === 'advertiser') {
          advertiserData.value = {
            businessNumber: response.data.businessNumber || '',
            businessCert: response.data.businessCert || ''
          }
          selectedTab.value = 'advertiser'
        }
      } catch (error) {
        console.error('데이터 로딩 실패:', error)
      }
    }

    const handleInfluencerSubmit = async () => {
      try {
        await axios.patch('http://localhost:3000/users/1', {
          type: 'influencer',
          ...influencerData.value
        })
        alert('인플루언서 등록이 완료되었습니다.')
      } catch (error) {
        console.error('등록 실패:', error)
        alert('등록에 실패했습니다.')
      }
    }

    const handleAdvertiserSubmit = async () => {
      try {
        await axios.patch('http://localhost:3000/users/1', {
          type: 'advertiser',
          ...advertiserData.value
        })
        alert('광고주 등록이 완료되었습니다.')
      } catch (error) {
        console.error('등록 실패:', error)
        alert('등록에 실패했습니다.')
      }
    }

    const handleCancel = () => {
      fetchData()
    }

    const handleAuth = () => {
      alert('본인 인증이 요청되었습니다.')
    }

    onMounted(() => {
      fetchData()
    })

    return {
      selectedTab,
      influencerData,
      advertiserData,
      handleInfluencerSubmit,
      handleAdvertiserSubmit,
      handleCancel,
      handleAuth
    }
  }
}
</script>

<style scoped>
.channel-change-container {
  padding: 40px;
  background: white;
  border-radius: 4px;
}

.tab-buttons {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
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

.form-group {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.form-row {
  display: flex;
  gap: 50px;
}

.form-row-full {
  width: 100%;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.input-group label {
  font-size: 16px;
  color: #000;
  opacity: 0.8;
}

.input-field {
  height: 50px;
  padding: 0 16px;
  border: none;
  border-radius: 4px;
  background-color: rgba(245, 245, 245, 0.7);
  font-size: 16px;
}

.input-field::placeholder {
  color: #000;
  opacity: 0.5;
}

.auth-section {
  display: flex;
  justify-content: flex-start;
}

.auth-button {
  padding: 16px 48px;
  background-color: #7b21e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.category-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.category-label {
  font-size: 16px;
  color: #000;
  opacity: 0.8;
}

.category-select {
  width: 200px;
  height: 47px;
  border: 1.5px solid rgba(0, 0, 0, 0.4);
  border-radius: 4px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  justify-content: space-between;
}

.category-value {
  font-size: 16px;
}

.category-arrows {
  display: flex;
  flex-direction: column;
}

.arrow {
  font-size: 12px;
  cursor: pointer;
  color: #666;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 32px;
  margin-top: 20px;
}

.cancel-button {
  font-size: 16px;
  color: #000;
  opacity: 0.8;
  background: none;
  border: none;
  cursor: pointer;
}

.submit-button {
  padding: 16px 48px;
  background-color: #7b21e8;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.submit-button:hover,
.auth-button:hover {
  background-color: #6618c4;
}
</style> 