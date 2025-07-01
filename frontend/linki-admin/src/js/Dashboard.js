import httpClient from '@/utils/httpRequest'

export const getDashboard = async () => {
  return await httpClient.get('/v1/admin/api/dashboard')
}
