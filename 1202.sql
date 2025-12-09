DROP DATABASE IF EXISTS MyFridge;
CREATE DATABASE MyFridge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE MyFridge;

-- Clear out the old table, if they existed at all.
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS fridge_items;
DROP TABLE IF EXISTS Recipe;
DROP TABLE IF EXISTS RecipeIngredient;
DROP TABLE IF EXISTS shopping_lists;
DROP TABLE IF EXISTS shopping_list_items;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;

-- 1. User(使用者資訊) 
CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userName VARCHAR(20) NOT NULL UNIQUE COMMENT '使用者名稱',
    email VARCHAR(40) NOT NULL UNIQUE COMMENT '電子郵件',
    password VARCHAR(255) NOT NULL COMMENT '密碼（加密存儲）',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間'
) COMMENT='使用者帳號資訊表',ENGINE=InnoDB;

-- 2. ingredients (材料相關資訊)
CREATE TABLE Ingredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ingredientName VARCHAR(20) NOT NULL UNIQUE COMMENT '食材名稱',
    category ENUM(
        'vegetable',   -- 蔬菜
        'fruit',       -- 水果
        'meat',        -- 肉類
        'dairy',       -- 乳製品 
        'seasoning',   -- 調味料
        'oil',         -- 油類
        'seafood',     -- 海鮮
        'egg',         -- 蛋類
		'bean',         -- 豆類
        'other'        -- 其他
    ) NOT NULL COMMENT '食材分類',
    shelfLifeDays INT COMMENT '預設保存天數'
) COMMENT='所有食材資訊表' ENGINE=InnoDB;

-- 示例資料
INSERT INTO myfridge.Ingredient (ingredientName, category, shelfLifeDays) VALUES
('番茄', 'vegetable', 7),           -- 1
('雞蛋', 'egg', 30),                 -- 2
('雞肉', 'meat', 3),                 -- 3
('豬肉', 'meat', 3),                 -- 4
('蝦', 'seafood', 2),                -- 5
('蔥花', 'vegetable', 10),           -- 6
('醬油', 'seasoning', 365),          -- 7
('鹽', 'seasoning', 999),            -- 8
('帶骨雞肉塊', 'meat', 3),           -- 9
('老薑片', 'vegetable', 14),         -- 10
('麻油', 'oil', 365),                -- 11
('米酒', 'seasoning', 365),          -- 12
('時令蔬菜', 'vegetable', 7),        -- 13
('大蒜', 'vegetable', 30),           -- 14
('五花肉', 'meat', 3),               -- 15
('冰糖', 'seasoning', 999),          -- 16
('雞胸肉', 'meat', 3),               -- 17
('花生米', 'bean', 180),             -- 18
('乾辣椒/花椒', 'seasoning', 365),   -- 19
('嫩豆腐', 'bean', 2),               -- 20
('牛肉/豬肉末', 'meat', 2),          -- 21
('豆瓣醬', 'seasoning', 365),        -- 22
('豬里脊肉', 'meat', 3),             -- 23
('木耳/筍絲', 'vegetable', 7),       -- 24
('泡椒', 'seasoning', 365),          -- 25
('老抽', 'seasoning', 365),          -- 26
('生抽', 'seasoning', 365),          -- 27
('可樂', 'other', 30),               -- 28
('雞中翅', 'meat', 3),               -- 29
('新鮮活魚', 'seafood', 2),          -- 30
('蔥薑絲', 'vegetable', 7),          -- 31
('蒸魚豉油', 'seasoning', 365),      -- 32
('土豆', 'vegetable', 14),           -- 33
('白醋', 'seasoning', 365),          -- 34
('黃瓜', 'vegetable', 7),            -- 35
('甜麵醬', 'seasoning', 365),        -- 36
('黃豆醬', 'seasoning', 365),        -- 37
('青椒', 'vegetable', 7),            -- 38
('大蔥白', 'vegetable', 7),          -- 39
('糖', 'seasoning', 999);            -- 40

-- 3. 冰箱庫存表 存儲使用者冰箱中的食材庫存
CREATE TABLE FridgeItem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL COMMENT '使用者 ID',
    ingredientId INT NOT NULL COMMENT '食材 ID',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 1 COMMENT '數量',
    unit VARCHAR(10) NOT NULL COMMENT '單位',
    purchasedDate DATE NOT NULL COMMENT '購買日期',
    expiredDate DATE COMMENT '過期日期',
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredientId) REFERENCES Ingredient(id) ON DELETE RESTRICT,
    INDEX idx_userId (userId),
    INDEX idx_expired_date (expiredDate)
) COMMENT='冰箱庫存表',ENGINE=InnoDB;

-- 4. 食譜表 存儲所有食譜的基本資訊
CREATE TABLE Recipe (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT COMMENT '使用者 ID（null 表示系統食譜）',
    title VARCHAR(100) NOT NULL COMMENT '食譜名稱',
    description TEXT COMMENT '食譜描述',
    imageUrl VARCHAR(255) COMMENT '食譜圖片 URL（public 資料夾的相對路徑）',
    cookingTime INT COMMENT '烹飪時間（分鐘）',
    difficulty INT COMMENT '難度等級（1-5）',
    step TEXT COMMENT '詳細步驟',
    isPublic BOOLEAN DEFAULT TRUE COMMENT '是否公開食譜',
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
    INDEX idx_userId (userId)
) COMMENT='食譜表',ENGINE=InnoDB;
-- 清空舊資料 (如果需要)
-- SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM myfridge.RecipeIngredient WHERE recipeId > 0;
-- DELETE FROM myfridge.Recipe WHERE id > 0;
-- DELETE FROM myfridge.ingredients WHERE id > 0;
-- ALTER TABLE myfridge.ingredients AUTO_INCREMENT = 1;
-- ALTER TABLE myfridge.Recipe AUTO_INCREMENT = 1;
-- ALTER TABLE myfridge.RecipeIngredient AUTO_INCREMENT = 1;
-- SET FOREIGN_KEY_CHECKS = 1; 
-- 插入 12 個食譜
INSERT INTO myfridge.Recipe (userId, title, description, imageUrl, cookingTime, difficulty, step, isPublic) VALUES

-- 食譜 1: 番茄炒蛋
(NULL, '番茄炒蛋', '簡單快手菜，營養豐富，酸甜開胃的家常經典。', '/TomatoEgg.webp', 12, 3, 
'1. 打蛋：將雞蛋打入碗中，加入少許鹽和幾滴水（讓蛋更嫩滑），用筷子充分攪打均勻，直至蛋液起泡。

2. 炒蛋：鍋中倒入適量的油，燒熱後將蛋液倒入，快速用鏟子劃散，炒至半熟且邊緣微焦時盛出備用。

3. 加番茄：利用鍋中餘油，將切好的番茄塊倒入，加入少許鹽和糖（糖的量可根據番茄的酸度調整），中小火翻炒至番茄出汁變軟。

4. 合體：將炒好的雞蛋重新倒入鍋中，與番茄汁快速翻炒均勻，讓雞蛋充分吸收番茄的酸甜味，可以淋入少許水澱粉勾芡使湯汁濃稠，最後撒上蔥花即可出鍋。', TRUE),

-- 食譜 2: 紅燒肉
(NULL, '紅燒肉', '經典濃油赤醬的本幫菜，肥而不膩，入口即化，色澤紅亮誘人。', '/Redbraisedpork.webp', 90, 5,
'1. 汆燙：將五花肉切成麻將大小的塊狀，冷水下鍋，加入薑片和料酒，大火煮沸後撇去浮沫，撈出洗淨瀝乾。

2. 炒糖色：鍋中放少許油，加入冰糖或白砂糖，小火慢慢熬煮至糖融化變成琥珀色，注意不要炒焦。

3. 上色：將五花肉塊倒入鍋中，快速翻炒，讓每塊肉都均勻裹上糖色，隨後加入薑片、蔥段、八角和桂皮等香料。

4. 慢燉入味：倒入足量的熱水（水量需沒過肉塊）、老抽和生抽調味，大火煮沸後轉最小火，蓋上鍋蓋燜煮60-90分鐘，直到肉質軟爛。

5. 收汁：待肉軟爛後，開大火將湯汁收濃，期間要不斷翻動防止粘鍋，讓湯汁緊密地包裹在肉塊上，呈現出油光發亮的狀態即可。', TRUE),

-- 食譜 3: 宮保雞丁
(NULL, '宮保雞丁', '川菜經典名菜，雞肉鮮嫩，花生米酥脆，味道鹹甜酸辣適中。', '/Bao.webp', 20, 5,
'1. 醃製雞丁：將雞胸肉或雞腿肉切成小丁，加入少許鹽、料酒、蛋清和澱粉，抓勻後醃製10分鐘備用。

2. 調製醬汁：將醋、糖、生抽、老抽、料酒、水澱粉和少量清水混合，攪拌均勻製成宮保汁。

3. 炒香配料：鍋中放油燒熱，先將乾辣椒段和花椒粒小火煸炒出香味，隨後放入蔥段和薑片爆香。

4. 滑炒雞丁：將醃好的雞丁倒入鍋中，快速滑炒至變色，然後加入事先炸酥的花生米。

5. 淋醬收尾：將調好的宮保汁沿鍋邊倒入，迅速翻炒，讓醬汁均勻地包裹住雞丁和花生米，湯汁變得濃稠後即可出鍋，保持雞丁的嫩滑和花生米的酥脆。', TRUE),

-- 食譜 4: 麻婆豆腐
(NULL, '麻婆豆腐', '著名的川菜，麻、辣、燙、嫩、酥、香、鮮，口感和風味極具特色。', '/Tofu.webp', 15, 4,
'1. 準備豆腐：將嫩豆腐切成約2公分見方的小塊，放入加了少許鹽的熱水中輕輕汆燙1分鐘，撈出瀝水，這能讓豆腐不易碎。

2. 炒香調料：鍋中放油燒熱，先放入牛肉末或豬肉末煸炒至變色，接著加入豆瓣醬和豆豉，小火炒出紅油和香氣。

3. 下豆腐：將切好的豆腐塊輕輕倒入鍋中，加入適量的水或高湯，轉中小火慢煮入味。

4. 調味收汁：加入生抽、少許糖調味，待湯汁稍微收乾時，勾入適量的水澱粉使湯汁變得濃稠。

5. 撒料出鍋：最後撒上大量的花椒粉（這是「麻」的關鍵）和蔥花，輕輕推勻後即可出鍋，趁熱食用風味最佳。', TRUE),

-- 食譜 5: 魚香肉絲
(NULL, '魚香肉絲', '魚香是川菜獨特的複合味，鹹甜酸辣兼具，醬汁濃郁，拌飯一絕。', '/Fish&Meat.webp', 25, 5,
'1. 醃製肉絲：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。

2. 調製魚香汁：將醋、糖、生抽、老抽、水澱粉和清水按比例混合均勻，製成魚香汁。

3. 滑炒肉絲：鍋中熱油，將醃好的肉絲快速滑炒至變色即刻盛出，保持肉絲的嫩度。

4. 炒香配料：利用底油，先後放入薑末、蒜末、泡椒末和剁碎的豆瓣醬，小火炒出紅油和香氣。

5. 混合收汁：將肉絲和切好的木耳絲、筍絲等配料倒入鍋中，快速翻炒，隨後倒入魚香汁，大火翻炒，讓醬汁均勻地裹在食材上，湯汁變得濃稠發亮後即可出鍋。', TRUE),

-- 食譜 6: 蒜蓉清炒時蔬
(NULL, '蒜蓉清炒時蔬', '保持蔬菜的原汁原味，清爽健康，是餐桌上不可或缺的平衡菜。', '/Veg.webp', 8, 2,
'1. 清洗備料：將所選的時令蔬菜（如：菠菜、空心菜、油菜等）清洗乾淨，瀝乾水分，大蒜切成細蓉備用。

2. 爆香蒜蓉：鍋中倒入較多的油，大火燒熱後，將一半的蒜蓉倒入，快速爆香，注意不要炒焦。

3. 快速翻炒：將蔬菜倒入鍋中，大火快速翻炒，這樣可以最大限度地保留蔬菜的翠綠色和清脆口感，如果蔬菜不易熟可適當加蓋燜煮片刻。

4. 調味出鍋：在蔬菜接近斷生時，加入適量的鹽和少許雞精調味，如果喜歡更濃的蒜味，此時可加入剩下的生蒜蓉。

5. 均勻翻動：快速翻炒均勻後，即可盛盤上桌，追求速度是清炒時蔬的關鍵。', TRUE),

-- 食譜 7: 可樂雞翅
(NULL, '可樂雞翅', '深受小朋友喜愛的甜口菜，雞翅軟嫩入味，帶著淡淡的可樂焦糖香。', '/cokechicken.webp', 30, 3,
'1. 雞翅處理：將雞中翅清洗乾淨，用刀在表面劃兩刀方便入味，加入薑片、料酒和少量生抽醃製15分鐘。

2. 煸炒上色：鍋中放少量油，將雞翅放入，中小火煎至兩面金黃微焦，逼出多餘油脂後盛出。

3. 加入可樂：將煎好的雞翅重新放回鍋中，加入生抽和老抽調色，然後倒入整罐可樂，水量需沒過雞翅。

4. 小火慢燉：大火煮沸後轉小火，蓋上鍋蓋燜煮約20分鐘，讓雞翅充分吸收可樂的甜味和焦糖色。

5. 大火收汁：開大火將湯汁收濃，期間要不斷翻動雞翅防止粘鍋，待湯汁變得濃稠且均勻包裹住雞翅後即可關火盛盤。', TRUE),

-- 食譜 8: 清蒸魚
(NULL, '清蒸魚', '粵菜的代表作，最大限度保留魚肉的鮮美和嫩滑，清淡健康。', '/fish.webp', 15, 4,
'1. 魚身處理：將魚（如鱸魚、石斑魚）內臟去除洗淨，魚身劃幾刀，用鹽和料酒輕輕塗抹，魚肚和魚身上放上蔥薑絲去腥。

2. 準備蒸鍋：蒸鍋中水燒開，將魚放在墊有蔥薑段的盤子上，確保魚身與盤底有空隙，利於蒸汽循環。

3. 控制火候：大火蒸約8-10分鐘（根據魚的大小調整時間），魚眼突出、魚肉剛好離骨即為成熟。

4. 淋醬油：將蒸魚盤中多餘的湯汁倒掉（腥味來源），夾去魚身上的蔥薑絲，重新鋪上新的蔥絲和香菜。

5. 熱油激香：將適量的蒸魚豉油淋在魚身上，隨後燒熱少許油，將熱油均勻地潑在蔥絲上，激發出香氣即可上桌。', TRUE),

-- 食譜 9: 酸辣土豆絲
(NULL, '酸辣土豆絲', '極具人氣的家常菜，酸爽開胃，土豆絲清脆爽口。', '/sourspicy.webp', 10, 2,
'1. 切絲浸泡：將土豆去皮後切成極細的絲，放入清水中反覆沖洗或浸泡，直到水變清澈，目的是洗去多餘的澱粉，保證炒出來的口感脆爽。

2. 準備調料：將乾辣椒切段，蒜切末備用，醋、鹽、少量糖調和成汁。

3. 爆炒調料：鍋中燒熱油，先放入乾辣椒段和花椒粒小火煸炒出香，隨後加入蒜末爆香。

4. 大火快炒：將瀝乾的土豆絲倒入鍋中，轉大火快速翻炒，加入少量白醋和鹽調味。

5. 醋香收尾：在土豆絲快要炒熟時，沿鍋邊淋入大量的醋，快速翻炒幾下即可出鍋，保持其酸味和清脆的口感。', TRUE),

-- 食譜 10: 木須肉
(NULL, '木須肉', '傳統京菜，雞蛋嫩黃，木耳爽脆，肉片滑嫩，色彩豐富，營養均衡。', '/muxu.webp', 20, 4,
'1. 滑炒雞蛋：雞蛋打散後，鍋中放油燒熱，將蛋液倒入，快速滑炒成大塊的雞蛋碎，盛出備用。

2. 醃製肉片：將豬肉切成薄片，用鹽、料酒和澱粉抓勻醃製10分鐘。

3. 炒肉片：鍋中放油，將醃好的肉片倒入，快速滑炒至變色即刻盛出，保持肉片的嫩度。

4. 炒配料：利用鍋中底油，放入蔥段、薑絲爆香，隨後加入泡發好的木耳和黃瓜片或筍片，大火翻炒。

5. 混合調味：將炒好的雞蛋和肉片重新倒入鍋中，加入生抽、少許鹽和糖調味，快速翻炒均勻，淋入少許水澱粉勾薄芡即可出鍋。', TRUE),

-- 食譜 11: 醬爆肉片
(NULL, '醬爆肉片', '濃郁的醬香味，肉片軟嫩，是道非常下飯的北方家常菜。', '/JumBao.webp', 18, 4,
'1. 肉片處理：將豬後腿肉或里脊肉切成薄片，加入料酒、生抽、少許澱粉抓勻醃製10分鐘。

2. 調製醬汁：用甜麵醬、黃豆醬、少許糖和少量水混合均勻，製成濃郁的醬汁。

3. 滑炒肉片：鍋中熱油，將醃好的肉片倒入，快速滑炒至變色後盛出備用。

4. 爆香醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入調好的醬汁，小火慢慢炒出醬香和光澤。

5. 混合收汁：將肉片和切好的青椒或洋蔥片倒入鍋中，快速大火翻炒，讓濃稠的醬汁均勻地裹在肉片和蔬菜上，撒上蔥花即可出鍋。', TRUE),

-- 食譜 12: 京醬肉絲
(NULL, '京醬肉絲', '北京風味名菜，肉絲細嫩，醬香濃郁，搭配豆腐皮和蔥絲食用。', '/TomatoEgg.webp', 20, 5,
'1. 肉絲醃製：豬里脊肉切成均勻的細絲，加入料酒、鹽、蛋清和澱粉抓勻醃製15分鐘。

2. 滑炒肉絲：鍋中放油，將肉絲倒入快速滑炒至變色，立刻盛出，保持肉絲的嫩度。

3. 炒醬汁：鍋中留底油，先放入蔥薑末爆香，接著倒入甜麵醬（可加入少許糖和水稀釋），小火慢慢炒出醬香味。

4. 入味收汁：將炒好的肉絲重新倒入鍋中，轉大火快速翻炒，讓濃稠的醬汁均勻地裹在每根肉絲上。

5. 搭配擺盤：將肉絲盛出放在盤中，旁邊配上切好的蔥白絲和黃瓜絲，搭配薄薄的豆腐皮或春餅捲著吃。', TRUE);

-- 5. 食譜食材表（多對多關聯） 存儲每個食譜需要的食材和份量
CREATE TABLE RecipeIngredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipeId INT NOT NULL COMMENT '食譜 ID',
    ingredientId INT NOT NULL COMMENT '食材 ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '需要的數量',
    unit VARCHAR(5) NOT NULL COMMENT '單位',
    FOREIGN KEY (recipeId) REFERENCES Recipe(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredientId) REFERENCES Ingredient(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_recipe_ingredient (recipeId, ingredientId),
    INDEX idx_recipeId (recipeId),
    INDEX idx_ingredientId (ingredientId)
) COMMENT='食譜和食材的關聯表',ENGINE=InnoDB;

-- 食譜 1: 番茄炒蛋
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(1, 1, 2, '個'),   -- 番茄
(1, 2, 3, '個'),   -- 雞蛋
(1, 6, 1, '匙');   -- 蔥花

-- 食譜 2: 紅燒肉
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(2, 15, 500, '克'), -- 五花肉
(2, 16, 30, '克'),  -- 冰糖
(2, 7, 3, '匙');    -- 醬油

-- 食譜 3: 宮保雞丁
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(3, 17, 300, '克'), -- 雞胸肉
(3, 18, 50, '克'),  -- 花生米
(3, 19, 10, '克');  -- 乾辣椒/花椒

-- 食譜 4: 麻婆豆腐
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(4, 20, 1, '塊'),   -- 嫩豆腐
(4, 21, 100, '克'), -- 牛肉/豬肉末
(4, 22, 2, '匙');   -- 豆瓣醬

-- 食譜 5: 魚香肉絲
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(5, 23, 250, '克'), -- 豬里脊肉
(5, 24, 100, '克'), -- 木耳/筍絲
(5, 25, 1, '匙');   -- 泡椒

-- 食譜 6: 蒜蓉清炒時蔬
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(6, 13, 500, '克'), -- 時令蔬菜
(6, 14, 3, '瓣'),   -- 大蒜
(6, 8, 1, '茶匙'); -- 鹽

-- 食譜 7: 可樂雞翅
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(7, 29, 8, '個'),   -- 雞中翅
(7, 28, 300, '毫升'), -- 可樂
(7, 7, 2, '匙');    -- 醬油

-- 食譜 8: 清蒸魚
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(8, 30, 500, '克'), -- 新鮮活魚
(8, 31, 50, '克'),  -- 蔥薑絲
(8, 32, 3, '匙');   -- 蒸魚豉油

-- 食譜 9: 酸辣土豆絲
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(9, 33, 2, '個'),   -- 土豆
(9, 34, 3, '匙'),   -- 白醋
(9, 19, 5, '個');   -- 乾辣椒/花椒

-- 食譜 10: 木須肉
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(10, 4, 150, '克'), -- 豬肉
(10, 2, 2, '個'),   -- 雞蛋
(10, 35, 100, '克'); -- 黃瓜

-- 食譜 11: 醬爆肉片
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(11, 4, 250, '克'), -- 豬肉
(11, 36, 3, '匙'),  -- 甜麵醬
(11, 38, 1, '個');  -- 青椒

-- 食譜 12: 京醬肉絲
INSERT INTO myfridge.RecipeIngredient (recipeId, ingredientId, amount, unit) VALUES
(12, 23, 250, '克'), -- 豬里脊肉
(12, 36, 4, '匙'),   -- 甜麵醬
(12, 39, 1, '根');   -- 大蔥白

-- 6. 購物清單項目表 存儲購物清單中的個別食材
CREATE TABLE ShoppingListItem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL COMMENT '使用者 ID',
    recipeId INT COMMENT '來自哪個食譜（null=手動新增）',
    recipeName VARCHAR(50) COMMENT '食譜名稱（冗餘存儲，方便顯示）',
    ingredientId INT COMMENT '食材 ID（可為 null，支援自訂食材名）',
    ingredientName VARCHAR(20) NOT NULL COMMENT '食材名稱',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 1,
    unit VARCHAR(10) NOT NULL,
    category VARCHAR(20),
    isPurchased BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (recipeId) REFERENCES Recipe(id) ON DELETE SET NULL,
    FOREIGN KEY (ingredientId) REFERENCES Ingredient(id) ON DELETE SET NULL,
    INDEX idx_userId (userId),
    INDEX idx_isPurchased (isPurchased)
) COMMENT='購物清單項目表',ENGINE=InnoDB;

-- 7. 收藏食譜表 存儲使用者收藏的食譜
CREATE TABLE Favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL COMMENT '使用者 ID',
    recipeId INT NOT NULL COMMENT '食譜 ID',
    savedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏時間',
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (recipeId) REFERENCES Recipe(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_recipe (userId, recipeId) COMMENT '同一使用者不能重複收藏同一食譜',
    INDEX idx_userId (userId),
    INDEX idx_recipeId (recipeId),
    INDEX idx_savedAt (savedAt)
) COMMENT='使用者收藏食譜表',ENGINE=InnoDB;

-- 8. 評論表 存儲使用者對食譜的評論
CREATE TABLE Comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL COMMENT '使用者 ID',
    recipeId INT NOT NULL COMMENT '食譜 ID',
    rating INT NOT NULL COMMENT '評分（1-5）',
    text TEXT NOT NULL COMMENT '評論內容',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (recipeId) REFERENCES Recipe(id) ON DELETE CASCADE,
    INDEX idx_userId (userId),
    INDEX idx_recipeId (recipeId),
    INDEX idx_rating (rating),
    INDEX idx_createdAt (createdAt)
) COMMENT='食譜評論表',ENGINE=InnoDB;