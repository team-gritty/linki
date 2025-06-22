import httpClient from '@/utils/httpRequest'

export const homeAPI = {

  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/categories')
      return response.data
    } catch (error) {
      console.error('Error fetching categories:', error)
      throw error
    }
  },

  // 인플루언서 목록 조회
  getmonthInfluencers: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/monthInfluencers')
      return response.data
    } catch (error) {
      console.error('Error fetching monthInfluencers:', error)
      throw error
    }
  },

  // 배너 목록 조회
  getBanners: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/banners')
      return response.data
    } catch (error) {
      console.error('Error fetching banners:', error)
      throw error
    }
  },

  // 오늘 마감 상품 조회
  getEndingTodayCampaigns: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/ending-today-campaigns')
      return response.data
    } catch (error) {
      console.error('Error fetching ending today products:', error)
      throw error
    }
  }
} 