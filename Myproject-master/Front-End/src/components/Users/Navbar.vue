<script setup>
import { ref,defineEmits,defineProps } from 'vue'
import Searchbar from './Searchbar.vue'
import AccountMenu from './AccountMenu.vue'
const props = defineProps({
    currentPage: String  // ✅ 從 App.vue 接收
})
const emit = defineEmits(['change-page'])
const currentPage = ref('Home')
const navLinks = ref([{ name: 'Home' }, { name: 'My Fridge' }, { name: 'Recipes' }, { name: 'Shopping List' }, ])
const isMenuOpen = ref(false)
const isActive = (pageName) => {
   return props.currentPage === pageName
} //控制哪個按鈕要顯示橘色

const handlePageChange = (pageName) => {
    currentPage.value = pageName
    console.log('Navbar emit:', pageName)
    emit('change-page', pageName) 
} //通知App.vue切換畫面
</script>

<template>
    <header class="fixed top-0 w-full z-50 bg-gray-100 shadow-sm">
    <!-- 主導航欄 -->
    <div class="px-4 md:px-8 py-4">
        <!-- 手機版：Logo + 漢堡按鈕 -->
        <div class="flex md:hidden items-center justify-between">
            <div class="text-2xl font-bold text-orange-500 whitespace-nowrap">
                Pantry Pilot
            </div>
            <button
                @click="isMenuOpen = !isMenuOpen"
                class="p-2 rounded-lg hover:bg-gray-200 transition-colors"
            >
                <Icon 
                    v-if="!isMenuOpen"
                    icon="mdi:menu"
                    class="w-6 h-6 text-gray-700"
                />
                <Icon 
                    v-else
                    icon="mdi:close"
                    class="w-6 h-6 text-gray-700"
                />
            </button>
        </div>

        <!-- 桌面版：Logo + 導航 + 工具 -->
        <div class="hidden md:flex items-center justify-between">
            <div class="text-2xl font-bold text-orange-500 whitespace-nowrap">
                Pantry Pilot
            </div>
            <nav class="flex items-center space-x-6 flex-1 justify-center">
                <a
                    href="#"
                    v-for="link in navLinks"
                    :key="link.name"
                    :class="[
                        'px-6 py-2.5 rounded-full cursor-pointer transition-all duration-200 whitespace-nowrap text-lg font-semibold',
                        isActive(link.name)
                            ? 'bg-orange-100 text-orange-500 shadow-lg scale-110 border-b-4 border-orange-500'
                            : 'text-gray-500 hover:text-orange-500 '
                    ]"
                    @click.prevent="handlePageChange(link.name)"
                >
                    {{ link.name }}
                </a>
            </nav>
            <!-- 右側工具位置 -->
            <div class="flex items-center space-x-4 w-32">
                <!-- <Searchbar /> -->
                <!-- <AccountMenu /> -->
            </div>
        </div>
    </div>
    
    <!-- 手機版側邊欄選單 -->
    <div
        v-show="isMenuOpen"
        class="md:hidden bg-white border-t border-gray-200 relative z-50"
    >
        <nav class="flex flex-col space-y-1 px-4 py-4">
            <a
                href="#"
                v-for="link in navLinks"
                :key="link.name"
                :class="[
                    'px-4 py-3 rounded-lg transition-all duration-200 font-semibold text-base',
                    isActive(link.name)
                        ? 'bg-orange-100 text-orange-500'
                        : 'text-gray-700 hover:bg-orange-50 hover:text-orange-500'
                ]"
                @click.prevent="handlePageChange(link.name)"
            >
                {{ link.name }}
            </a>
        </nav>
    </div>

    <!-- 黑色遮罩 -->
    <div
        v-show="isMenuOpen"
        class="fixed inset-0 bg-black/50 md:hidden z-40"
        @click="isMenuOpen = false"
        style="top: 70px"
    ></div>
</header>
</template>
