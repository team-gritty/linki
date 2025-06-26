import httpRequester from '@/libs/httpRequester.js'

export const getInfluencerUserList = async (page, size) => {
  return await httpRequester.get('/v1/admin/api/influencerUsers')
}

export const searchInfluencerUser = async (searchType, keyword) => {
  // 스프링 연동 시 사용할 코드
    return await httpRequester.post('/v1/admin/api/influencerUsers/search', {
      searchType,
      keyword
    })

  // json-server 테스트용 코드
  // return await httpRequester.get(`/v1/admin/api/influencerUsers/search/${searchType}?q=${keyword}`)
}

export async function exportExcel() {
  const res = await httpRequester.post('/v1/admin/api/influencerUsers/exportExcel');
  const downloadUrl = res.data;
  window.open(downloadUrl);
}

// Keyset 페이지네이션 기본 조회
export const getInfluencerUserListWithKeyset = async (cursor = null, size = 10) => {
  const params = { size }
  if (cursor) {
    params.cursor = cursor
  }
  return await httpRequester.get('/v1/admin/api/influencerUsers', { params })
}

// Keyset 페이지네이션 검색
export const searchInfluencerUserWithKeyset = async (searchType, keyword, cursor = null, size = 10) => {
  const requestBody = {
    searchType,
    keyword,
    cursor,
    size
  }
  return await httpRequester.post('/v1/admin/api/influencerUsers/search', requestBody)
}