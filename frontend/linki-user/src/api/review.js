import httpClient from '@/utils/httpRequest'

export const reviewApi = {
  // 인플루언서가 작성한 리뷰 조회
  async getWrittenReviews() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/mypage/reviews/written`);
      return response.data;
    } catch (error) {
      console.error('Error fetching written reviews:', error);
      throw error;
    }
  },

  // 기존 메서드명 호환성 유지
  async getInfluencerReviews() {
    return this.getWrittenReviews();
  },

  // 인플루언서가 받은 리뷰 조회
  async getAdvertiserReviews() {
    try {
      const url = `/v1/api/influencer/mypage/reviews/received`;
      console.log('🔍 DEBUG: Calling URL:', url);
      const response = await httpClient.get(url);
      return response.data;
    } catch (error) {
      console.error('Error fetching advertiser reviews:', error);
      throw error;
    }
  },

  // 리뷰 가능한 계약 목록 조회 (계약, 정산 상태가 모두 COMPLETED인 계약)
  async getReviewableContracts() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/mypage/reviews/available-contracts`);
      return response.data;
    } catch (error) {
      console.error('Error fetching reviewable contracts:', error);
      throw error;
    }
  },

  // 광고주에 대한 리뷰 작성
  async submitAdvertiserReview(reviewData) {
    try {
      const response = await httpClient.post(`/v1/api/influencer/mypage/reviews/advertiser-write`, reviewData);
      return response.data;
    } catch (error) {
      console.error('Error submitting advertiser review:', error);
      throw error;
    }
  },

  // 특정 캠페인에 대한 광고주 리뷰 조회
  async getAdvertiserReviewsByCampaign(campaignId) {
    try {
      const response = await httpClient.get(`/v1/api/nonuser/reviews/advertiser/campaign/${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching advertiser reviews by campaign:', error);
      throw error;
    }
  }
};