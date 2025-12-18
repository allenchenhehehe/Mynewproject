package com.myfridge.myfridge.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.myfridge.myfridge.entity.User;
import com.myfridge.myfridge.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUser(){

        return userRepository.findAll();

    }

    public User getUserById(Integer id){

        return userRepository.findById(id);

    }

    public User getUserByEmail(String email){

        return userRepository.findByEmail(email);

    }

    public User register(User user){

        User existing = userRepository.findByEmail(user.getEmail());

		if(existing!=null) {
			throw new RuntimeException("此Email已經註冊!");
		}

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.insert(user);
		
    }

    public User login(String email, String password){

        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        if ("inactive".equals(user.getStatus()) || "banned".equals(user.getStatus())) {
            throw new RuntimeException("此帳號已被停用，請聯繫管理員");
        }
        
        // 2. 驗證密碼
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("帳號或密碼錯誤");
        }
        
        // 3. 回傳使用者資訊
        return user;
    }
}
