package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.reponse.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.NotFoundUserException;
import com.arius.ecommerce.repository.UserRepository;
import com.arius.ecommerce.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new NotFoundUserException("User not found");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);

        String token = null;

        if(auth.isAuthenticated()){
            token = jwtUtils.generateToken(loginRequest.getEmail());
        }

        return new AuthResponse(token, "User logged in successfully");
    }
}
