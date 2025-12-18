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
public class User {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
	private String userName;
	private String email;
	private String role;
	
	// 密碼：只能寫入，不能讀取（安全性）
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	// 時間戳記
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private transient LocalDateTime createdAt;  
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)    
    private transient LocalDateTime updatedAt;
    
}
