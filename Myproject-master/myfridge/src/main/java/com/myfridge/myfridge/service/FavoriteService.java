package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfridge.myfridge.entity.Favorite;
import com.myfridge.myfridge.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    // 查詢所有使用者的收藏（管理員用
    public List<Favorite> getAllFavorite(){
        return favoriteRepository.findAll();
    }

    // 查詢使用者的收藏清單
    public List<Favorite> getFavoriteByUserId(Integer userId){

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }

        return favoriteRepository.findByUserId(userId);
    }

    // 檢查是否已收藏
    public Boolean isFavorited(Integer userId, Integer recipeId) {

        if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}

		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}

        return favoriteRepository.checkExists(userId, recipeId);
    }

    // 新增收藏
    public Favorite addFavorite(Integer userId, Integer recipeId) {

        if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}

		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}

        if (favoriteRepository.checkExists(userId, recipeId)) {
            throw new RuntimeException("已經收藏過此食譜");
        }

        return favoriteRepository.insert(userId, recipeId);
    }

    // 刪除收藏
    public void deleteFavorite(Integer userId, Integer recipeId) {

         if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}

		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}

        int rows = favoriteRepository.deleteItem(userId, recipeId);
        if (rows == 0) {
            throw new RuntimeException("找不到此收藏或無權刪除");
        }
    }

    // 切換收藏狀態
    @Transactional
    public boolean toggleFavorite(Integer userId, Integer recipeId) {

        if(userId == null) {
			throw new IllegalArgumentException("使用者 ID 不可為空");
		}

		if(recipeId == null) {
			throw new IllegalArgumentException("食譜 ID 不可為空");
		}

        if(favoriteRepository.checkExists(userId, recipeId)) {
            deleteFavorite(userId, recipeId);
            return false;
        }else {
            addFavorite(userId, recipeId);
            return true;
        }
    }
}
