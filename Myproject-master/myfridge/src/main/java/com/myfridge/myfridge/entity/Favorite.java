package com.myfridge.myfridge.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    //DB基本欄位
	private Integer id;
	private Integer userId;
	private Integer recipeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime savedAt;
	//前端顯示要用
	private String title;
	private String description;
	private String imageUrl;
	private Integer cookingTime;
	private Integer difficulty;
	
	//新增：管理員用的欄位
    private String userName;
    private String recipeTitle;
}
