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

-- 1. users(使用者資訊) 
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE COMMENT '使用者名稱',
    email VARCHAR(40) NOT NULL UNIQUE COMMENT '電子郵件',
    password VARCHAR(255) NOT NULL COMMENT '密碼（加密存儲）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間'
) COMMENT='使用者帳號資訊表',ENGINE=InnoDB;

-- 2. ingredients (材料相關資訊)
CREATE TABLE ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_name VARCHAR(30) NOT NULL UNIQUE COMMENT '食材名稱',
    category ENUM(
        'vegetable',   -- 蔬菜
        'fruit',       -- 水果
        'meat',        -- 肉類
        'dairy',       -- 乳製品 
        'seasoning',   -- 調味料
        'oil',         -- 油類
        'seafood',     -- 海鮮
        '其他'
    ) NOT NULL COMMENT '食材分類',
    shelf_life_days INT COMMENT '預設保存天數'
) COMMENT='所有食材資訊表' ENGINE=InnoDB;

-- 示例資料
INSERT INTO ingredients (ingredient_name, category, shelf_life_days) VALUES
('番茄', 'vegetable', 7),
('雞蛋', 'dairy', 30),
('雞肉', 'meat', 3),
('豬肉', 'meat', 3),
('蝦', 'seafood', 2),
('蔥花', 'vegetable', 10),
('醬油', 'seasoning', 365),
('鹽', 'seasoning', 999),
('帶骨雞肉塊', 'meat', 3),
('老薑片', 'vegetable', 14),
('麻油', 'seasoning', 365),
('米酒', 'seasoning', 365);

-- 3. 冰箱庫存表 存儲使用者冰箱中的食材庫存
CREATE TABLE fridge_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    ingredient_id INT NOT NULL COMMENT '食材 ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '數量',
    unit VARCHAR(10) NOT NULL COMMENT '單位',
    purchase_date DATE NOT NULL COMMENT '購買日期',
    expiry_date DATE COMMENT '過期日期',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
) COMMENT='冰箱庫存表',ENGINE=InnoDB;

-- 4. 食譜表 存儲所有食譜的基本資訊
CREATE TABLE recipes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT COMMENT '使用者 ID（null 表示系統食譜）',
    title VARCHAR(30) NOT NULL COMMENT '食譜名稱',
    description TEXT COMMENT '食譜描述',
    image_url VARCHAR(255) COMMENT '食譜圖片 URL',
    cooking_time_minutes INT COMMENT '烹飪時間（分鐘）',
    servings INT COMMENT '份量（人）',
    instructions TEXT COMMENT '詳細步驟',
    created_by VARCHAR(20) COMMENT '創作者',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) COMMENT='食譜表',ENGINE=InnoDB;

-- 5. 食譜食材表（多對多關聯） 存儲每個食譜需要的食材和份量
CREATE TABLE recipe_ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id INT NOT NULL COMMENT '食譜 ID',
    ingredient_id INT NOT NULL COMMENT '食材 ID',
    quantity DECIMAL(10, 2) NOT NULL COMMENT '需要的數量',
    unit VARCHAR(5) NOT NULL COMMENT '單位',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_recipe_ingredient (recipe_id, ingredient_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) COMMENT='食譜和食材的關聯表',ENGINE=InnoDB;

-- 6. 購物清單表 存儲使用者的購物清單
CREATE TABLE shopping_lists (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '使用者 ID',
    name VARCHAR(30) COMMENT '購物清單名稱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) COMMENT='購物清單（可能有多個）',ENGINE=InnoDB;

-- 7. 購物清單項目表 存儲購物清單中的個別食材
CREATE TABLE shopping_list_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shopping_list_id INT NOT NULL COMMENT '購物清單 ID',
    recipe_id INT COMMENT '來自哪個食譜（null 表示手動新增）',
    ingredient_id INT NOT NULL COMMENT '食材 ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '數量',
    unit VARCHAR(5) NOT NULL COMMENT '單位',
    is_purchased BOOLEAN DEFAULT FALSE COMMENT '是否已購買',
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE SET NULL,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE RESTRICT,
    INDEX idx_shopping_list_id (shopping_list_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) COMMENT='購物清單項目表',ENGINE=InnoDB;