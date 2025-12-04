DROP DATABASE IF EXISTS MyFridge;
CREATE DATABASE MyFridge CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE MyFridge;

-- Clear out the old table, if they existed at all.
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS fridge_items;
DROP TABLE IF EXISTS recipes;
DROP TABLE IF EXISTS recipe_ingredients;
DROP TABLE IF EXISTS shopping_lists;
DROP TABLE IF EXISTS shopping_list_items;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;

-- 1. users(使用者資訊) 
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '使用者名稱',
    email VARCHAR(40) NOT NULL UNIQUE COMMENT '電子郵件',
    password VARCHAR(255) NOT NULL COMMENT '密碼（加密存儲）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間'
) COMMENT='使用者帳號資訊表',ENGINE=InnoDB;

-- 2. ingredients (材料相關資訊)
CREATE TABLE ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_name VARCHAR(20) NOT NULL UNIQUE COMMENT '食材名稱',
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
    shelf_life_days INT COMMENT '預設保存天數'
) COMMENT='所有食材資訊表' ENGINE=InnoDB;

-- 示例資料
INSERT INTO ingredients (ingredient_name, category, shelf_life_days) VALUES
('番茄', 'vegetable', 7),
('雞蛋', 'egg', 30),
('雞肉', 'meat', 3),
('豬肉', 'meat', 3),
('蝦', 'seafood', 2),
('蔥花', 'vegetable', 10),
('醬油', 'seasoning', 365),
('鹽', 'seasoning', 999),
('帶骨雞肉塊', 'meat', 3),
('老薑片', 'vegetable', 14),
('麻油', 'seasoning', 365),
('米酒', 'seasoning', 365),
('時令蔬菜', 'vegetable', 7),
('大蒜', 'vegetable', 30),
('五花肉', 'meat', 3),
('冰糖', 'seasoning', 999),
('雞胸肉', 'meat', 3),
('花生米', 'other', 180),
('乾辣椒/花椒', 'seasoning', 365),
('嫩豆腐', 'other', 2),
('牛肉/豬肉末', 'meat', 2),
('豆瓣醬', 'seasoning', 365),
('豬里脊肉', 'meat', 3),
('木耳/筍絲', 'vegetable', 7),
('泡椒/豆辨醬', 'seasoning', 365);

-- 3. 冰箱庫存表 存儲使用者冰箱中的食材庫存
CREATE TABLE fridge_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    ingredient_id INT NOT NULL COMMENT '食材 ID',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 1 COMMENT '數量',
    unit VARCHAR(10) NOT NULL COMMENT '單位',
    purchased_date DATE NOT NULL COMMENT '購買日期',
    expired_date DATE COMMENT '過期日期',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_expired_date (expired_date)
) COMMENT='冰箱庫存表',ENGINE=InnoDB;

-- 4. 食譜表 存儲所有食譜的基本資訊
CREATE TABLE recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT COMMENT '使用者 ID（null 表示系統食譜）',
    title VARCHAR(100) NOT NULL COMMENT '食譜名稱',
    description TEXT COMMENT '食譜描述',
    image_url VARCHAR(255) COMMENT '食譜圖片 URL（public 資料夾的相對路徑）',
    cooking_time INT COMMENT '烹飪時間（分鐘）',
    difficulty INT COMMENT '難度等級（1-5）',
    step TEXT COMMENT '詳細步驟',
    is_public BOOLEAN DEFAULT TRUE COMMENT '是否公開食譜',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) COMMENT='食譜表',ENGINE=InnoDB;

-- 5. 食譜食材表（多對多關聯） 存儲每個食譜需要的食材和份量
CREATE TABLE recipe_ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id INT NOT NULL COMMENT '食譜 ID',
    ingredient_id INT NOT NULL COMMENT '食材 ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '需要的數量',
    unit VARCHAR(5) NOT NULL COMMENT '單位',
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_recipe_ingredient (recipe_id, ingredient_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) COMMENT='食譜和食材的關聯表',ENGINE=InnoDB;

-- 6. 購物清單項目表 存儲購物清單中的個別食材
CREATE TABLE shopping_list_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    recipe_id INT COMMENT '來自哪個食譜（null=手動新增）',
    recipe_name VARCHAR(50) COMMENT '食譜名稱（冗餘存儲，方便顯示）',
    ingredient_id INT COMMENT '食材 ID（可為 null，支援自訂食材名）',
    ingredient_name VARCHAR(20) NOT NULL COMMENT '食材名稱',
    amount DECIMAL(10, 2) NOT NULL DEFAULT 1,
    unit VARCHAR(10) NOT NULL,
    category VARCHAR(20),
    is_purchased BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE SET NULL,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_is_purchased (is_purchased)
) COMMENT='購物清單項目表',ENGINE=InnoDB;

-- 7. 收藏食譜表 存儲使用者收藏的食譜
CREATE TABLE favorites (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    recipe_id INT NOT NULL COMMENT '食譜 ID',
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏時間',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_recipe (user_id, recipe_id) COMMENT '同一使用者不能重複收藏同一食譜',
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_saved_at (saved_at)
) COMMENT='使用者收藏食譜表',ENGINE=InnoDB;

-- 8. 評論表 存儲使用者對食譜的評論
CREATE TABLE comments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    recipe_id INT NOT NULL COMMENT '食譜 ID',
    rating INT NOT NULL COMMENT '評分（1-5）',
    text TEXT NOT NULL COMMENT '評論內容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
) COMMENT='食譜評論表',ENGINE=InnoDB;