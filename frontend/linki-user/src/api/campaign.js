import httpClient from '@/utils/httpRequest'

export const campaignAPI = {
  // 캠페인 목록 조회
  getCampaigns: async (params) => {
    const response = await httpClient.get('/campaigns', params)
    return {
      campaigns: response.data,
      totalItems: parseInt(response.headers['x-total-count'] || '0')
    }
  },

  // 캠페인 상세 조회
  getCampaignDetail: async (campaignId) => {
    const response = await httpClient.get(`/campaign-details/${campaignId}`)
    return response.data[campaignId] // campaign-details 객체에서 해당 ID의 데이터 반환
  },

  // 광고주 리뷰 조회
  getAdvertiserReviews: async (productId) => {
    const response = await httpClient.get('/advertiser-reviews', {
      productId: productId
    })
    // json-server에서는 쿼리 파라미터로 필터링
    return response.data.filter(review => review.productId === productId)
  },

  // 제안서 제출
  submitProposal: async (productId, contents) => {
    const response = await httpClient.post(`/proposals`, {
      contents: contents,
      product_id: productId,
      status: 'PENDING',
      submitted_at: new Date().toISOString(),
      influencer_id: 'INF001' // TODO: 실제 로그인한 사용자 ID로 대체
    })
    return response.data
  }
} 