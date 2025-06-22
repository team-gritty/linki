//광고주가 사용하는 리뷰 관련 api

import httpClient from '@/utils/httpRequest'

const BASE_URL = '';  

export const reviewApi = {
  // 내가 받은(광고주) 리뷰 조회
  getReceivedReviews() {
    return httpClient.get(`/v1/api/advertiser/reviews/received`);
  },

  // 내가 쓴(인플루언서에게 남긴) 리뷰 조회
  getGivenReviews() {
    return httpClient.get(`/v1/api/advertiser/reviews/given`);
  },

  // 인플루언서에 대한 리뷰 작성 (작성한 리뷰 DB에 저장)
  writeInfluencerReview(reviewData) {
    return httpClient.post(`/v1/api/advertiser/reviews/given`, reviewData);
  },

  // 특정 인플루언서의 리뷰 조회 (채널 상세 페이지에서 사용)
  getInfluencerReviews(influencerId) {
    return httpClient.get(`/v1/api/user/influencers/${influencerId}/reviews`);
  },

  /**
   * 채널별 리뷰 평균점수/개수 fetch 및 계산
   * @param {number|string} channelId
   * @returns {Promise<{avg: number, count: number}>}
   */
  getReviewStats: async (channelId) => {
    try {
      const res = await httpClient.get(`/v1/api/user/influencers/${channelId}/reviews`);
      const reviews = res.data;
      const count = reviews.length;
      const avg = count > 0 ? (reviews.reduce((sum, r) => sum + (r.reviewScore || 0), 0) / count) : 0;
      return { avg, count };
    } catch (e) {
      console.warn(`채널 ${channelId} 리뷰 통계 조회 실패:`, e);
      return { avg: 0, count: 0 };
    }
  }
};