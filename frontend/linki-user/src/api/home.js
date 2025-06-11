import httpClient from '@/utils/httpRequest'

export const homeAPI = {
  // 캠페인 목록 조회
  getCampaigns: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/campaigns')
      return response.data.map(campaign => ({
        campaignId: campaign.campaignId,
        campaignName: campaign.campaignName,
        campaignImg: campaign.campaignImg,
        campaignCategory: campaign.campaignCategory,
        campaignDeadline: campaign.campaignDeadline,
        campaignStatus: campaign.campaignStatus,
        companyName: campaign.companyName,
        campaignCondition: campaign.campaignCondition,
        campaignDesc: campaign.campaignDesc,
        createdAt: campaign.createdAt
      }))
    } catch (error) {
      console.error('Error fetching campaigns:', error)
      throw error
    }
  },

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
  getInfluencers: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/influencers')
      return response.data.map(influencer => ({
        id: influencer.id,
        name: influencer.name,
        profileImage: influencer.profileImage,
        category: influencer.category,
        subscribers: typeof influencer.subscribers === 'string' ? 
          influencer.subscribers : 
          influencer.subscribers.toLocaleString(),
        reviews: influencer.avgCommentCount || 0,
        rating: 4.5,
        platform: 'YouTube',
        averageViews: influencer.avgViewCount
      }))
    } catch (error) {
      console.error('Error fetching influencers:', error)
      throw error
    }
  },

  // 배너 목록 조회
  getBanners: async () => {
    try {
      const response = await httpClient.get('/v1/api/banners')
      return response.data
    } catch (error) {
      console.error('Error fetching banners:', error)
      throw error
    }
  },

  // 사이드바 카테고리 조회
  getSidebarCategories: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/sidebar-categories')
      return response.data
    } catch (error) {
      console.error('Error fetching sidebar categories:', error)
      throw error
    }
  },

  // 오늘 마감 상품 조회
  getEndingTodayProducts: async () => {
    try {
      const response = await httpClient.get('/v1/api/home/ending-today')
      return response.data
    } catch (error) {
      console.error('Error fetching ending today products:', error)
      throw error
    }
  }
} 