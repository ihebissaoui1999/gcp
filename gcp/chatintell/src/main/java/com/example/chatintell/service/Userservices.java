package com.example.chatintell.service;

import com.example.chatintell.entity.CategoryType;
import com.example.chatintell.entity.User;
import com.example.chatintell.entity.UserResponse;
import com.example.chatintell.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class Userservices {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserResponse> finAllUsersExceptSelf(Authentication connectedUser) {
        return userRepository.findAllUsersExceptSelf(connectedUser.getName())
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
    public List<User> getAllUserss() {
        return userRepository.findAll();
    }

}
