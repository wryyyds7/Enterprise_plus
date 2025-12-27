import { createPinia } from 'pinia'
import { useAuthStore } from './modules/auth'
import { useAiStore } from './modules/ai'
import { useChatStore } from './modules/chat'
import { useEnterpriseStore } from './modules/enterprise'
import { useUserStore } from './modules/user'
import { useSearchStore } from './modules/search'

const pinia = createPinia()

export {
  useAuthStore,
  useAiStore,
  useChatStore,
  useEnterpriseStore,
  useUserStore,
  useSearchStore
}
export default pinia