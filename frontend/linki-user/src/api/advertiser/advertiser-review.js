//광고주가 사용하는 리뷰 관련 api

import httpClient from '@/utils/httpRequest'
import { contractApi, CONTRACT_STATUS } from './advertiser-contract'

export const reviewApi = {
  // 내가 받은(광고주) 리뷰 조회
  getReceivedReviews() {
    return httpClient.get(`/v1/api/advertiser/mypage/reviews/received`);
  },

  // 내가 쓴(인플루언서에게 남긴) 리뷰 조회
  getGivenReviews() {
    return httpClient.get(`/v1/api/advertiser/mypage/reviews/given`);
  },

  // 리뷰 작성 가능한 완료된 계약 조회 - 백엔드 API 사용
  async getCompletedContractsForReview() {
    try {
      const url = `/v1/api/advertiser/mypage/reviews/contracts`;
      console.log('=== getCompletedContractsForReview API 호출 ===');
      console.log('Request URL:', url);

      const response = await httpClient.get(url);

      if (Array.isArray(response.data)) {
        response.data.forEach((item, index) => {
        });
      }

      return response.data;
    } catch (error) {
      console.error('Error fetching completed contracts for review:', error);
      throw error;
    }
  },

  // 인플루언서에 대한 리뷰 작성 (작성한 리뷰 DB에 저장)
  writeInfluencerReview(contractId, reviewData) {
    console.log('=== 리뷰 작성 API 호출 ===');
    console.log('Contract ID:', contractId);
    console.log('Review Data:', reviewData);
    return httpClient.post(`/v1/api/advertiser/mypage/reviews/${contractId}`, reviewData);
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