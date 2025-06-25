import { defineStore } from 'pinia'
import { useAccountStore } from './account'

const MAX_ACCESS_COUNT = 5

export const useChannelAccessStore = defineStore('channelAccess', {
  persist: true,
  
  state: () => ({
    // 사용자별 채널 상세 조회 횟수를 저장
    // { userId: { count: number, lastAccess: timestamp } }
    accessData: {}
  }),

  getters: {
    /**
     * 현재 사용자의 채널 상세 조회 횟수를 반환
     */
    getCurrentUserAccessCount: (state) => {
      const accountStore = useAccountStore()
      const userId = accountStore.user?.userId
      
      if (!userId) return 0
      
      return state.accessData[userId]?.count || 0
    },

    /**
     * 현재 사용자가 일반회원(ROLE_USER)인지 확인
     */
    isRegularUser: () => {
      const accountStore = useAccountStore()
      return accountStore.user?.userRole === 'ROLE_USER'
    },

    /**
     * 현재 사용자가 채널 상세 페이지에 접근 가능한지 확인
     */
    canAccessChannelDetail: (state) => {
      const accountStore = useAccountStore()
      
      // 로그인하지 않은 경우 접근 불가
      if (!accountStore.isLoggedIn) {
        return { 
          canAccess: false, 
          reason: 'NOT_LOGGED_IN',
          message: '로그인이 필요합니다.' 
        }
      }

      // 일반회원이 아닌 경우 제한 없음
      if (!useChannelAccessStore().isRegularUser) {
        return { 
          canAccess: true, 
          reason: 'UNLIMITED_ACCESS',
          remainingCount: -1 
        }
      }

      // 일반회원인 경우 5회 제한 체크
      const currentCount = useChannelAccessStore().getCurrentUserAccessCount
      const canAccess = currentCount < MAX_ACCESS_COUNT
      const remainingCount = Math.max(0, MAX_ACCESS_COUNT - currentCount)

      return {
        canAccess,
        reason: canAccess ? 'ACCESS_ALLOWED' : 'LIMIT_EXCEEDED',
        currentCount,
        remainingCount,
        maxCount: MAX_ACCESS_COUNT,
        message: canAccess 
          ? `채널 상세 분석을 ${remainingCount}회 더 이용하실 수 있습니다.`
          : '일반회원은 채널 상세 분석을 5회까지만 이용할 수 있습니다.'
      }
    }
  },

  actions: {
    /**
     * 현재 사용자의 채널 상세 조회 횟수를 증가
     */
    incrementAccessCount() {
      const accountStore = useAccountStore()
      const userId = accountStore.user?.userId

      if (!userId) {
        console.warn('사용자 ID가 없어 접근 횟수를 증가시킬 수 없습니다.')
        return false
      }

      // 현재 데이터 가져오기
      const currentData = this.accessData[userId] || { count: 0, lastAccess: null }
      
      // 횟수 증가
      this.accessData[userId] = {
        count: currentData.count + 1,
        lastAccess: new Date().toISOString()
      }

      console.log(`사용자 ${userId}의 채널 조회 횟수 증가: ${currentData.count} → ${this.accessData[userId].count}`)
      return true
    },

    /**
     * 채널 상세 페이지 접근 시도
     */
    attemptChannelAccess() {
      const accessCheck = this.canAccessChannelDetail

      if (!accessCheck.canAccess) {
        return {
          success: false,
          ...accessCheck
        }
      }

      // 일반회원인 경우에만 횟수 증가
      if (this.isRegularUser) {
        this.incrementAccessCount()
      }

      return {
        success: true,
        ...accessCheck
      }
    },

    /**
     * 특정 사용자의 접근 데이터 초기화 (관리자 또는 테스트용)
     */
    resetUserAccessCount(userId = null) {
      const accountStore = useAccountStore()
      const targetUserId = userId || accountStore.user?.userId

      if (!targetUserId) {
        console.warn('초기화할 사용자 ID가 없습니다.')
        return false
      }

      delete this.accessData[targetUserId]
      console.log(`사용자 ${targetUserId}의 채널 접근 데이터 초기화 완료`)
      return true
    },

    /**
     * 모든 접근 데이터 초기화 (테스트용)
     */
    resetAllAccessData() {
      this.accessData = {}
      console.log('모든 채널 접근 데이터 초기화 완료')
    },
  }
}) 