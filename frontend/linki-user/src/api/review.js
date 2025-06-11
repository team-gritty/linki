import axios from 'axios';



export const reviewApi = {
  // 인플루언서가 작성한 리뷰 조회
  getInfluencerReviews() {
    return axios.get(`/v1/api/influencer/reviews/written`);
  },

  // 인플루언서가 받은 리뷰 조회
  getAdvertiserReviews() {
    return axios.get(`/v1/api/influencer/reviews/received`);
  },

  // 광고주에 대한 리뷰 작성
  submitAdvertiserReview(reviewData) {
    return axios.post(`/v1/api/influencer/reviews/written`, reviewData);
  }
};