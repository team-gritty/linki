import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  define: {
    global: 'globalThis',
  },
  server: {
    port: 3002,
    host: '0.0.0.0',
    allowedHosts: ['linki.kr', 'www.linki.kr'],
    proxy: {
      '/v1': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        ws: true
      },
    }
  }
})
