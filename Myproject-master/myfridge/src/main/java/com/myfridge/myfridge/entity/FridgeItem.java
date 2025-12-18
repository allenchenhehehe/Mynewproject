package com.myfridge.myfridge.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FridgeItem {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
	private Integer userId;
	private Integer ingredientId;
	private Double amount;
	private String unit;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate purchasedDate;  
	@JsonFormat(pattern = "yyyy-MM-dd")    
    private LocalDate expiredDate;
    private String ingredientName;  // 從 ingredients 表 JOIN 來的
    private String category; // 從 ingredients 表 JOIN 來的
}
