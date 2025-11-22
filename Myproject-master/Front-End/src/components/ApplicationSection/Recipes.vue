<script setup>
import { ref, computed, defineEmits, defineProps } from 'vue'
const emit = defineEmits(['gotorecipedetail'])
const props = defineProps({ 
  fridgeItems: Array
})

const mockRecipes = [
    {
        id: 1,
        title: '番茄炒蛋',
        description: '簡單快手菜，營養豐富，酸甜開胃的家常經典。',
        difficulty: 3,
        coocking_time: 12,
        step: '1. 打蛋：將雞蛋打入碗中，加入少許鹽和幾滴水（讓蛋更嫩滑），用筷子充分攪打均勻，直至蛋液起泡。\n\n2. 炒蛋：鍋中倒入適量的油，燒熱後將蛋液倒入，快速用鏟子劃散，炒至半熟且邊緣微焦時盛出備用。\n\n3. 加番茄：利用鍋中餘油，將切好的番茄塊倒入，加入少許鹽和糖（糖的量可根據番茄的酸度調整），中小火翻炒至番茄出汁變軟。\n\n4. 合體：將炒好的雞蛋重新倒入鍋中，與番茄汁快速翻炒均勻，讓雞蛋充分吸收番茄的酸甜味，可以淋入少許水澱粉勾芡使湯汁濃稠，最後撒上蔥花即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 4, ingredient_name: '番茄', quantity: 2, unit: '個' },
            { ingredient_id: 3, ingredient_name: '雞蛋', quantity: 3, unit: '個' },
            { ingredient_id: 101, ingredient_name: '蔥花', quantity: 1, unit: '匙' },
        ],
    },
    {
        id: 2,
        title: '紅燒肉',
        description: '經典濃油赤醬的本幫菜，肥而不膩，入口即化，色澤紅亮誘人。',
        difficulty: 5,
        coocking_time: 90,
        step: '1. 汆燙：將五花肉切成麻將大小的塊狀，冷水下鍋，加入薑片和料酒，大火煮沸後撇去浮沫，撈出洗淨瀝乾。\n\n2.  炒糖色：鍋中放少許油，加入冰糖或白砂糖，小火慢慢熬煮至糖融化變成琥珀色，注意不要炒焦。\n\n3. 上色：將五花肉塊倒入鍋中，快速翻炒，讓每塊肉都均勻裹上糖色，隨後加入薑片、蔥段、八角和桂皮等香料。\n\n4. 慢燉入味：倒入足量的熱水（水量需沒過肉塊）、老抽和生抽調味，大火煮沸後轉最小火，蓋上鍋蓋燜煮60-90分鐘，直到肉質軟爛。\n\n5. 收汁：待肉軟爛後，開大火將湯汁收濃，期間要不斷翻動防止粘鍋，讓湯汁緊密地包裹在肉塊上，呈現出油光發亮的狀態即可。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 5, ingredient_name: '五花肉', quantity: 500, unit: '克' },
            { ingredient_id: 6, ingredient_name: '冰糖', quantity: 30, unit: '克' },
            { ingredient_id: 7, ingredient_name: '醬油（老抽/生抽）', quantity: 3, unit: '匙' },
        ],
    },
    {
        id: 3,
        title: '宮保雞丁',
        description: '川菜經典名菜，雞肉鮮嫩，花生米酥脆，味道鹹甜酸辣適中，層次豐富。',
        difficulty: 5,
        coocking_time: 20,
        step: '1. 醃製雞丁：將雞胸肉或雞腿肉切成小丁，加入少許鹽、料酒、蛋清和澱粉，抓勻後醃製10分鐘備用。\n\n2. 調製醬汁：將醋、糖、生抽、老抽、料酒、水澱粉和少量清水混合，攪拌均勻製成宮保汁。\n\n3. 炒香配料：鍋中放油燒熱，先將乾辣椒段和花椒粒小火煸炒出香味，隨後放入蔥段和薑片爆香。\n\n4. 滑炒雞丁：將醃好的雞丁倒入鍋中，快速滑炒至變色，然後加入事先炸酥的花生米。\n\n5. 淋醬收尾：將調好的宮保汁沿鍋邊倒入，迅速翻炒，讓醬汁均勻地包裹住雞丁和花生米，湯汁變得濃稠後即可出鍋，保持雞丁的嫩滑和花生米的酥脆。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 8, ingredient_name: '雞胸肉', quantity: 300, unit: '克' },
            { ingredient_id: 9, ingredient_name: '花生米', quantity: 50, unit: '克' },
            { ingredient_id: 10, ingredient_name: '乾辣椒/花椒', quantity: 10, unit: '克' },
        ],
    },
    {
        id: 4,
        title: '麻婆豆腐',
        description: '著名的川菜，麻、辣、燙、嫩、酥、香、鮮，口感和風味極具特色。',
        difficulty: 4,
        coocking_time: 15,
        step: '1. 準備豆腐：將嫩豆腐切成約2公分見方的小塊，放入加了少許鹽的熱水中輕輕汆燙1分鐘，撈出瀝水，這能讓豆腐不易碎。\n\n2. 炒香調料：鍋中放油燒熱，先放入牛肉末或豬肉末煸炒至變色，接著加入豆瓣醬和豆豉，小火炒出紅油和香氣。\n\n3. 下豆腐：將切好的豆腐塊輕輕倒入鍋中，加入適量的水或高湯，轉中小火慢煮入味。\n\n4. 調味收汁：加入生抽、少許糖調味，待湯汁稍微收乾時，勾入適量的水澱粉使湯汁變得濃稠。\n\n5. 撒料出鍋：最後撒上大量的花椒粉（這是「麻」的關鍵）和蔥花，輕輕推勻後即可出鍋，趁熱食用風味最佳。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 11, ingredient_name: '嫩豆腐', quantity: 1, unit: '塊' },
            { ingredient_id: 12, ingredient_name: '牛肉/豬肉末', quantity: 100, unit: '克' },
            { ingredient_id: 13, ingredient_name: '豆瓣醬', quantity: 2, unit: '匙' },
        ],
    },
    {
        id: 5,
        title: '魚香肉絲',
        description: '魚香是川菜獨特的複合味，鹹甜酸辣兼具，醬汁濃郁，拌飯一絕。',
        difficulty: 5,
        coocking_time: 25,
        step: '1. 醃製肉絲：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。\n\n2. 調製魚香汁：將醋、糖、生抽、老抽、水澱粉和清水按比例混合均勻，製成魚香汁。\n\n3. 滑炒肉絲：鍋中熱油，將醃好的肉絲快速滑炒至變色即刻盛出，保持肉絲的嫩度。\n\n4. 炒香配料：利用底油，先後放入薑末、蒜末、泡椒末和剁碎的豆瓣醬，小火炒出紅油和香氣。\n\n5. 混合收汁：將肉絲和切好的木耳絲、筍絲等配料倒入鍋中，快速翻炒，隨後倒入魚香汁，大火翻炒，讓醬汁均勻地裹在食材上，湯汁變得濃稠發亮後即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 14, ingredient_name: '豬里脊肉', quantity: 250, unit: '克' },
            { ingredient_id: 15, ingredient_name: '木耳/筍絲', quantity: 100, unit: '克' },
            { ingredient_id: 16, ingredient_name: '泡椒/豆瓣醬', quantity: 1, unit: '匙' },
        ],
    },
    {
        id: 6,
        title: '蒜蓉清炒時蔬',
        description: '保持蔬菜的原汁原味，清爽健康，是餐桌上不可或缺的平衡菜。',
        difficulty: 2,
        coocking_time: 8,
        step: '1. 清洗備料：將所選的時令蔬菜（如：菠菜、空心菜、油菜等）清洗乾淨，瀝乾水分，大蒜切成細蓉備用。\n\n2. 爆香蒜蓉：鍋中倒入較多的油，大火燒熱後，將一半的蒜蓉倒入，快速爆香，注意不要炒焦。\n\n3. 快速翻炒：將蔬菜倒入鍋中，大火快速翻炒，這樣可以最大限度地保留蔬菜的翠綠色和清脆口感，如果蔬菜不易熟可適當加蓋燜煮片刻。\n\n4. 調味出鍋：在蔬菜接近斷生時，加入適量的鹽和少許雞精調味，如果喜歡更濃的蒜味，此時可加入剩下的生蒜蓉。\n\n5. 均勻翻動：快速翻炒均勻後，即可盛盤上桌，追求速度是清炒時蔬的關鍵。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 17, ingredient_name: '時令蔬菜', quantity: 500, unit: '克' },
            { ingredient_id: 18, ingredient_name: '大蒜', quantity: 3, unit: '瓣' },
            { ingredient_id: 19, ingredient_name: '鹽', quantity: 1, unit: '茶匙' },
        ],
    },
    {
        id: 7,
        title: '可樂雞翅',
        description: '深受小朋友喜愛的甜口菜，雞翅軟嫩入味，帶著淡淡的可樂焦糖香。',
        difficulty: 3,
        coocking_time: 30,
        step: '1. 雞翅處理：將雞中翅清洗乾淨，用刀在表面劃兩刀方便入味，加入薑片、料酒和少量生抽醃製15分鐘。\n\n2. 煸炒上色：鍋中放少量油，將雞翅放入，中小火煎至兩面金黃微焦，逼出多餘油脂後盛出。\n\n3. 加入可樂：將煎好的雞翅重新放回鍋中，加入生抽和老抽調色，然後倒入整罐可樂，水量需沒過雞翅。\n\n4. 小火慢燉：大火煮沸後轉小火，蓋上鍋蓋燜煮約20分鐘，讓雞翅充分吸收可樂的甜味和焦糖色。\n\n5. 大火收汁：開大火將湯汁收濃，期間要不斷翻動雞翅防止粘鍋，待湯汁變得濃稠且均勻包裹住雞翅後即可關火盛盤。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 20, ingredient_name: '雞中翅', quantity: 8, unit: '個' },
            { ingredient_id: 21, ingredient_name: '可樂', quantity: 300, unit: '毫升' },
            { ingredient_id: 22, ingredient_name: '醬油', quantity: 2, unit: '匙' },
        ],
    },
    {
        id: 8,
        title: '清蒸魚',
        description: '粵菜的代表作，最大限度保留魚肉的鮮美和嫩滑，清淡健康。',
        difficulty: 4,
        coocking_time: 15,
        step: '1. 魚身處理：將魚（如鱸魚、石斑魚）內臟去除洗淨，魚身劃幾刀，用鹽和料酒輕輕塗抹，魚肚和魚身上放上蔥薑絲去腥。\n\n2. 準備蒸鍋：蒸鍋中水燒開，將魚放在墊有蔥薑段的盤子上，確保魚身與盤底有空隙，利於蒸汽循環。\n\n3. 控制火候：大火蒸約8-10分鐘（根據魚的大小調整時間），魚眼突出、魚肉剛好離骨即為成熟。\n\n4. 淋醬油：將蒸魚盤中多餘的湯汁倒掉（腥味來源），夾去魚身上的蔥薑絲，重新鋪上新的蔥絲和香菜。\n\n5. 熱油激香：將適量的蒸魚豉油淋在魚身上，隨後燒熱少許油，將熱油均勻地潑在蔥絲上，激發出香氣即可上桌。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 23, ingredient_name: '新鮮活魚', quantity: 500, unit: '克' },
            { ingredient_id: 24, ingredient_name: '蔥薑絲', quantity: 50, unit: '克' },
            { ingredient_id: 25, ingredient_name: '蒸魚豉油', quantity: 3, unit: '匙' },
        ],
    },
    {
        id: 9,
        title: '酸辣土豆絲',
        description: '極具人氣的家常菜，酸爽開胃，土豆絲清脆爽口。',
        difficulty: 2,
        coocking_time: 10,
        step: '1. 切絲浸泡：將土豆去皮後切成極細的絲，放入清水中反覆沖洗或浸泡，直到水變清澈，目的是洗去多餘的澱粉，保證炒出來的口感脆爽。\n\n2. 準備調料：將乾辣椒切段，蒜切末備用，醋、鹽、少量糖調和成汁。\n\n3. 爆炒調料：鍋中燒熱油，先放入乾辣椒段和花椒粒小火煸炒出香，隨後加入蒜末爆香。\n\n4. 大火快炒：將瀝乾的土豆絲倒入鍋中，轉大火快速翻炒，加入少量白醋和鹽調味。\n\n5. 醋香收尾：在土豆絲快要炒熟時，沿鍋邊淋入大量的醋，快速翻炒幾下即可出鍋，保持其酸味和清脆的口感。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 26, ingredient_name: '土豆', quantity: 2, unit: '個' },
            { ingredient_id: 27, ingredient_name: '白醋', quantity: 3, unit: '匙' },
            { ingredient_id: 28, ingredient_name: '乾辣椒', quantity: 5, unit: '個' },
        ],
    },
    {
        id: 10,
        title: '木須肉',
        description: '傳統京菜，雞蛋嫩黃，木耳爽脆，肉片滑嫩，色彩豐富，營養均衡。',
        difficulty: 4,
        coocking_time: 20,
        step: '1. 滑炒雞蛋：雞蛋打散後，鍋中放油燒熱，將蛋液倒入，快速滑炒成大塊的雞蛋碎，盛出備用。\n\n2. 醃製肉片：將豬肉切成薄片，用鹽、料酒和澱粉抓勻醃製10分鐘。\n\n3. 炒肉片：鍋中放油，將醃好的肉片倒入，快速滑炒至變色即刻盛出，保持肉片的嫩度。\n\n4. 炒配料：利用鍋中底油，放入蔥段、薑絲爆香，隨後加入泡發好的木耳和黃瓜片或筍片，大火翻炒。\n\n5. 混合調味：將炒好的雞蛋和肉片重新倒入鍋中，加入生抽、少許鹽和糖調味，快速翻炒均勻，淋入少許水澱粉勾薄芡即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 29, ingredient_name: '豬肉片', quantity: 150, unit: '克' },
            { ingredient_id: 30, ingredient_name: '雞蛋', quantity: 2, unit: '個' },
            { ingredient_id: 31, ingredient_name: '木耳/黃瓜', quantity: 100, unit: '克' },
        ],
    },
    {
        id: 11,
        title: '醬爆肉片',
        description: '濃郁的醬香味，肉片軟嫩，是道非常下飯的北方家常菜。',
        difficulty: 4,
        coocking_time: 18,
        step: '1. 肉片處理：將豬後腿肉或里脊肉切成薄片，加入料酒、生抽、少許澱粉抓勻醃製10分鐘。\n\n2. 調製醬汁：用甜麵醬、黃豆醬、少許糖和少量水混合均勻，製成濃郁的醬汁。\n\n3. 滑炒肉片：鍋中熱油，將醃好的肉片倒入，快速滑炒至變色後盛出備用。\n\n4. 爆香醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入調好的醬汁，小火慢慢炒出醬香和光澤。\n\n5. 混合收汁：將肉片和切好的青椒或洋蔥片倒入鍋中，快速大火翻炒，讓濃稠的醬汁均勻地裹在肉片和蔬菜上，撒上蔥花即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 32, ingredient_name: '豬後腿肉', quantity: 250, unit: '克' },
            { ingredient_id: 33, ingredient_name: '甜麵醬/黃豆醬', quantity: 3, unit: '匙' },
            { ingredient_id: 34, ingredient_name: '青椒', quantity: 1, unit: '個' },
        ],
    },
    {
        id: 12,
        title: '京醬肉絲',
        description: '北京風味名菜，肉絲細嫩，醬香濃郁，搭配豆腐皮和蔥絲食用。',
        difficulty: 5,
        coocking_time: 20,
        step: '1. 肉絲醃製：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。\n\n2. 滑炒肉絲：鍋中放油，將肉絲倒入快速滑炒至變色，立刻盛出，保持肉絲的嫩度。\n\n3. 炒醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入甜麵醬（可加入少許糖和水稀釋），小火慢慢炒出醬香味。\n\n4. 入味收汁：將炒好的肉絲重新倒入鍋中，轉大火快速翻炒，讓濃稠的醬汁均勻地裹在每根肉絲上。\n\n5. 搭配擺盤：將肉絲盛出放在盤中，旁邊配上切好的蔥白絲和黃瓜絲，搭配薄薄的豆腐皮或春餅捲著吃。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 35, ingredient_name: '豬里脊肉', quantity: 250, unit: '克' },
            { ingredient_id: 36, ingredient_name: '甜麵醬', quantity: 4, unit: '匙' },
            { ingredient_id: 37, ingredient_name: '大蔥白', quantity: 1, unit: '根' },
        ],
    },
    {
        id: 13,
        title: '地三鮮',
        description: '東北名菜，茄子軟糯，土豆香甜，青椒清脆，三種蔬菜風味完美結合。',
        difficulty: 4,
        coocking_time: 20,
        step: '1. 油炸處理：將土豆、茄子和青椒分別切塊。先將土豆塊放入熱油中炸至金黃軟熟，撈出；接著炸茄子塊至變軟上色，撈出；最後將青椒塊稍微過油即可。\n\n2. 調製醬汁：用生抽、老抽、糖、鹽和水澱粉混合均勻，製成濃稠的醬汁。\n\n3. 爆香：鍋中留少量底油，放入蒜末爆香。\n\n4. 混合翻炒：將炸好的三種蔬菜一同倒入鍋中，快速翻炒。\n\n5. 淋醬收汁：倒入調好的醬汁，大火快速翻炒，讓醬汁均勻包裹在所有食材上，湯汁收濃即可關火出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 38, ingredient_name: '土豆', quantity: 1, unit: '個' },
            { ingredient_id: 39, ingredient_name: '茄子', quantity: 1, unit: '條' },
            { ingredient_id: 40, ingredient_name: '青椒', quantity: 1, unit: '個' },
        ],
    },
    {
        id: 14,
        title: '白灼蝦',
        description: '粵菜中常見的烹飪手法，最大限度地保留食材的原汁原味和鮮甜。',
        difficulty: 2,
        coocking_time: 10,
        step: '1. 處理鮮蝦：將活蝦或鮮蝦清洗乾淨，剪去蝦鬚，挑去蝦線（可省略），確保蝦身無雜質。\n\n2. 準備灼水：鍋中倒入足量的清水，加入薑片、蔥段和少許料酒，大火煮沸，這能有效去除蝦的腥味。\n\n3. 快速灼燙：將蝦倒入沸水中，轉中小火，煮約2-3分鐘，看到蝦身捲曲、顏色變紅即可立即撈出，過度烹煮會導致蝦肉變老。\n\n4. 準備蘸料：將生抽、薑末、少許醋和麻油混合，製成簡單的蘸醬。\n\n5. 擺盤：將灼好的蝦瀝乾水分，整齊地擺放在盤中，搭配蘸醬食用，享受蝦肉本身的鮮甜Q彈。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 41, ingredient_name: '鮮蝦', quantity: 300, unit: '克' },
            { ingredient_id: 42, ingredient_name: '蔥薑料酒', quantity: 50, unit: '克' },
            { ingredient_id: 43, ingredient_name: '生抽', quantity: 2, unit: '匙' },
        ],
    },
    {
        id: 15,
        title: '揚州炒飯',
        description: '經典淮揚菜，米飯粒粒分明，配料豐富，色、香、味俱全。',
        difficulty: 3,
        coocking_time: 20,
        step: '1. 準備配料：將火腿丁、蝦仁、雞丁、胡蘿蔔丁、玉米粒等配料切好，雞蛋打散備用，最好使用隔夜飯。\n\n2. 炒雞蛋：鍋中放油燒熱，倒入蛋液快速炒成雞蛋碎，盛出備用。\n\n3. 炒香配料：利用底油，先放入蝦仁、雞丁等不易熟的配料炒熟，再加入火腿丁、胡蘿蔔丁和玉米粒翻炒均勻。\n\n4. 下米飯：將隔夜飯倒入鍋中，轉大火快速翻炒，將米飯撥散，炒至粒粒分明。\n\n5. 混合調味：加入炒好的雞蛋碎和所有配料，撒入鹽和少許胡椒粉調味，快速翻炒均勻，最後撒上蔥花即可出鍋。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 44, ingredient_name: '隔夜飯', quantity: 300, unit: '克' },
            { ingredient_id: 45, ingredient_name: '雞蛋', quantity: 2, unit: '個' },
            { ingredient_id: 46, ingredient_name: '蝦仁/火腿/蔬菜丁', quantity: 150, unit: '克' },
        ],
    },
    {
        id: 16,
        title: '糖醋排骨',
        description: '酸甜口的江浙菜代表，排骨色澤紅亮，外酥內嫩，酸甜適口。',
        difficulty: 5,
        coocking_time: 45,
        step: '1. 排骨處理：將排骨切塊，冷水下鍋，加入薑片和料酒汆燙，煮沸後撇去浮沫，撈出洗淨瀝乾。\n\n2. 油炸鎖汁：排骨瀝乾後，放入燒熱的油鍋中，炸至表面金黃微焦，撈出瀝油。\n\n3. 調製糖醋汁：按照1:2:3:4:5的比例（酒:醋:糖:醬油:水）調製糖醋汁，這是萬能比例。\n\n4. 燒煮入味：鍋中留底油，倒入排骨，隨即倒入調好的糖醋汁，大火煮沸後轉小火，蓋上鍋蓋燜煮30分鐘。\n\n5. 大火收汁：待排骨軟爛後，開大火將湯汁收濃，湯汁會變得濃稠且呈現焦糖色，均勻地包裹在排骨上，最後撒上白芝麻即可。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 47, ingredient_name: '豬肋排', quantity: 500, unit: '克' },
            { ingredient_id: 48, ingredient_name: '陳醋', quantity: 3, unit: '匙' },
            { ingredient_id: 49, ingredient_name: '白砂糖', quantity: 4, unit: '匙' },
        ],
    },
    {
        id: 17,
        title: '麻油雞',
        description: '台灣或客家傳統滋補湯品，酒香麻油香濃郁，雞肉鮮嫩，溫暖滋補。',
        difficulty: 3,
        coocking_time: 40,
        step: '1. 炒香薑片：鍋中倒入麻油，用中小火慢慢煸炒老薑片，直到薑片邊緣捲曲、顏色變深，但不要炒焦，讓麻油充分吸收薑的香氣。\n\n2. 炒雞肉：將切塊的雞肉（建議用雞腿肉）倒入鍋中，轉大火快速翻炒至雞肉變色，鎖住肉汁。\n\n3. 米酒入味：倒入全米酒（或米酒與水1:1混合），水量需淹沒雞肉，大火煮沸，讓酒精揮發。\n\n4. 燜煮入味：煮沸後轉小火，蓋上鍋蓋燜煮20-30分鐘，直到雞肉軟爛入味。\n\n5. 調味：最後加入少許鹽調味即可。這道菜的重點在於米酒的香氣和麻油的溫潤，適合冬天或產後滋補。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 50, ingredient_name: '帶骨雞肉塊', quantity: 500, unit: '克' },
            { ingredient_id: 51, ingredient_name: '老薑片', quantity: 10, unit: '片' },
            { ingredient_id: 52, ingredient_name: '麻油', quantity: 50, unit: '毫升' },
            { ingredient_id: 53, ingredient_name: '米酒', quantity: 500, unit: '毫升' },
        ],
    },
    {
        id: 18,
        title: '蔥油拌麵',
        description: '上海經典小吃，麵條滑順，蔥油香氣濃郁，製作簡單卻回味無窮。',
        difficulty: 2,
        coocking_time: 15,
        step: '1. 製作蔥油：鍋中倒入多量油，放入切好的蔥白和蔥綠（蔥白多一點），小火慢熬，直到蔥段變黃變乾，將蔥段撈出備用，製成的油即為蔥油。\n\n2. 調製醬汁：在蔥油中加入生抽、老抽和少許糖，小火加熱至糖融化，製成蔥油醬汁。\n\n3. 煮麵：水燒開後，放入麵條煮熟，煮好後立即撈出，瀝乾水分。\n\n4. 拌麵：將煮好的麵條放入碗中，淋上適量的蔥油醬汁，再放上炸好的蔥段。\n\n5. 均勻攪拌：迅速將麵條和醬汁攪拌均勻，讓麵條完全裹上濃郁的蔥油和醬汁，趁熱食用風味最佳。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 54, ingredient_name: '麵條', quantity: 200, unit: '克' },
            { ingredient_id: 55, ingredient_name: '小蔥/大蔥', quantity: 1, unit: '把' },
            { ingredient_id: 56, ingredient_name: '醬油', quantity: 3, unit: '匙' },
        ],
    },
    {
        id: 19,
        title: '蒜蓉粉絲蒸扇貝',
        description: '海鮮與蒜蓉的完美結合，粉絲吸收了鮮美的湯汁，口感豐富。',
        difficulty: 4,
        coocking_time: 15,
        step: '1. 處理扇貝：將新鮮扇貝打開，取出肉清洗乾淨，只保留半邊殼，粉絲用溫水泡軟。\n\n2. 製作蒜蓉醬：大量大蒜切成蓉，一半用熱油炒至金黃（熟蒜蓉），一半保持生蒜蓉，將兩者混合，加入生抽、鹽和蠔油調味。\n\n3. 鋪盤：在扇貝殼上先鋪一層泡軟的粉絲，再放上扇貝肉，最後在扇貝肉上鋪滿調好的蒜蓉醬。\n\n4. 蒸煮：將扇貝放入燒開水的蒸鍋中，大火蒸約6-8分鐘，時間不宜過長，以保持扇貝肉的鮮嫩。\n\n5. 熱油激香：出鍋後，在每個扇貝上撒上蔥花，燒熱少許油，將熱油均勻地潑在蔥花上，激發出香氣即可。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 57, ingredient_name: '新鮮扇貝', quantity: 6, unit: '個' },
            { ingredient_id: 58, ingredient_name: '粉絲', quantity: 50, unit: '克' },
            { ingredient_id: 59, ingredient_name: '大蒜', quantity: 1, unit: '頭' },
        ],
    },
    {
        id: 20,
        title: '水煮肉片',
        description: '川菜經典，麻辣鮮香，肉片滑嫩，湯汁紅亮，是超級下飯的重口味菜。',
        difficulty: 5,
        coocking_time: 30,
        step: '1. 肉片醃製：將豬里脊肉切成大薄片，加入鹽、料酒、蛋清和大量澱粉抓勻，再加入少許油鎖住水分，醃製15分鐘。\n\n2. 準備底菜：將豆芽、大白菜等蔬菜放入鍋中用熱水或少量油炒熟，鋪在大碗底部備用。\n\n3. 炒底料：鍋中熱油，放入豆瓣醬、乾辣椒段和花椒，小火炒出紅油和香氣，加入適量清水或高湯煮沸。\n\n4. 滑肉片：將湯中的辣椒段和花椒撈出（可保留部分），轉小火，將醃好的肉片一片一片放入湯中，煮至肉片變色即可關火。\n\n5. 潑熱油：將肉片和湯汁一起倒入鋪好底菜的大碗中，在肉片上鋪滿蒜末、辣椒粉和花椒粒，最後燒熱大量的油，將熱油均勻地潑在調料上，激發出麻辣香氣即可。',
        creator_id: 'system',
        is_public: true,
        image_url: '/TomatoEgg.webp',
        ingredients: [
            { ingredient_id: 60, ingredient_name: '豬里脊肉', quantity: 300, unit: '克' },
            { ingredient_id: 61, ingredient_name: '豆瓣醬', quantity: 2, unit: '匙' },
            { ingredient_id: 62, ingredient_name: '豆芽/白菜', quantity: 200, unit: '克' },
            { ingredient_id: 63, ingredient_name: '乾辣椒/花椒', quantity: 20, unit: '克' },
        ],
    },
]

const recipes = ref(mockRecipes)
const filterByMyIngredient = ref(false) //要不要根據我的食材篩選
const selectByCookingTime = ref('all') //根據時長
const selectByDifficulty = ref('all') //根據時長

const fridgeIngredientMap = computed(() => {
  const map = {}
  if (props.fridgeItems && props.fridgeItems.length > 0) {
    props.fridgeItems.forEach((item) => {
      if (!map[item.ingredient_id]) {
        map[item.ingredient_id] = 0
      }
      map[item.ingredient_id] += item.quantity
    })
  }
  return map
})
function canMakeRecipe(recipe) {
  return recipe.ingredients.every((ingredient) => {
    const fridgeQuantity = fridgeIngredientMap.value[ingredient.ingredient_id] || 0
    return fridgeQuantity >= ingredient.quantity
  })
}
const filterRecipes = computed(() => {
    let result = recipes.value
  // 第一步：先按食材篩選
if (filterByMyIngredient.value) {
    // 如果冰箱是空的 (Map key 數量為 0)，直接回傳空陣列，不用再跑 filter
    if (Object.keys(fridgeIngredientMap.value).length === 0) {
      return []
    }
    result = result.filter((recipe) => canMakeRecipe(recipe))
  }

  // 第二步：再按時間篩選
  if (selectByCookingTime.value === 'all') {
    // 保持所有食譜
  } else if (selectByCookingTime.value === '15') {
    result = result.filter((recipe) => recipe.coocking_time <= 15)
  } else if (selectByCookingTime.value === '30') {
    result = result.filter((recipe) => recipe.coocking_time <= 30)
  } else if (selectByCookingTime.value === '60') {
    result = result.filter((recipe) => recipe.coocking_time >= 60)
  }
//第三步:困難度篩選
  if (selectByDifficulty.value === 'all') {
    // 保持所有食譜
  }
  else if (selectByDifficulty.value === 'easy') {
    result = result.filter((recipe) => recipe.difficulty == 1)
  } else if (selectByDifficulty.value === 'medium') {
    result = result.filter((recipe) => recipe.difficulty >= 2 && recipe.difficulty <= 3)
  } else if (selectByDifficulty.value === 'hard') {
    result = result.filter((recipe) => recipe.difficulty >= 4)
  } 
  return result
})

function handleCookRecipe(recipe) {
    emit('gotorecipedetail', recipe)
}

</script>
<template>
    <div class="mt-28 max-w-7xl mx-auto px-4">
        <div class="flex justify-center items-center"><h1 class="mb-8 text-4xl font-bold">想做些甚麼料理嗎!</h1></div>

        <div class="bg-white p-4 rounded-lg mb-4 shadow-md flex flex-col items-center justify-center space-y-6">
            <div>
                <h3 class="text-lg font-bold text-gray-500">食譜類型</h3>
            </div>

            <div class="flex gap-3">
                <button
                    @click="filterByMyIngredient = false"
                    :class="!filterByMyIngredient ? 'text-white bg-amber-500' : 'text-gray-200 bg-gray-700'"
                    class="rounded font-semibold px-3 py-2 cursor-pointer"
                >
                    所有食譜
                </button>
                <button
                    @click="filterByMyIngredient = true"
                    :class="filterByMyIngredient ? 'text-white bg-amber-500' : 'text-gray-200 bg-gray-700'"
                    class="rounded font-semibold px-3 py-2 cursor-pointer"
                >
                    根據我的食材
                </button>
            </div>

            <div>
                <h3 class="text-lg font-bold text-gray-500">烹飪時間</h3>
            </div>

            <div class="flex gap-2">
                <button
                    @click="selectByCookingTime = 'all'"
                    :class="selectByCookingTime == 'all' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    所有
                </button>
                <button
                    @click="selectByCookingTime = '15'"
                    :class="selectByCookingTime == '15' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    小於15分鐘
                </button>
                <button
                    @click="selectByCookingTime = '30'"
                    :class="selectByCookingTime == '30' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    小於30分鐘
                </button>
                <button
                    @click="selectByCookingTime = '60'"
                    :class="selectByCookingTime == '60' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    60分鐘以上
                </button>
            </div>

            <div>
                <h3 class="text-lg font-bold text-gray-500">難易程度</h3>
            </div>

            <div class="flex gap-2">
                <button
                    @click="selectByDifficulty = 'all'"
                    :class="selectByDifficulty == 'all' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    所有
                </button>
                <button
                    @click="selectByDifficulty = 'easy'"
                    :class="selectByDifficulty == 'easy' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    簡易
                </button>
                <button
                    @click="selectByDifficulty = 'medium'"
                    :class="selectByDifficulty == 'medium' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    中等
                </button>
                <button
                    @click="selectByDifficulty = 'hard'"
                    :class="selectByDifficulty == 'hard' ? 'text-white bg-amber-500' : 'text-gray-300 bg-gray-700'"
                    class="rounded font-semibold px-3 py-1 cursor-pointer"
                >
                    困難
                </button>
            </div>
        </div>

        <div class="flex flex-wrap gap-6 mt-8">
            <div
                v-for="recipe in filterRecipes"
                :key="recipe.id"
                class="bg-white rounded-lg overflow-hidden shadow-md w-full md:w-[calc(50%-12px)] shrink-0 flex"
            >
                <img :src="recipe.image_url" :alt="recipe.title" class="w-1/3 object-cover h-48" />
                <div class="flex flex-col justify-between flex-1 p-4">
                    <div>
                        <h3 class="font-bold text-xl mb-2">{{ recipe.title }}</h3>
                        <p class="text-sm text-gray-500 mb-4">{{ recipe.description }}</p>
                    </div>

                    <div class="flex justify-between mb-3">

                    <div class="flex items-center gap-1">
                        <span class="text-sm">難度:</span>
                        <div class="flex gap-0.5">
                            <Icon 
                                v-for="n in recipe.difficulty" 
                                :key="n"
                                icon="mdi:star"
                                class="text-sm text-amber-400"
                            />
                            <Icon 
                                v-for="n in (5 - recipe.difficulty)" 
                                :key="'empty-' + n"
                                icon="mdi:star-outline"
                                class="text-sm text-gray-300"
                            />
                        </div>
                    </div>
                    <span class="text-sm">大約:&nbsp;&nbsp;{{ recipe.coocking_time }}分</span>
                    </div>
                    <div class="flex gap-2">
                            <button @click="handleCookRecipe(recipe)" class="w-full bg-amber-300 text-black text-sm px-3 py-2 rounded cursor-pointer">
                                我要做!
                            </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
