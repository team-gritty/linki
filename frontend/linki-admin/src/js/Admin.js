import httpRequester from '@/libs/httpRequester'

export const getAdminList = async (params) => {
  return await httpRequester.get('/v1/admin/api/admins', { params })
}

export const approveAdmin = async (adminId) => {
  return await httpRequester.post(`/v1/admin/api/admins/${adminId}/approve`)
}

export const rejectAdmin = async (adminId) => {
  return await httpRequester.post(`/v1/admin/api/admins/${adminId}/reject`)
} 