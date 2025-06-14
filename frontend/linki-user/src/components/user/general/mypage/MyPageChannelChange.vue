<template>
  <div class="channel-change-container">
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

    <!-- 인플루언서 등록 폼 -->
    <div v-if="selectedTab === 'influencer'" class="form-container">
      <div class="form-group">
        <div class="form-row">
          <div class="input-group">

            <label>채널명 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="influencerData.channelName"
              placeholder="채널명을 입력해주세요"
              class="input-field"
              :disabled="isLoading"
            />
            <div v-if="errors.channelName" class="error-message">{{ errors.channelName }}</div>
          </div>
          <div class="input-group">
            <label>채널 URL <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="influencerData.channelUrl"
              placeholder="채널 URL을 입력해주세요"
              class="input-field"
              :disabled="isLoading"
            />
            <div v-if="errors.channelUrl" class="error-message">{{ errors.channelUrl }}</div>

          </div>
        </div>

        <div class="form-row">
          <div class="input-group">

            <label>소유자 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="influencerData.owner"
              placeholder="채널 소유자명을 입력해주세요"
              class="input-field"
              :disabled="isLoading"
            />
            <div v-if="errors.owner" class="error-message">{{ errors.owner }}</div>
          </div>
          <div class="input-group">
            <label>e-mail <span class="required">*</span></label>
            <input 
              type="email" 
              v-model="influencerData.email"
              placeholder="이메일을 입력해주세요"
              class="input-field"
              :disabled="isLoading"
            />
            <div v-if="errors.email" class="error-message">{{ errors.email }}</div>

          </div>
        </div>

        <div class="form-row-full">
          <div class="input-group">

            <label>계좌번호 <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="influencerData.accountNumber"
              placeholder="계좌번호를 입력해주세요 (- 없이 숫자만)"
              class="input-field"
              :disabled="isLoading"
            />
            <div v-if="errors.accountNumber" class="error-message">{{ errors.accountNumber }}</div>

          </div>
        </div>

        <div class="auth-section">

          <button class="auth-button" @click="handleAuth" :disabled="isLoading || isAuthenticated">
            {{ isAuthenticated ? '인증 완료' : '본인 인증' }}
          </button>
        </div>

        <div class="category-section">
          <div class="category-label">채널 카테고리 선택 <span class="required">*</span></div>
          <select 
            v-model="influencerData.category" 
            class="category-select"
            :disabled="isLoading"
          >
            <option value="">카테고리를 선택해주세요</option>
            <option v-for="category in categories" :key="category.value" :value="category.value">
              {{ category.label }}
            </option>
          </select>
          <div v-if="errors.category" class="error-message">{{ errors.category }}</div>
        </div>

        <div class="button-group">
          <button class="cancel-button" @click="handleCancel" :disabled="isLoading">취소</button>
          <button 
            class="submit-button" 
            @click="handleInfluencerSubmit" 
            :disabled="isLoading || !isAuthenticated || !isInfluencerFormValid"
          >
            {{ isLoading ? '처리 중...' : '등록 신청' }}
          </button>

        </div>
      </div>
    </div>

    <!-- 광고주 등록 폼 -->
    <div v-if="selectedTab === 'advertiser'" class="form-container">
      <div class="form-group">
        <div class="form-row-full">
          <div class="input-group">

            <label>사업자 번호 <span class="required">*</span></label>

            <input 
              type="text" 
              v-model="advertiserData.businessNumber"
              placeholder="000-00-00000"
              class="input-field"

              :disabled="isLoading"
            />
            <div v-if="errors.businessNumber" class="error-message">{{ errors.businessNumber }}</div>
          </div>
        </div>

        <div class="form-row-full">
          <div class="input-group">

            <label>사업자등록증 <span class="required">*</span></label>
            <div class="file-upload-container">
              <input 
                type="file" 
                ref="fileInput"
                @change="handleFileChange"
                accept=".pdf,.jpg,.jpeg,.png"
                :disabled="isLoading"
                class="file-input"
              />
              <button class="file-upload-button" @click="triggerFileInput" :disabled="isLoading">
                파일 선택
              </button>
              <span class="file-name">{{ selectedFileName || '선택된 파일 없음' }}</span>
            </div>
            <div v-if="errors.businessCert" class="error-message">{{ errors.businessCert }}</div>

          </div>
        </div>

        <div class="button-group">

          <button class="cancel-button" @click="handleCancel" :disabled="isLoading">취소</button>
          <button 
            class="submit-button" 
            @click="handleAdvertiserSubmit" 
            :disabled="isLoading || !isAdvertiserFormValid"
          >
            {{ isLoading ? '처리 중...' : '등록 신청' }}
          </button>

        </div>
      </div>
    </div>
  </div>
</template>

<script>

import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useAlert } from '@/composables/alert'


export default {
  name: 'MyPageChannelChange',
  setup() {

    const { showAlert } = useAlert()
    const selectedTab = ref('influencer')
    const isLoading = ref(false)
    const isAuthenticated = ref(false)
    const fileInput = ref(null)
    const selectedFileName = ref('')
    const errors = ref({})
    
    const categories = [
      { value: '01', label: '뷰티' },
      { value: '02', label: '패션' },
      { value: '03', label: '푸드' },
      { value: '04', label: '여행' },
      { value: '05', label: '게임' },
      { value: '06', label: '음악' },
      { value: '07', label: '스포츠' },
      { value: '08', label: '교육' },
      { value: '09', label: '테크' },
      { value: '10', label: '기타' }
    ]

    
    const influencerData = ref({
      channelName: '',
      channelUrl: '',
      owner: '',
      email: '',
      accountNumber: '',

      category: ''

    })

    const advertiserData = ref({
      businessNumber: '',

      businessCert: null
    })

    const isInfluencerFormValid = computed(() => {
      return (
        influencerData.value.channelName &&
        influencerData.value.channelUrl &&
        influencerData.value.owner &&
        influencerData.value.email &&
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(influencerData.value.email) &&
        influencerData.value.accountNumber &&
        influencerData.value.category &&
        isAuthenticated.value
      )
    })

    const isAdvertiserFormValid = computed(() => {
      return (
        advertiserData.value.businessNumber &&
        /^\d{3}-\d{2}-\d{5}$/.test(advertiserData.value.businessNumber) &&
        advertiserData.value.businessCert
      )
    })

    const validateInfluencerForm = () => {
      errors.value = {}
      
      if (!influencerData.value.channelName) {
        errors.value.channelName = '채널명을 입력해주세요.'
      }
      if (!influencerData.value.channelUrl) {
        errors.value.channelUrl = '채널 URL을 입력해주세요.'
      }
      if (!influencerData.value.owner) {
        errors.value.owner = '소유자명을 입력해주세요.'
      }
      if (!influencerData.value.email) {
        errors.value.email = '이메일을 입력해주세요.'
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(influencerData.value.email)) {
        errors.value.email = '유효한 이메일 주소를 입력해주세요.'
      }
      if (!influencerData.value.accountNumber) {
        errors.value.accountNumber = '계좌번호를 입력해주세요.'
      } else if (!/^\d+$/.test(influencerData.value.accountNumber)) {
        errors.value.accountNumber = '계좌번호는 숫자만 입력해주세요.'
      }
      if (!influencerData.value.category) {
        errors.value.category = '카테고리를 선택해주세요.'
      }
      if (!isAuthenticated.value) {
        showAlert('본인 인증이 필요합니다.', 'error')
        return false
      }

      return Object.keys(errors.value).length === 0
    }

    const validateAdvertiserForm = () => {
      errors.value = {}

      if (!advertiserData.value.businessNumber) {
        errors.value.businessNumber = '사업자 번호를 입력해주세요.'
      } else if (!/^\d{3}-\d{2}-\d{5}$/.test(advertiserData.value.businessNumber)) {
        errors.value.businessNumber = '올바른 사업자 번호 형식을 입력해주세요. (000-00-00000)'
      }
      if (!advertiserData.value.businessCert) {
        errors.value.businessCert = '사업자등록증을 업로드해주세요.'
      }

      return Object.keys(errors.value).length === 0
    }

    const fetchData = async () => {
      try {
        isLoading.value = true
        const response = await axios.get('/api/user/channel-info')
        
        if (response.data.success) {
          const data = response.data.data
          if (data.type === 'influencer') {
            influencerData.value = {
              channelName: data.channelName || '',
              channelUrl: data.channelUrl || '',
              owner: data.owner || '',
              email: data.email || '',
              accountNumber: data.accountNumber || '',
              category: data.category || ''
            }
            isAuthenticated.value = data.isAuthenticated || false
            selectedTab.value = 'influencer'
          } else if (data.type === 'advertiser') {
            advertiserData.value = {
              businessNumber: data.businessNumber || '',
              businessCert: null
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

    const handleInfluencerSubmit = async () => {

      if (!validateInfluencerForm()) return

      try {
        isLoading.value = true
        const response = await axios.post('/api/user/channel/influencer', influencerData.value)
        
        if (response.data.success) {
          showAlert('인플루언서 등록이 완료되었습니다.', 'success')
          // 필요한 경우 리다이렉션 또는 추가 처리
        } else {
          showAlert(response.data.message || '등록에 실패했습니다.', 'error')
        }
      } catch (error) {
        console.error('등록 실패:', error)
        const errorMessage = error.response?.data?.message || '등록 처리 중 오류가 발생했습니다.'
        showAlert(errorMessage, 'error')
      } finally {
        isLoading.value = false

      }
    }

    const handleAdvertiserSubmit = async () => {

      if (!validateAdvertiserForm()) return

      try {
        isLoading.value = true
        const formData = new FormData()
        formData.append('businessNumber', advertiserData.value.businessNumber)
        formData.append('businessCert', advertiserData.value.businessCert)

        const response = await axios.post('/api/user/channel/advertiser', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.success) {
          showAlert('광고주 등록이 완료되었습니다.', 'success')
          // 필요한 경우 리다이렉션 또는 추가 처리
        } else {
          showAlert(response.data.message || '등록에 실패했습니다.', 'error')
        }
      } catch (error) {
        console.error('등록 실패:', error)
        const errorMessage = error.response?.data?.message || '등록 처리 중 오류가 발생했습니다.'
        showAlert(errorMessage, 'error')
      } finally {
        isLoading.value = false

      }
    }

    const handleCancel = () => {

      errors.value = {}
      fetchData()
    }

    const handleAuth = async () => {
      try {
        isLoading.value = true
        const response = await axios.post('/api/user/auth/verify')
        
        if (response.data.success) {
          isAuthenticated.value = true
          showAlert('본인 인증이 완료되었습니다.', 'success')
        } else {
          showAlert(response.data.message || '본인 인증에 실패했습니다.', 'error')
        }
      } catch (error) {
        console.error('본인 인증 실패:', error)
        showAlert('본인 인증 처리 중 오류가 발생했습니다.', 'error')
      } finally {
        isLoading.value = false
      }
    }

    const handleFileChange = (event) => {
      const file = event.target.files[0]
      if (file) {
        if (file.size > 5 * 1024 * 1024) { // 5MB 제한
          showAlert('파일 크기는 5MB를 초과할 수 없습니다.', 'error')
          event.target.value = ''
          return
        }
        advertiserData.value.businessCert = file
        selectedFileName.value = file.name
      }
    }

    const triggerFileInput = () => {
      fileInput.value.click()

    }

    onMounted(() => {
      fetchData()
    })

    return {
      selectedTab,
      influencerData,
      advertiserData,

      isLoading,
      isAuthenticated,
      errors,
      categories,
      fileInput,
      selectedFileName,
      isInfluencerFormValid,
      isAdvertiserFormValid,
      handleInfluencerSubmit,
      handleAdvertiserSubmit,
      handleCancel,
      handleAuth,
      handleFileChange,
      triggerFileInput

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
  justify-content: center;
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

  padding: 0 12px;
  font-size: 16px;
  background-color: white;

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


.required {
  color: #ff4d4f;
  margin-left: 4px;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

.file-upload-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-input {
  display: none;
}

.file-upload-button {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.file-name {
  color: #666;
  font-size: 14px;
}

.tab-button:disabled,
.submit-button:disabled,
.cancel-button:disabled,
.auth-button:disabled,
.file-upload-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.input-field:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

</style> 