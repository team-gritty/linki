import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 캠페인 목록 조회
  getCampaigns: async (params = {}) => {
    const response = await httpClient.get('/v1/api/influencer/campaigns', {
      params: {
        _page: params._page || 1,
        _limit: params._limit || 10,
        _sort: 'createdAt',
        _order: params._order || 'desc',
        ...(params.productCategory && params.productCategory !== 'all' ? { productCategory: params.productCategory } : {})
      }
    })
    return {
      campaigns: response.data,
      totalItems: parseInt(response.headers['x-total-count'] || '0')
    }
  },

  // 캠페인 상세 조회
  getCampaignDetail: async (productId) => {
    const response = await httpClient.get(`/v1/api/influencer/campaigns/${productId}`)
    // json-server가 배열을 반환하므로 첫 번째 항목을 가져옴
    const data = Array.isArray(response.data) ? response.data[0] : response.data
    return data
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (productId) => {
    const response = await httpClient.get(`/v1/api/influencer/reviews/advertiser`, {
      params: {
        productId: String(productId),
        _sort: 'advertiserReviewCreatedAt',
        _order: 'desc'
      }
    })
    return response.data
  },

  // 제안서 제출
  submitProposal: async (productId, contents) => {
    const response = await httpClient.post(`/v1/api/influencer/campaigns/${productId}/proposals`, {
      contents: contents,
      product_id: String(productId),
      status: 'PENDING',
      submitted_at: new Date().toISOString(),
      influencer_id: 'INF001' // TODO: 실제 로그인한 사용자 ID로 대체
    })
    return response.data
  }
} 