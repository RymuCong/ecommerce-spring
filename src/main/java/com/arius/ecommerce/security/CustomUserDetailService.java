package com.arius.ecommerce.security;

import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);

        if (user == null){
            throw new ResourceNotFoundException("User","email",username);
        }

        return new UserPrincipal(user);
    }
}
