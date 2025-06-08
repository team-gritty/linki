import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 캠페인 목록 조회
  getCampaigns: async (params = {}) => {
    try {
      const response = await httpClient.get('/v1/api/campaigns', {
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
      const response = await httpClient.get(`/v1/api/campaigns/${campaignId}`)
      const campaigns = response.data;
      // 단일 캠페인 반환
      return Array.isArray(campaigns) ? campaigns[0] : campaigns;
    } catch (error) {
      console.error('Error fetching campaign detail:', error)
      throw error
    }
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (productId) => {
    try {
      const response = await httpClient.get(`/v1/api/influencer/reviews/advertiser`, {
        params: {
          productId: String(productId),
          _sort: 'advertiserReviewCreatedAt',
          _order: 'desc'
        }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching advertiser reviews:', error)
      return []
    }
  },

  // 제안서 제출
  submitProposal: async (productId, contents) => {
    try {
      const response = await httpClient.post(`/api/proposals`, {
        contents: contents,
        product_id: String(productId),
        status: 'PENDING',
        submitted_at: new Date().toISOString(),
        influencer_id: 'INF001' // TODO: 실제 로그인한 사용자 ID로 대체
      })
      return response.data
    } catch (error) {
      console.error('Error submitting proposal:', error)
      throw error
    }
  }
} 