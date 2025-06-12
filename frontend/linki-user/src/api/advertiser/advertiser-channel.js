import httpClient from '@/utils/httpRequest'

/**
 * 전체 채널 목록 조회
 */
const channelApi = {
  getAllChannels: async () => {
    try {
      const response = await httpClient.get('/v1/api/channels')
      return response.data
    } catch (error) {
      console.error('Error fetching all channels:', error)
      throw error
    }
  },

  /**
   * 특정 채널 상세 조회 (채널 상세 페이지)
   * @param {number|string} channelId 채널 ID
   * @returns {Promise<any>} 채널 데이터
   */
  getChannelById: async (channelId) => {
    try {
      const response = await httpClient.get(`/v1/api/channels/${channelId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching channel by ID:', error)
      throw error
    }
  },

  /**
   * 카테고리별 채널 목록 조회
   * @param {string[]} categories 카테고리 배열
   * @returns {Promise<any[]>} 카테고리별 채널 목록
   */
  getChannelsByCategories: async (categories) => {
    try {
      const params = categories.map(cat => `category=${encodeURIComponent(cat)}`).join('&')
      const response = await httpClient.get(`/v1/api/channels?${params}`)
      return response.data
    } catch (error) {
      console.error('Error fetching channels by categories:', error)
      throw error
    }
  },

  /**
   * 구독자 수 히스토리 조회
   * @param {number|string} channelId 채널 ID
   * @returns {Promise<any[]>} 구독자 수 히스토리 데이터
   */
  getSubscriberHistory: async (channelId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/channels/${channelId}/subscriber-history`)
      return response.data
    } catch (error) {
      console.error('Error fetching subscriber history:', error)
      throw error
    }
  }
}

export default channelApi 