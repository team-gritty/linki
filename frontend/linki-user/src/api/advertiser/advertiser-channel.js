import httpClient from '@/utils/httpRequest'

/**
 * 전체 채널 목록 조회
 * @param {number} page 페이지 번호 (0부터 시작)
 * @param {number} limit 페이지 크기 (기본값: 10)
 */
const channelApi = {
  getAllChannels: async (page = 0, limit = 10) => {
    try {
      const params = {
        page: page,
        limit: limit
      }
      const response = await httpClient.get('/v1/api/nonuser/channels', params)
      // 페이징 정보를 포함한 전체 응답을 반환
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
      const response = await httpClient.get(`/v1/api/user/channels/${channelId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching channel by ID:', error)
      throw error
    }
  },

  /**
   * 카테고리별 채널 목록 조회
   * @param {string[]} categories 카테고리 배열
   * @param {number} page 페이지 번호 (0부터 시작)
   * @param {number} limit 페이지 크기 (기본값: 10)
   * @returns {Promise<any[]>} 카테고리별 채널 목록
   */
  getChannelsByCategories: async (categories, page = 0, limit = 10) => {
    try {
      const params = {
        page: page,
        limit: limit
      }
      
      // 카테고리가 있으면 추가
      if (categories && categories.length > 0) {
        // 여러 카테고리를 각각 category 파라미터로 추가
        categories.forEach(cat => {
          if (!params.category) {
            params.category = cat
          } else {
            // 이미 category가 있으면 배열로 변환
            if (!Array.isArray(params.category)) {
              params.category = [params.category]
            }
            params.category.push(cat)
          }
        })
      }
      
      const response = await httpClient.get('/v1/api/nonuser/channels', params)
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
      const response = await httpClient.get(`/v1/api/user/channels/${channelId}/subscriber-history`)
      return response.data
    } catch (error) {
      console.error('Error fetching subscriber history:', error)
      throw error
    }
  }
}

export default channelApi 