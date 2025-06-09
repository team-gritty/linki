import httpRequester from '@/libs/httpRequester'

export const getReviewList = async (params) => {
  return await httpRequester.get('/v1/admin/api/reviews', { params })
}

export const approveReview = async (reviewId) => {
  return await httpRequester.post(`/v1/admin/api/reviews/${reviewId}/approve`)
}

export const rejectReview = async (reviewId) => {
  return await httpRequester.post(`/v1/admin/api/reviews/${reviewId}/reject`)
}

export const deleteReview = async (reviewId) => {
  return await httpRequester.delete(`/v1/admin/api/reviews/${reviewId}`)
} 