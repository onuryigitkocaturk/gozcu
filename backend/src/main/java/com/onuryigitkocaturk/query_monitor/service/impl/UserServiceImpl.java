package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.enums.Role;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateUserException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already exists: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), hashedPassword, request.getEmail(), Role.USER);

        return userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }
}
