import httpClient from '@/utils/httpRequest'

export const homeAPI = {
  // 캠페인 목록 조회
  getCampaigns: async () => {
    try {
      const response = await httpClient.get('/home/api/campaignProducts')
      return response.data.map(campaign => ({
        id: campaign.id,
        productName: campaign.name || campaign.productName,
        productImg: campaign.image || campaign.productImg,
        productCategory: campaign.category || campaign.productCategory,
        productDeadline: campaign.deadline || campaign.productDeadline,
        productPublishStatus: campaign.status || campaign.productPublishStatus,
        companyName: campaign.advertiser || campaign.companyName
      }))
    } catch (error) {
      console.error('Error fetching campaigns:', error)
      throw error
    }
  },

  // 카테고리 목록 조회
  getCategories: async () => {
    try {
      const response = await httpClient.get('/home/api/categories')
      return response.data
    } catch (error) {
      console.error('Error fetching categories:', error)
      throw error
    }
  },

  // 인플루언서 목록 조회
  getInfluencers: async () => {
    try {
      const response = await httpClient.get('/home/api/influencers')
      return response.data
    } catch (error) {
      console.error('Error fetching influencers:', error)
      throw error
    }
  },

  // 배너 목록 조회
  getBanners: async () => {
    try {
      const response = await httpClient.get('/home/api/banners')
      return response.data
    } catch (error) {
      console.error('Error fetching banners:', error)
      throw error
    }
  },

  // 사이드바 카테고리 조회
  getSidebarCategories: async () => {
    try {
      const response = await httpClient.get('/home/api/sidebarCategories')
      return response.data
    } catch (error) {
      console.error('Error fetching sidebar categories:', error)
      throw error
    }
  },

  // 오늘 마감 상품 조회
  getEndingTodayProducts: async () => {
    try {
      const response = await httpClient.get('/home/api/endingTodayProducts')
      return response.data
    } catch (error) {
      console.error('Error fetching ending today products:', error)
      throw error
    }
  }
} 