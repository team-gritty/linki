<!-- <script setup>
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const userType = userStore.userType

onMounted(() => {
  if (userType === 'influencer') {
    router.replace('/mypage/influencer')
  } else if (userType === 'advertiser') {
    router.replace('/mypage/advertiser')
  } else {
    router.replace('/login')
  }
})
</script>

<template>
  <div>Redirecting...</div>
</template>

<style scoped>

</style> -->