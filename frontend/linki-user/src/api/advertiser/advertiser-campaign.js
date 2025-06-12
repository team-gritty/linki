import httpClient from '@/utils/httpRequest'

const campaignApi = {
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
      return Array.isArray(response.data) ? response.data[0] : response.data
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (advertiserId) => {
    try {
      const response = await httpClient.get(`/v1/api/advertiser/reviews/received`, {
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
      const response = await httpClient.get('/v1/api/advertiser/campaigns', {
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
      
      // 쿼리 파라미터 방식으로 호출하면 배열이 반환됨
      const response = await httpClient.get(`/v1/api/advertiser/campaigns/${campaignId}`)
      console.log('API: Raw response:', response)
      
      if (!response.data || response.data.length === 0) {
        throw new Error('Campaign not found')
      }

      // 쿼리 파라미터 방식이므로 배열의 첫 번째 요소를 사용
      const campaign = response.data[0]
      
      return {
        campaignImg: campaign.campaignImg,
        campaignName: campaign.campaignName,
        companyName: campaign.companyName,
        campaignDesc: campaign.campaignDesc,
        campaignDeadline: campaign.campaignDeadline,
        campaignCondition: campaign.campaignCondition,
        campaignCategory: campaign.campaignCategory
      }
    } catch (error) {
      console.error('Error fetching my campaign detail:', error)
      throw error
    }
  },

  // 마이페이지 - 캠페인 등록
  registerCampaign: async (campaignData) => {
    try {
      const response = await httpClient.post('/v1/api/advertiser/campaigns', campaignData)
      return response.data
    } catch (error) {
      console.error('Error registering campaign:', error)
      throw error
    }
  },

  // 캠페인 수정
  updateCampaign: async (campaignId, campaignData) => {
    console.log("PUT 요청 왔습니다---------", campaignId, campaignData)
    try {
      const response = await httpClient.put(`/v1/api/advertiser/campaigns/${campaignId}`, campaignData)
      return response.data
    } catch (error) {
      console.error('Error updating campaign:', error)
      throw error
    }
  },

  // 캠페인 공개/비공개 및 모집상태 변경
  updateCampaignStatus: async (campaignId, statusData) => {
    try {
      const response = await httpClient.patch(`/v1/api/advertiser/campaigns/${campaignId}`, statusData)
      return response.data
    } catch (error) {
      console.error('Error updating campaign status:', error)
      throw error
    }
  }
}

export default campaignApi
