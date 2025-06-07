import axios from 'axios'

/**
 * 채널별 리뷰 평균점수/개수 fetch 및 계산
 * @param {number|string} channelId
 * @returns {Promise<{avg: number, count: number}>}
 */
export async function getReviewStats(channelId) {
  try {
    const res = await axios.get(`http://localhost:3001/influencer-reviews?influencer_id=${channelId}`)
    const reviews = res.data
    const count = reviews.length
    const avg = count > 0 ? (reviews.reduce((sum, r) => sum + (r.influencer_review_score || 0), 0) / count) : 0
    return { avg, count }
  } catch (e) {
    return { avg: 0, count: 0 }
  }
} 