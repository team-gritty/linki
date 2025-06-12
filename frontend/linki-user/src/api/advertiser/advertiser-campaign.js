import httpClient from '@/utils/httpRequest'

export const campaignApi = {
  // 캠페인 목록 조회
  getCampaigns: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/advertiser/campaigns', {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc',
          ...(params.productCategory && params.productCategory !== 'all' ? { productCategory: params.productCategory } : {})
        }
      })
      return {
        campaigns: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      }
    } catch (error) {
      console.error('Error fetching campaigns:', error)
      return {
        campaigns: [],
        totalItems: 0
      }
    }
  },

  // 캠페인 상세 정보 조회
  getCampaignDetail: async (campaignId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/campaigns/${campaignId}`)
      const campaigns = response.data;
      // 단일 캠페인 반환
      return Array.isArray(campaigns) ? campaigns[0] : campaigns;
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (advertiserId) => {
    try {
      const response = await httpClient.get(`v1/api/advertiser/reviews/received`, {
        params: {
          advertiser_id: String(advertiserId)
        }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching advertiser reviews:', error)
      return []
    }
  },

  // 마이페이지 - 캠페인 목록 조회
  getMyCampaigns: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/mypage/advertiser/campaigns', {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc'
        }
      })
      return {
        campaigns: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      }
    } catch (error) {
      console.error('Error fetching my campaigns:', error)
      return {
        campaigns: [],
        totalItems: 0
      }
    }
  },

  // 마이페이지 - 캠페인 상세 정보 조회
  getMyCampaignDetail: async (campaignId) => {
    try {
      console.log('API: Fetching campaign detail for ID:', campaignId)
      // campaign-list에서 데이터 가져오기
      const response = await httpClient.get(`v1/api/advertiser/campaigns?campaignId=${campaignId}`)
      console.log('API: Raw response:', response)
      
      if (!response.data || response.data.length === 0) {
        throw new Error('Campaign not found')
      }

      // 응답 데이터 변환
      const campaign = response.data[0]
      return {
        productImg: campaign.campaign_img,
        productName: campaign.campaign_name,
        companyName: campaign.campaign_brand,
        productDesc: campaign.campaign_desc,
        productDeadline: campaign.campaign_deadline,
        productCondition: campaign.campaign_condition,
        productCategory: campaign.campaign_category
      }
    } catch (error) {
      console.error('Error fetching my campaign detail:', error)
      throw error
    }
  },

  // 마이페이지 - 캠페인 등록
  registerCampaign: async (campaignData) => {
    try {
      const response = await httpClient.post('/v1/api/advertiser/mypage/campaigns', campaignData)
      return response.data
    } catch (error) {
      console.error('Error registering campaign:', error)
      throw error
    }
  }
}
