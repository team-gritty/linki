import axios from 'axios';

const BASE_URL = 'http://localhost:3000';

export const reviewApi = {
  // 인플루언서가 작성한 리뷰 조회
  getInfluencerReviews() {
    return axios.get(`${BASE_URL}/influencer-reviews`);
  },

  // 인플루언서가 받은 리뷰 조회
  getAdvertiserReviews() {
    return axios.get(`${BASE_URL}/advertiser-reviews`);
  },

  // 광고주에 대한 리뷰 작성
  submitAdvertiserReview(reviewData) {
    return axios.post(`${BASE_URL}/influencer-reviews`, reviewData);
  }
}; 