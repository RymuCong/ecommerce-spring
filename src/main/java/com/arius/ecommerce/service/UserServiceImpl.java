package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.reponse.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.entity.Role;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.NotFoundUserException;
import com.arius.ecommerce.repository.RoleRepository;
import com.arius.ecommerce.repository.UserRepository;
import com.arius.ecommerce.security.AppConstants;
import com.arius.ecommerce.security.JwtUtils;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    @Override
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        User existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            return new AuthResponse(null, "User already exists with email " + registerRequest.getEmail());
        }

        User user = CommonMapper.INSTANCE.toUser(registerRequest);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByRoleName(AppConstants.ROLE_USER);
        user.getRoles().add(role);

        userRepository.save(user);
        return new AuthResponse(jwtUtils.generateToken(user.getEmail()), "User registered successfully");
    }
}
