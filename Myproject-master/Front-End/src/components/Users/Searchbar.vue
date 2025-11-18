<script setup>
import { ref, computed } from 'vue'
const isHovered = ref(false) // 追蹤滑鼠是否懸停在容器上 (用於預覽展開)
const isLocked = ref(false) // 追蹤是否被點擊鎖定 (使用者正在輸入)

const isExpanded = computed(() => isHovered.value || isLocked.value) //決定最終的展開狀態：只要懸停或鎖定，即展開

const handleMouseEnter = () => {
    isHovered.value = true
} //懸停事件處理：滑鼠進入

const handleMouseLeave = () => {
    // 只有當沒有被「鎖定」時，滑鼠離開才會收縮
    if (!isLocked.value) {
        isHovered.value = false
    }
} //懸停事件處理：滑鼠離開

const toggleLock = () => {
    // 點擊容器時切換鎖定狀態
    isLocked.value = !isLocked.value

    if (isLocked.value) {
        isHovered.value = true // 確保展開狀態

        // 使用 setTimeout 確保 DOM 更新後，輸入框再獲取焦點
        setTimeout(() => {
            const input = document.getElementById('search-input')
            if (input) input.focus()
        }, 100)
    } else {
        // 解鎖後，如果滑鼠不在上面，則收縮
        isHovered.value = false
    }
} //點擊/鎖定事件處理

const handleInputBlur = () => {
    // 輸入框失去焦點時，解除鎖定狀態。
    // 如果此時滑鼠不在容器上，搜尋框會自動收縮。
    isLocked.value = false
} //輸入框失焦處理：實現點擊外部收回的關鍵

const searchClasses = computed(() => ({
    'w-10 px-3': !isExpanded.value, // 未展開時
    'w-56 px-5': isExpanded.value, // 展開時
    'bg-gray-400': isLocked.value, // 鎖定時提供更亮的背景，作為視覺提示
}))

const inputClasses = computed(() => ({
    // 未展開時：隱藏、不可點擊、寬度為零
    'opacity-0 pointer-events-none w-0': !isExpanded.value,
    // 展開時：完全顯示、佔滿空間、右側留白給圖標
    'opacity-100 pr-6 w-full bg-transparent': isExpanded.value,
})) //計算 Tailwind 類別：控制輸入框的透明度和寬度
</script>
<template>
    <div
        :class="[
            // 基礎樣式：霧化玻璃、圓角、過渡動畫
            'relative flex items-center bg-gray-300  rounded-full py-2',
            'transition-all duration-300 ease-out cursor-pointer',
            // 懸停/鎖定狀態的視覺差異
            { 'hover:bg-gray-400': !isLocked },
            { 'bg-gray-400': isLocked },
            searchClasses, // 應用計算屬性來控制寬度
        ]"
        @mouseenter="handleMouseEnter"
        @mouseleave="handleMouseLeave"
        @click="toggleLock"
    >
        <input
            id="search-input"
            type="text"
            placeholder="Search..."
            :class="[
                'shrink-0  text-gray-800 placeholder-gray-800 focus:outline-none text-sm',
                'transition-all duration-300 ease-in-out ',
                inputClasses, // 應用計算屬性來控制透明度和寬度
            ]"
            @click.stop
            @focus="isLocked = true"
            @blur="handleInputBlur"
        />

        <Icon
            icon="ic:round-search"
            class="w-4 h-4 text-[#ff5400] absolute top-1/2 -translate-y-1/2"
            :class="{ 'right-3': !isExpanded, 'right-5': isExpanded }"
        />
    </div>
</template>
