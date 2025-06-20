import axios from "axios";
import { useAccountStore } from "@/stores/account";

const instance = axios.create({
  // baseURL: 'http://localhost:8080',  // json-server URL
  // baseURL: import.meta.env.VITE_API_BASE_URL,
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

        switch (error.response.status) {
            case 401: {
                const config = error.config;
                if (config.retried) {
                    // 토큰이 만료되었으므로 로그아웃 처리
                    const accountStore = useAccountStore();
                    accountStore.clearAuth();
                    localStorage.removeItem('token');
                    window.alert("로그인 정보가 만료되었습니다.");
                    window.location.replace("/login");
                    return;
                }
                try {
                    const res = await axios.get("/v1/api/nonuser/token");
                    const accessToken = res.data.accessToken || res.data;
                    const accountStore = useAccountStore();
                    accountStore.setAccessToken(accessToken);
                    config.headers.authorization = `Bearer ${accessToken}`;
                    config.retried = true;
                    return instance(config);
                } catch (e) {
                    const accountStore = useAccountStore();
                    accountStore.clearAuth();
                    localStorage.removeItem('token');
                    window.alert("로그인 정보가 만료되었습니다.");
                    window.location.replace("/login");
                }
                break;
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
const generateConfig = () => {
    const accountStore = useAccountStore();
    
    const config = {
        headers: {}
    };

    if (accountStore.accessToken) {
        config.headers.authorization = `Bearer ${accountStore.accessToken}`;
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
            ...generateConfig(),
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
            ...generateConfig(),
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
            ...generateConfig(),
            ...config
        });
    }
};

export default httpClient; 