import httpClient from '@/utils/httpRequest'

export const reviewAPI = {
  // 채널별 리뷰 통계 조회
  getChannelReviewStats: async (channelId) => {
    try {
      const response = await httpClient.get(`/v1/api/channels/${channelId}/reviews`)
      const reviews = response.data
      const count = reviews.length
      const avg = count > 0 
        ? (reviews.reduce((sum, r) => sum + (r.influencer_review_score || 0), 0) / count) 
        : 0
      return { avg, count }
    } catch (error) {
      console.error('Error fetching review stats:', error)
      return { avg: 0, count: 0 }
    }
  },

  // 인플루언서 리뷰 목록 조회
  getInfluencerReviews: async (channelId, params = {}) => {
    try {
      const response = await httpClient.get(`/v1/api/channels/${channelId}/reviews`, {
        params: {
          _page: params._page || 1,
          _limit: params._limit || 10,
          _sort: params._sort || 'createdAt',
          _order: params._order || 'desc'
        }
      })
      return {
        reviews: response.data,
        totalItems: parseInt(response.headers['x-total-count'] || '0')
      }
    } catch (error) {
      console.error('Error fetching influencer reviews:', error)
      return {
        reviews: [],
        totalItems: 0
      }
    }
  }
} 