import axios from "axios";
import { useAccountStore } from "@/stores/account";

const instance = axios.create({
    // baseURL: 'http://localhost:8080',  // admin-integration-service URL
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json',
    },
    paramsSerializer: {
        encode: encodeURIComponent,
        serialize: (params) => {
            // URL 쿼리 파라미터 직렬화
            const searchParams = new URLSearchParams();
            for (const key in params) {
                if (params[key] !== undefined && params[key] !== null) {
                    searchParams.append(key, params[key]);
                }
            }
            return searchParams.toString();
        }
    }
});

// 요청 인터셉터
instance.interceptors.request.use(
    (config) => {
        console.log('API Request:', config.method?.toUpperCase(), config.url, config.params || config.data)
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터
instance.interceptors.response.use(
    (response) => {
        console.log('API Response:', response.status, response.data)
        return response;
    },
    async (error) => {
        console.error('API Error:', error.response?.status, error.response?.data)
        if (!error.response) return Promise.reject(error);

        const config = error.config;

        switch (error.response.status) {
            case 401: {
                console.log('401 에러 발생 - URL:', config.url, 'Method:', config.method);

                // 로그인 요청 실패는 '아이디/비밀번호 불일치'이므로 토큰 재발급 로직을 실행하지 않음
                if (config.url === 'v1/api/admin/login' || config.url === '/v1/api/admin/login') {
                    console.log('로그인 실패. 토큰 재발급 로직을 건너뜁니다.');
                    return Promise.reject(error);
                }
                
                console.log('이미 재시도했는지:', config.retried);
                
                if (config.retried) {
                    // 토큰이 만료되었으므로 로그아웃 처리
                    console.log('이미 재시도했으므로 로그아웃 처리');
                    const accountStore = useAccountStore();
                    accountStore.clearAuth();
                    localStorage.removeItem('token');
                    window.alert("로그인 정보가 만료되었습니다.");
                    window.location.replace("/login");
                    return Promise.reject(error);
                }
                try {
                    console.log('토큰 재발급 시도 중...');
                    const res = await axios.get("/v1/api/admin/token", {
                        withCredentials: true
                    });
                    console.log('토큰 재발급 응답:', res.data);
                    
                    const accessToken = res.data.accessToken;
                    if (!accessToken) {
                        throw new Error('토큰 재발급 실패: accessToken이 없습니다');
                    }
                    
                    const accountStore = useAccountStore();
                    accountStore.setAccessToken(accessToken);
                    config.headers.authorization = `Bearer ${accessToken}`;
                    config.retried = true;
                    console.log('토큰 재발급 성공, 원래 요청 재시도');
                    return instance(config);
                } catch (e) {
                    console.error('토큰 재발급 실패:', e);
                    console.error('에러 응답:', e.response?.data);
                    const accountStore = useAccountStore();
                    accountStore.clearAuth();
                    localStorage.removeItem('token');
                    window.alert("로그인 정보가 만료되었습니다.");
                    window.location.replace("/login");
                    return Promise.reject(e);
                }
            }
            case 400:
                console.log("잘못된 요청입니다.");
                break;
            case 500:
                console.log("오류가 있습니다. 관리자에게 문의해주세요.");
                break;
        }
        return Promise.reject(error);
    }
);

// HTTP 요청 설정 생성
const generateConfig = (data) => {
    const accountStore = useAccountStore();
    
    const config = {
        headers: {}
    };

    if (accountStore.accessToken) {
        config.headers.authorization = `Bearer ${accountStore.accessToken}`;
    }

    // FormData인 경우 Content-Type을 삭제하여 브라우저가 자동으로 boundary 설정하도록 함
    if (data instanceof FormData) {
        // FormData의 경우 Content-Type을 명시적으로 설정하지 않음 (브라우저가 자동 설정)
        config.headers['Content-Type'] = undefined;
    }

    return config;
};

// HTTP 클라이언트 메서드
const httpClient = {
    /**
     * GET 요청
     * @param {string} url - 요청 URL
     * @param {Object} [params] - URL 쿼리 파라미터
     * @returns {Promise} 
     */
    get(url, params = {}, config = {}) {
        const finalConfig = {
            ...generateConfig(),
            ...config,
            params
        };
        
        // params가 있고 빈 객체가 아닐 때만 추가
        if (params && typeof params === 'object' && Object.keys(params).length > 0) {
            finalConfig.params = params;
        }
        
        return instance.get(url, finalConfig);
    },

    /**
     * POST 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    post(url, data, config = {}) {
        return instance.post(url, data, {
            ...generateConfig(data),
            ...config
        });
    },

    /**
     * PUT 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    put(url, data, config = {}) {
        return instance.put(url, data, {
            ...generateConfig(data),
            ...config
        });
    },

    /**
     * DELETE 요청
     * @param {string} url - 요청 URL
     * @returns {Promise}
     */
    delete(url, config = {}) {
        return instance.delete(url, {
            ...generateConfig(),
            ...config
        });
    },

    /**
     * PATCH 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    patch(url, data, config = {}) {
        return instance.patch(url, data, {
            ...generateConfig(data),
            ...config
        });
    }
};

export default httpClient; 