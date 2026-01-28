package com.herbei.qr_inventory.service;

import com.herbei.qr_inventory.model.User;
import com.herbei.qr_inventory.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService 
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) 
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) 
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) 
    {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() 
    {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) 
    {
        return userRepository.existsByUsername(username);
    }
}
