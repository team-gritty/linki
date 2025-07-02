// Google OAuth Configuration
export const GOOGLE_CONFIG = {
  // 실제 Google Cloud Console에서 가져온 클라이언트 ID로 교체 필요
  CLIENT_ID: 'YOUR_GOOGLE_CLIENT_ID',
  
  // OAuth 설정
  SCOPE: 'openid email profile',
  RESPONSE_TYPE: 'id_token',
  
  // 리디렉션 URI를 백엔드 설정에 맞춤 (포트 8081)
  getRedirectUri: () => 'http://211.188.61.90:8000/login/oauth2/code/google',
  
  // Google OAuth URL 생성 함수
  getAuthUrl: () => {
    const config = {
      client_id: GOOGLE_CONFIG.CLIENT_ID,
      scope: GOOGLE_CONFIG.SCOPE,
      redirect_uri: GOOGLE_CONFIG.getRedirectUri(),
      response_type: GOOGLE_CONFIG.RESPONSE_TYPE,
      nonce: Math.random().toString(36).substring(2, 15)
    }
    
    const params = new URLSearchParams(config)
    return `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`
  }
}

// API 설정
export const API_CONFIG = {
  BASE_URL: 'http://211.188.61.90:8000',
  GOOGLE_LOGIN_ENDPOINT: '/v1/api/user/auth/google'
} 