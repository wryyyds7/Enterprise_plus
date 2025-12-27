import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')  // 配置路径别名
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8089',  // 网关服务器地址，端口8089
        changeOrigin: true,  // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '')  // 去掉/api前缀，后端无需处理
      }
    }
  }
})
