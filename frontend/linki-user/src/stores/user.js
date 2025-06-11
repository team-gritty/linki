import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref({
    id: null,
    email: '',
    nickname: '',
    name: '',
    phone: '',
    type: '',
    profileImage: '',
    businessRegistrationNumber: '',
    businessRegistrationImage: '',
    isInfluencer: false,
    isAdvertiser: false
  })

  const setUser = (userData) => {
    user.value = {
      ...user.value,
      ...userData
    }
  }

  const clearUser = () => {
    user.value = {
      id: null,
      email: '',
      nickname: '',
      name: '',
      phone: '',
      type: '',
      profileImage: '',
      businessRegistrationNumber: '',
      businessRegistrationImage: '',
      isInfluencer: false,
      isAdvertiser: false
    }
  }

  const updateUserProfile = (profileData) => {
    user.value = {
      ...user.value,
      ...profileData
    }
  }

  return {
    user,
    setUser,
    clearUser,
    updateUserProfile
  }
}) 