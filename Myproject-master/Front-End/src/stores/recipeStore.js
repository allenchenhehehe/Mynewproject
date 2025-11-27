import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Mock 數據 - 所有食譜
const mockRecipes = [
    {
        id: 1,
        title: '番茄炒蛋',
        description: '簡單快手菜，營養豐富，酸甜開胃的家常經典。',
        difficulty: 3,
        cooking_time: 12,
        step: '1. 打蛋：將雞蛋打入碗中，加入少許鹽和幾滴水（讓蛋更嫩滑），用筷子充分攪打均勻，直至蛋液起泡。\n\n2. 炒蛋：鍋中倒入適量的油，燒熱後將蛋液倒入，快速用鏟子劃散，炒至半熟且邊緣微焦時盛出備用。\n\n3. 加番茄：利用鍋中餘油，將切好的番茄塊倒入，加入少許鹽和糖（糖的量可根據番茄的酸度調整），中小火翻炒至番茄出汁變軟。\n\n4. 合體：將炒好的雞蛋重新倒入鍋中，與番茄汁快速翻炒均勻，讓雞蛋充分吸收番茄的酸甜味，可以淋入少許水澱粉勾芡使湯汁濃稠，最後撒上蔥花即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 4, ingredient_name: '番茄', quantity: 2, unit: '個',category: 'vegetable' },
            { ingredient_id: 3, ingredient_name: '雞蛋', quantity: 3, unit: '個',category: 'egg' },
            { ingredient_id: 101, ingredient_name: '蔥花', quantity: 1, unit: '匙',category: 'vegetable' },
        ],
    },
    {
        id: 2,
        title: '紅燒肉',
        description: '經典濃油赤醬的本幫菜，肥而不膩，入口即化，色澤紅亮誘人。',
        difficulty: 5,
        cooking_time: 90,
        step: '1. 汆燙：將五花肉切成麻將大小的塊狀，冷水下鍋，加入薑片和料酒，大火煮沸後撇去浮沫，撈出洗淨瀝乾。\n\n2.  炒糖色：鍋中放少許油，加入冰糖或白砂糖，小火慢慢熬煮至糖融化變成琥珀色，注意不要炒焦。\n\n3. 上色：將五花肉塊倒入鍋中，快速翻炒，讓每塊肉都均勻裹上糖色，隨後加入薑片、蔥段、八角和桂皮等香料。\n\n4. 慢燉入味：倒入足量的熱水（水量需沒過肉塊）、老抽和生抽調味，大火煮沸後轉最小火，蓋上鍋蓋燜煮60-90分鐘，直到肉質軟爛。\n\n5. 收汁：待肉軟爛後，開大火將湯汁收濃，期間要不斷翻動防止粘鍋，讓湯汁緊密地包裹在肉塊上，呈現出油光發亮的狀態即可。',
        creator_id: 'system',
        is_public: true,
        image_url: '/Redbraisedpork.webp',
        ingredients: [
            { ingredient_id: 5, ingredient_name: '五花肉', quantity: 500, unit: '克',category: 'meat' },
            { ingredient_id: 6, ingredient_name: '冰糖', quantity: 30, unit: '克', category: 'seasoning' },
            { ingredient_id: 7, ingredient_name: '醬油（老抽/生抽）', quantity: 3, unit: '匙', category: 'seasoning' },
        ],
    },
    {
        id: 3,
        title: '宮保雞丁',
        description: '川菜經典名菜，雞肉鮮嫩，花生米酥脆，味道鹹甜酸辣適中。',
        difficulty: 5,
        cooking_time: 20,
        step: '1. 醃製雞丁：將雞胸肉或雞腿肉切成小丁，加入少許鹽、料酒、蛋清和澱粉，抓勻後醃製10分鐘備用。\n\n2. 調製醬汁：將醋、糖、生抽、老抽、料酒、水澱粉和少量清水混合，攪拌均勻製成宮保汁。\n\n3. 炒香配料：鍋中放油燒熱，先將乾辣椒段和花椒粒小火煸炒出香味，隨後放入蔥段和薑片爆香。\n\n4. 滑炒雞丁：將醃好的雞丁倒入鍋中，快速滑炒至變色，然後加入事先炸酥的花生米。\n\n5. 淋醬收尾：將調好的宮保汁沿鍋邊倒入，迅速翻炒，讓醬汁均勻地包裹住雞丁和花生米，湯汁變得濃稠後即可出鍋，保持雞丁的嫩滑和花生米的酥脆。',
        creator_id: 'system',
        is_public: true,
        image_url: '/Bao.webp',
        ingredients: [
            { ingredient_id: 8, ingredient_name: '雞胸肉', quantity: 300, unit: '克', category: 'meat' },
            { ingredient_id: 9, ingredient_name: '花生米', quantity: 50, unit: '克', category: 'seasoning' },
            { ingredient_id: 10, ingredient_name: '乾辣椒/花椒', quantity: 10, unit: '克', category: 'seasoning' },
        ],
    },
    {
        id: 4,
        title: '麻婆豆腐',
        description: '著名的川菜，麻、辣、燙、嫩、酥、香、鮮，口感和風味極具特色。',
        difficulty: 4,
        cooking_time: 15,
        step: '1. 準備豆腐：將嫩豆腐切成約2公分見方的小塊，放入加了少許鹽的熱水中輕輕汆燙1分鐘，撈出瀝水，這能讓豆腐不易碎。\n\n2. 炒香調料：鍋中放油燒熱，先放入牛肉末或豬肉末煸炒至變色，接著加入豆瓣醬和豆豉，小火炒出紅油和香氣。\n\n3. 下豆腐：將切好的豆腐塊輕輕倒入鍋中，加入適量的水或高湯，轉中小火慢煮入味。\n\n4. 調味收汁：加入生抽、少許糖調味，待湯汁稍微收乾時，勾入適量的水澱粉使湯汁變得濃稠。\n\n5. 撒料出鍋：最後撒上大量的花椒粉（這是「麻」的關鍵）和蔥花，輕輕推勻後即可出鍋，趁熱食用風味最佳。',
        creator_id: 'system',
        is_public: true,
        image_url: '/Tofu.webp',
        ingredients: [
            { ingredient_id: 11, ingredient_name: '嫩豆腐', quantity: 1, unit: '塊', category: 'bean'},
            { ingredient_id: 12, ingredient_name: '牛肉/豬肉末', quantity: 100, unit: '克', category: 'meat'},
            { ingredient_id: 13, ingredient_name: '豆瓣醬', quantity: 2, unit: '匙', category: 'seasoning'},
        ],
    },
    {
        id: 5,
        title: '魚香肉絲',
        description: '魚香是川菜獨特的複合味，鹹甜酸辣兼具，醬汁濃郁，拌飯一絕。',
        difficulty: 5,
        cooking_time: 25,
        step: '1. 醃製肉絲：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。\n\n2. 調製魚香汁：將醋、糖、生抽、老抽、水澱粉和清水按比例混合均勻，製成魚香汁。\n\n3. 滑炒肉絲：鍋中熱油，將醃好的肉絲快速滑炒至變色即刻盛出，保持肉絲的嫩度。\n\n4. 炒香配料：利用底油，先後放入薑末、蒜末、泡椒末和剁碎的豆瓣醬，小火炒出紅油和香氣。\n\n5. 混合收汁：將肉絲和切好的木耳絲、筍絲等配料倒入鍋中，快速翻炒，隨後倒入魚香汁，大火翻炒，讓醬汁均勻地裹在食材上，湯汁變得濃稠發亮後即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/Fish&Meat.webp',
        ingredients: [
            { ingredient_id: 14, ingredient_name: '豬里脊肉', quantity: 250, unit: '克', category: 'meat' },
            { ingredient_id: 15, ingredient_name: '木耳/筍絲', quantity: 100, unit: '克', category: 'vegetable' },
            { ingredient_id: 16, ingredient_name: '泡椒/豆瓣醬', quantity: 1, unit: '匙', category: 'seasoning' },
        ],
    },
    {
        id: 6,
        title: '蒜蓉清炒時蔬',
        description: '保持蔬菜的原汁原味，清爽健康，是餐桌上不可或缺的平衡菜。',
        difficulty: 2,
        cooking_time: 8,
        step: '1. 清洗備料：將所選的時令蔬菜（如：菠菜、空心菜、油菜等）清洗乾��淨，瀝乾水分，大蒜切成細蓉備用。\n\n2. 爆香蒜蓉：鍋中倒入較多的油，大火燒熱後，將一半的蒜蓉倒入，快速爆香，注意不要炒焦。\n\n3. 快速翻炒：將蔬菜倒入鍋中，大火快速翻炒，這樣可以最大限度地保留蔬菜的翠綠色和清脆口感，如果蔬菜不易熟可適當加蓋燜煮片刻。\n\n4. 調味出鍋：在蔬菜接近斷生時，加入適量的鹽和少許雞精調味，如果喜歡更濃的蒜味，此時可加入剩下的生蒜蓉。\n\n5. 均勻翻動：快速翻炒均勻後，即可盛盤上桌，追求速度是清炒時蔬的關鍵。',
        creator_id: 'system',
        is_public: true,
        image_url: '/Veg.webp',
        ingredients: [
            { ingredient_id: 17, ingredient_name: '時令蔬菜', quantity: 500, unit: '克', category: 'vegetable' },
            { ingredient_id: 18, ingredient_name: '大蒜', quantity: 3, unit: '瓣', category: 'vegetable'},
            { ingredient_id: 19, ingredient_name: '鹽', quantity: 1, unit: '茶匙', category: 'seasoning'},
        ],
    },
    {
        id: 7,
        title: '可樂雞翅',
        description: '深受小朋友喜愛的甜口菜，雞翅軟嫩入味，帶著淡淡的可樂焦糖香。',
        difficulty: 3,
        cooking_time: 30,
        step: '1. 雞翅處理：將雞中翅清洗乾淨，用刀在表面劃兩刀方便入味，加入薑片、料酒和少量生抽醃製15分鐘。\n\n2. 煸炒上色：鍋中放少量油，將雞翅放入，中小火煎至兩面金黃微焦，逼出多餘油脂後盛出。\n\n3. 加入可樂：將煎好的雞翅重新放回鍋中，加入生抽和老抽調色，然後倒入整罐可樂，水量需沒過雞翅。\n\n4. 小火慢燉：大火煮沸後轉小火，蓋上鍋蓋燜煮約20分鐘，讓雞翅充分吸收可樂的甜味和焦糖色。\n\n5. 大火收汁：開大火將湯汁收濃，期間要不斷翻動雞翅防止粘鍋，待湯汁變得濃稠且均勻包裹住雞翅後即可關火盛盤。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 20, ingredient_name: '雞中翅', quantity: 8, unit: '個', category: 'meat'},
            { ingredient_id: 21, ingredient_name: '可樂', quantity: 300, unit: '毫升', category: 'other' },
            { ingredient_id: 22, ingredient_name: '醬油', quantity: 2, unit: '匙', category: 'seasoning' },
        ],
    },
    {
        id: 8,
        title: '清蒸魚',
        description: '粵菜的代表作，最大限度保留魚肉的鮮美和嫩滑，清淡健康。',
        difficulty: 4,
        cooking_time: 15,
        step: '1. 魚身處理：將魚（如鱸魚、石斑魚）內臟去除洗淨，魚身劃幾刀，用鹽和料酒輕輕塗抹，魚肚和魚身上放上蔥薑絲去腥。\n\n2. 準備蒸鍋：蒸鍋中水燒開，將魚放在墊有蔥薑段的盤子上，確保魚身與盤底有空隙，利於蒸汽循環。\n\n3. 控制火候：大火蒸約8-10分鐘（根據魚的大小調整時間），魚眼突出、魚肉剛好離骨即為成熟。\n\n4. 淋醬油：將蒸魚盤中多餘的湯汁倒掉（腥味來源），夾去魚身上的蔥薑絲，重新鋪上新的蔥絲和香菜。\n\n5. 熱油激香：將適量的蒸魚豉油淋在魚身上，隨後燒熱少許油，將熱油均勻地潑在蔥絲上，激發出香氣即可上桌。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 23, ingredient_name: '新鮮活魚', quantity: 500, unit: '克', category: 'seafood' },
            { ingredient_id: 24, ingredient_name: '蔥薑絲', quantity: 50, unit: '克', category: 'seasoning' },
            { ingredient_id: 25, ingredient_name: '蒸魚豉油', quantity: 3, unit: '匙', category: 'seasoning' },
        ],
    },
    {
        id: 9,
        title: '酸辣土豆絲',
        description: '極具人氣的家常菜，酸爽開胃，土豆絲清脆爽口。',
        difficulty: 2,
        cooking_time: 10,
        step: '1. 切絲浸泡：將土豆去皮後切成極細的絲，放入清水中反覆沖洗或浸泡，直到水變清澈，目的是洗去多餘的澱粉，保證炒出來的口感脆爽。\n\n2. 準備調料：將乾辣椒切段，蒜切末備用，醋、鹽、少量糖調和成汁。\n\n3. 爆炒調料：鍋中燒熱油，先放入乾辣椒段和花椒粒小火煸炒出香，隨後加入蒜末爆香。\n\n4. 大火快炒：將瀝乾的土豆絲倒入鍋中，轉大火快速翻炒，加入少量白醋和鹽調味。\n\n5. 醋香收尾：在土豆絲快要炒熟時，沿鍋邊淋入大量的醋，快速翻炒幾下即可出鍋，保持其酸味和清脆的口感。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 26, ingredient_name: '土豆', quantity: 2, unit: '個', category: 'vegetable'},
            { ingredient_id: 27, ingredient_name: '白醋', quantity: 3, unit: '匙', category: 'seasoning' },
            { ingredient_id: 28, ingredient_name: '乾辣椒', quantity: 5, unit: '個', category: 'seasoning' },
        ],
    },
    {
        id: 10,
        title: '木須肉',
        description: '傳統京菜，雞蛋嫩黃，木耳爽脆，肉片滑嫩，色彩豐富，營養均衡。',
        difficulty: 4,
        cooking_time: 20,
        step: '1. 滑炒雞蛋：雞蛋打散後，鍋中放油燒熱，將蛋液倒入，快速滑炒成大塊的雞蛋碎，盛出備用。\n\n2. 醃製肉片：將豬肉切成薄片，用鹽、料酒和澱粉抓勻醃製10分鐘。\n\n3. 炒肉片：鍋中放油，將醃好的肉片倒入，快速滑炒至變色即刻盛出，保持肉片的嫩度。\n\n4. 炒配料：利用鍋中底油，放入蔥段、薑絲爆香，隨後加入泡發好的木耳和黃瓜片或筍片，大火翻炒。\n\n5. 混合調味：將炒好的雞蛋和肉片重新倒入鍋中，加入生抽、少許鹽和糖調味，快速翻炒均勻，淋入少許水澱粉勾薄芡即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 29, ingredient_name: '豬肉片', quantity: 150, unit: '克', category: 'meat' },
            { ingredient_id: 30, ingredient_name: '雞蛋', quantity: 2, unit: '個', category: 'egg' },
            { ingredient_id: 31, ingredient_name: '木耳/黃瓜', quantity: 100, unit: '克', category: 'vegetable' },
        ],
    },
    {
        id: 11,
        title: '醬爆肉片',
        description: '濃郁的醬香味，肉片軟嫩，是道非常下飯的北方家常菜。',
        difficulty: 4,
        cooking_time: 18,
        step: '1. 肉片處理：將豬後腿肉或里脊肉切成薄片，加入料酒、生抽、少許澱粉抓勻醃製10分鐘。\n\n2. 調製醬汁：用甜麵醬、黃豆醬、少許糖和少量水混合均勻，製成濃郁的醬汁。\n\n3. 滑炒肉片：鍋中熱油，將醃好的肉片倒入，快速滑炒至變色後盛出備用。\n\n4. 爆香醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入調好的醬汁，小火慢慢炒出醬香和光澤。\n\n5. 混合收汁：將肉片和切好的青椒或洋蔥片倒入鍋中，快速大火翻炒，讓濃稠的醬汁均勻地裹在肉片和蔬菜上，撒上蔥花即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 32, ingredient_name: '豬後腿肉', quantity: 250, unit: '克', category: 'meat' },
            { ingredient_id: 33, ingredient_name: '甜麵醬/黃豆醬', quantity: 3, unit: '匙', category: 'seasoning' },
            { ingredient_id: 34, ingredient_name: '青椒', quantity: 1, unit: '個', category: 'vegetable' },
        ],
    },
    {
        id: 12,
        title: '京醬肉絲',
        description: '北京風味名菜，肉絲細嫩，醬香濃郁，搭配豆腐皮和蔥絲食用。',
        difficulty: 5,
        cooking_time: 20,
        step: '1. 肉絲醃製：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。\n\n2. 滑炒肉絲：鍋中放油，將肉絲倒入快速滑炒至變色，立刻盛出，保持肉絲的嫩度。\n\n3. 炒醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入甜麵醬（可加入少許糖和水稀釋），小火慢慢炒出醬香味。\n\n4. 入味收汁：將炒好的肉絲重新倒入鍋中，轉大火快速翻炒，讓濃稠的醬汁均勻地裹在每根肉絲上。\n\n5. 搭配擺盤：將肉絲盛出放在盤中，旁邊配上切好的蔥白絲和黃瓜絲，搭配薄薄的豆腐皮或春餅捲著吃。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 35, ingredient_name: '豬里脊肉', quantity: 250, unit: '克', category: 'meat' },
            { ingredient_id: 36, ingredient_name: '甜麵醬', quantity: 4, unit: '匙', category: 'seasoning' },
            { ingredient_id: 37, ingredient_name: '大蔥白', quantity: 1, unit: '根', category: 'vegetable' },
        ],
    },
]

export const useRecipeStore = defineStore('recipe', () => {
  // State
  const recipes = ref([...mockRecipes])

  // Getters
  const recipeCount = computed(() => recipes.value.length)

  const allRecipes = computed(() => recipes.value)

  // Actions
  function getRecipeById(id) {
    return recipes.value.find((recipe) => recipe.id === id)
  }

  function getRecipesByFilters(filters) {
    let filtered = [...recipes.value]

    if (filters.title) {
      filtered = filtered.filter((recipe) =>
        recipe.title.toLowerCase().includes(filters.title.toLowerCase())
      )
    }

    if (filters.difficulty) {
      filtered = filtered.filter((recipe) => recipe.difficulty === filters.difficulty)
    }

    if (filters.maxCookingTime) {
      filtered = filtered.filter((recipe) => recipe.cooking_time <= filters.maxCookingTime)
    }

    if (filters.category) {
      filtered = filtered.filter((recipe) => recipe.category === filters.category)
    }

    return filtered
  }

  function searchRecipes(query) {
    if (!query.trim()) {
      return recipes.value
    }

    const lowerQuery = query.toLowerCase()
    return recipes.value.filter((recipe) =>
      recipe.title.toLowerCase().includes(lowerQuery) ||
      recipe.description.toLowerCase().includes(lowerQuery)
    )
  }

  function getRecipesByDifficulty(difficulty) {
    return recipes.value.filter((recipe) => recipe.difficulty === difficulty)
  }

  function getRecipesByCookingTime(maxMinutes) {
    return recipes.value.filter((recipe) => recipe.cooking_time <= maxMinutes)
  }

  return {
    // State
    recipes,
    // Getters
    recipeCount,
    allRecipes,
    // Actions
    getRecipeById,
    getRecipesByFilters,
    searchRecipes,
    getRecipesByDifficulty,
    getRecipesByCookingTime
  }
})