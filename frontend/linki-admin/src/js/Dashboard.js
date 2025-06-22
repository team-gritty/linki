import httpRequester from '@/libs/httpRequester'

export const getDashboard = async () => {
  return await httpRequester.get('/v1/admin/api/dashboard')
}
