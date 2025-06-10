<template>
  <div :class="$style.myProfileParent">
    <div :class="$style.myProfile">My Profile</div>
    <div :class="$style.frameParent">
      <div :class="$style.parent">
        <div :class="$style.div">이름</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox" />
          <div :class="$style.md">Md</div>
        </div>
      </div>
      <div :class="$style.parent">
        <div :class="$style.div">연락처</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox" />
          <div :class="$style.md">Rimel</div>
        </div>
      </div>
    </div>
    <div :class="$style.frameGroup">
      <div :class="$style.parent">
        <div :class="$style.div">Email</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox" />
          <div :class="$style.md">rimel1111@gmail.com</div>
        </div>
      </div>
      <div :class="$style.parent">
        <div :class="$style.div">가입일</div>
        <div :class="$style.placeboxInfo">
          <div :class="$style.placeToInfoBox" />
          <div :class="$style.md">Kingston, 5236, United State</div>
        </div>
      </div>
    </div>
    <div :class="$style.cancelParent">
      <div :class="$style.div">Cancel</div>
      <div :class="$style.button">
        <div :class="$style.viewAllProducts">Save Changes</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)
const showPasswordModal = ref(false)
const passwordLoading = ref(false)

const profileData = ref({
  name: '',
  phone: '',
  email: '',
  joinDate: null
})

const passwordData = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordError = ref('')
const confirmError = ref('')

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const fetchProfile = async () => {
  try {
    const response = await axios.get('/api/influencer/profile')
    profileData.value = {
      ...response.data,
      joinDate: new Date(response.data.joinDate)
    }
  } catch (error) {
    console.error('프로필 정보 로딩 실패:', error)
    alert('프로필 정보를 불러오는데 실패했습니다.')
  }
}

const handleSubmit = async () => {
  isLoading.value = true
  try {
    await axios.put('/api/influencer/profile', {
      name: profileData.value.name,
      phone: profileData.value.phone,
      email: profileData.value.email
    })
    
    alert('프로필이 성공적으로 업데이트되었습니다.')
  } catch (error) {
    console.error('프로필 업데이트 실패:', error)
    alert('프로필 업데이트에 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

const validatePassword = () => {
  passwordError.value = ''
  confirmError.value = ''

  if (passwordData.value.newPassword.length < 8) {
    passwordError.value = '비밀번호는 8자 이상이어야 합니다.'
    return false
  }

  if (passwordData.value.newPassword !== passwordData.value.confirmPassword) {
    confirmError.value = '비밀번호가 일치하지 않습니다.'
    return false
  }

  return true
}

const handlePasswordChange = async () => {
  if (!validatePassword()) return

  passwordLoading.value = true
  try {
    await axios.post('/api/influencer/change-password', {
      currentPassword: passwordData.value.currentPassword,
      newPassword: passwordData.value.newPassword
    })
    
    alert('비밀번호가 성공적으로 변경되었습니다.')
    showPasswordModal.value = false
    passwordData.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    if (error.response?.status === 401) {
      alert('현재 비밀번호가 올바르지 않습니다.')
    } else {
      alert('비밀번호 변경 중 오류가 발생했습니다.')
    }
  } finally {
    passwordLoading.value = false
  }
}

const handleCancel = () => {
  router.push('/influencer/mypage')
}

onMounted(() => {
  fetchProfile()
})
</script>

<style module>
.myProfile {
  position: absolute;
  top: 40px;
  left: 80px;
  font-size: 20px;
  line-height: 28px;
  font-weight: 500;
}

.div {
  position: relative;
  line-height: 24px;
  background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), #000;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.placeToInfoBox {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0%;
  right: 0%;
  bottom: 0%;
  left: 0%;
  border-radius: 4px;
  background-color: rgba(245, 245, 245, 0.7);
  overflow: hidden;
}

.md {
  position: absolute;
  top: 13px;
  left: 16px;
  line-height: 24px;
  background: linear-gradient(rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.2)), #000;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  opacity: 0.5;
}

.placeboxInfo {
  width: 330px;
  position: relative;
  height: 50px;
}

.parent {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 8px;
}

.frameParent {
  position: absolute;
  top: 84px;
  left: 80px;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 50px;
}

.frameGroup {
  position: absolute;
  top: 190px;
  left: 80px;
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 50px;
}

.viewAllProducts {
  position: relative;
  line-height: 24px;
  font-weight: 500;
}

.button {
  border-radius: 4px;
  background-color: #d6bcf7;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 16px 48px;
}

.cancelParent {
  position: absolute;
  top: calc(50% + 219px);
  right: 80px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  gap: 32px;
  color: #fafafa;
}

.myProfileParent {
  width: 100%;
  position: relative;
  box-shadow: 0px 1px 13px rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  background-color: #fff;
  height: 630px;
  overflow: hidden;
  text-align: left;
  font-size: 16px;
  color: #7b21e8;
  font-family: Poppins;
}
</style>