import httpClient from '@/utils/httpRequest'

export const reviewApi = {
  // 인플루언서가 작성한 리뷰 조회
  async getInfluencerReviews() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/reviews/written`);
      return response.data;
    } catch (error) {
      console.error('Error fetching influencer reviews:', error);
      throw error;
    }
  },

  // 인플루언서가 받은 리뷰 조회
  async getAdvertiserReviews() {
    try {
      const response = await httpClient.get(`/v1/api/influencer/reviews/received`);
      return response.data;
    } catch (error) {
      console.error('Error fetching advertiser reviews:', error);
      throw error;
    }
  },

  // 광고주에 대한 리뷰 작성
  async submitAdvertiserReview(reviewData) {
    try {
      const response = await httpClient.post(`/v1/api/influencer/reviews/written`, reviewData);
      return response.data;
    } catch (error) {
      console.error('Error submitting advertiser review:', error);
      throw error;
    }
  },

  // 특정 캠페인에 대한 광고주 리뷰 조회
  async getAdvertiserReviewsByCampaign(campaignId) {
    try {
      const response = await httpClient.get(`/api/influencer/reviews/advertiser/campaign/${campaignId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching advertiser reviews by campaign:', error);
      throw error;
    }
  }
};