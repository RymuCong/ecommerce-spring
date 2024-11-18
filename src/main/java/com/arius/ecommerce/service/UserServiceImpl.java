package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.*;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.entity.CartItem;
import com.arius.ecommerce.entity.Role;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.NotFoundUserException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.RoleRepository;
import com.arius.ecommerce.repository.UserRepository;
import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.security.JwtUtils;
import com.arius.ecommerce.utils.CommonMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CartService cartService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, CartService cartService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.cartService = cartService;
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

    @Override
    public UserResponse getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<User> pagedUser = userRepository.findAll(pageable);

        List<User> users = pagedUser.getContent();

        if(users.isEmpty()){
            throw new APIException("No User exists");
        }

        List<UserDTO> userDTOS = users.stream().map(UserServiceImpl::getUserDTO).toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOS);
        userResponse.setPageNumber(pagedUser.getNumber());
        userResponse.setPageSize(pagedUser.getSize());
        userResponse.setTotalPages(pagedUser.getTotalPages());
        userResponse.setTotalElements(pagedUser.getTotalElements());
        userResponse.setLastPage(pagedUser.isLast());

        return userResponse;
    }

    @Override
    public UserDTO getUser(HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        User user = userRepository.findByEmail(emailId);

        if(user == null){
            throw new ResourceNotFoundException("User","email",emailId);
        }

        return getUserDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO dto, HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMobileNumber(dto.getMobileNumber());

        userRepository.save(user);

        return getUserDTO(user);
    }

    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User","userId",userId));

        List<CartItem> cartItems = user.getCart().getCartItems();
        Long cartId = user.getCart().getCartId();

        cartItems.forEach(cartItem -> {
            Long productId = cartItem.getProduct().getProductId();

            cartService.deleteProductFromCartUsingCartId(cartId,productId);
        });

        userRepository.delete(user);

        return  "User Deleted Successfully";
    }

    private static UserDTO getUserDTO(User user) {
        UserDTO userDTO = CommonMapper.INSTANCE.toUserDTO(user);

        CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(user.getCart());

        List<ProductDTO> productDTOS = user.getCart().getCartItems().stream()
                .map(item -> CommonMapper.INSTANCE.toProductDTO(item.getProduct())).toList();

        Set<RoleDTO> roleDTOS = user.getRoles().stream()
                .map(CommonMapper.INSTANCE::toRoleDTO).collect(Collectors.toSet());

        List<AddressDTO> addressDTOS = user.getAddresses().stream().map(
                CommonMapper.INSTANCE::toAddressDTO
        ).toList();

        userDTO.setCartDTO(cartDTO);
        userDTO.getCartDTO().setProducts(productDTOS);
        userDTO.setRoles(roleDTOS);
        userDTO.setAddress(addressDTOS);

        return userDTO;
    }
}
