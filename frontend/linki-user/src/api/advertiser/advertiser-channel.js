import httpClient from '@/utils/httpRequest'

/**
 * 전체 채널 목록 조회
 * @param {number} page 페이지 번호 (0부터 시작)
 * @param {number} limit 페이지 크기 (기본값: 10)
 * @param {string} keyword 검색 키워드 (선택)
 * @param {object} filters 추가 필터 옵션
 * @param {string} sortBy 정렬 기준 (subscriberCount, avgViewCount 등, 선택)
 * @param {string} sortDirection 정렬 방향 (desc, asc, 기본값: desc)
 */
const channelApi = {
  getAllChannels: async (page = 0, limit = 10, keyword = null, filters = {}, sortBy = null, sortDirection = 'desc') => {
    let params = {} // params를 함수 스코프로 이동
    
    try {
      params = {
        page: page,
        limit: limit
      }
      
      // 키워드가 있으면 추가
      if (keyword && keyword.trim()) {
        params.keyword = keyword.trim()
      }
      
      // 정렬 파라미터 추가
      if (sortBy && sortBy.trim()) {
        params.sortBy = sortBy.trim()
      }
      if (sortDirection && sortDirection.trim()) {
        params.sortDirection = sortDirection.trim()
      }
      
      // 필터 적용 - null/undefined 체크를 더 엄격하게
      if (filters.category && filters.category.trim()) {
        params.category = filters.category.trim()
      }
      if (typeof filters.minSubscribers === 'number' && filters.minSubscribers >= 0 && filters.minSubscribers < 1000000000) {
        params.minSubscribers = Math.floor(filters.minSubscribers)
      }
      if (typeof filters.maxSubscribers === 'number' && filters.maxSubscribers > 0 && filters.maxSubscribers <= 1000000000) {
        params.maxSubscribers = Math.floor(filters.maxSubscribers)
      }
      if (typeof filters.minAvgViewCount === 'number' && filters.minAvgViewCount >= 0 && filters.minAvgViewCount < 1000000000) {
        params.minAvgViewCount = Math.floor(filters.minAvgViewCount)
      }
      if (typeof filters.maxAvgViewCount === 'number' && filters.maxAvgViewCount > 0 && filters.maxAvgViewCount <= 1000000000) {
        params.maxAvgViewCount = Math.floor(filters.maxAvgViewCount)
      }
      
      console.log('=== API 호출 상세 정보 ===')
      console.log('원본 filters:', filters)
      console.log('정렬 정보:', { sortBy, sortDirection })
      console.log('최종 params:', params)
      console.log('========================')
      
      const response = await httpClient.get('/v1/api/nonuser/channels', params)
      // 페이징 정보를 포함한 전체 응답을 반환
      return response.data
    } catch (error) {
      console.error('Error fetching all channels:', error)
      console.error('요청 파라미터:', params) // 이제 params에 접근 가능
      if (error.response) {
        console.error('응답 상태:', error.response.status)
        console.error('응답 데이터:', error.response.data)
      }
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
   * @param {string} keyword 검색 키워드 (선택)
   * @param {object} filters 추가 필터 옵션
   * @param {string} sortBy 정렬 기준 (subscriberCount, avgViewCount 등, 선택)
   * @param {string} sortDirection 정렬 방향 (desc, asc, 기본값: desc)
   * @returns {Promise<any[]>} 카테고리별 채널 목록
   */
  getChannelsByCategories: async (categories, page = 0, limit = 10, keyword = null, filters = {}, sortBy = null, sortDirection = 'desc') => {
    try {
      const params = {
        page: page,
        limit: limit
      }
      
      // 카테고리 처리 - 백엔드는 단일 카테고리만 지원하므로 첫 번째 카테고리 사용
      if (categories && categories.length > 0) {
        params.category = categories[0] // 첫 번째 카테고리만 사용
      }
      
      // 키워드가 있으면 추가
      if (keyword && keyword.trim()) {
        params.keyword = keyword.trim()
      }
      
      // 정렬 파라미터 추가
      if (sortBy && sortBy.trim()) {
        params.sortBy = sortBy.trim()
      }
      if (sortDirection && sortDirection.trim()) {
        params.sortDirection = sortDirection.trim()
      }
      
      // 필터 적용
      if (filters.minSubscribers !== undefined && filters.minSubscribers !== null) {
        params.minSubscribers = filters.minSubscribers
      }
      if (filters.maxSubscribers !== undefined && filters.maxSubscribers !== null) {
        params.maxSubscribers = filters.maxSubscribers
      }
      if (filters.minAvgViewCount !== undefined && filters.minAvgViewCount !== null) {
        params.minAvgViewCount = filters.minAvgViewCount
      }
      if (filters.maxAvgViewCount !== undefined && filters.maxAvgViewCount !== null) {
        params.maxAvgViewCount = filters.maxAvgViewCount
      }
      
      console.log('카테고리별 API 호출 파라미터:', params)
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