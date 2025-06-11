import axios from 'axios'

/**
 * 채널별 리뷰 평균점수/개수 fetch 및 계산
 * @param {number|string} channelId
 * @returns {Promise<{avg: number, count: number}>}
 */
export async function getReviewStats(channelId) {
  try {
    const res = await axios.get(`http://localhost:3000/influencer-reviews?influencerId=${channelId}`)
    const reviews = res.data
    const count = reviews.length
    const avg = count > 0 ? (reviews.reduce((sum, r) => sum + (r.influencerReviewScore || 0), 0) / count) : 0
    return { avg, count }
  } catch (e) {
    return { avg: 0, count: 0 }
  }
} 