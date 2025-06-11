import axios from "axios";
import { useAccountStore } from "@/stores/account";

const instance = axios.create({
    baseURL: 'http://localhost:3000',  // json-server URL
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
        console.log('API Request:', config.method.toUpperCase(), config.url, config.params || config.data)
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
    (error) => {
        console.error('API Error:', error.response?.status, error.response?.data)
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
    get(url, config = {}) {
        const finalConfig = {
            ...generateConfig(),
            ...config
        };
        return instance.get(url, finalConfig);
    },

    /**
     * POST 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    post(url, data) {
        return instance.post(url, data, generateConfig());
    },

    /**
     * PUT 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    put(url, data) {
        return instance.put(url, data, generateConfig());
    },

    /**
     * DELETE 요청
     * @param {string} url - 요청 URL
     * @returns {Promise}
     */
    delete(url) {
        return instance.delete(url, generateConfig());
    },

    /**
     * PATCH 요청
     * @param {string} url - 요청 URL
     * @param {Object} data - 요청 바디 데이터
     * @returns {Promise}
     */
    patch(url, data) {
        return instance.patch(url, data, generateConfig());
    }
};

export default httpClient; 