package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.response.LoginResponse;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.security.UserPrincipal;
import com.arius.ecommerce.service.UserService;
import com.arius.ecommerce.utils.CommonMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<UserResponse> getAllUsers(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_USER_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    ){
        UserResponse userResponse = userService.getAllUsers(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(HttpServletRequest request){
        UserDTO userDTO = userService.getUser(request);

        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO dto, HttpServletRequest request){

        UserDTO user = userService.updateUser(dto,request);

        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId){

        String deleteUser = userService.deleteUser(userId);

        return new ResponseEntity<>(deleteUser,HttpStatus.OK);
    }

    @GetMapping("/admin/is-login")
    public ResponseEntity<?> getAdminIsLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Logged In"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "Access Denied"));
        }
    }

    @GetMapping("/user/is-login")
    public ResponseEntity<?> getUserIsLogin(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("USER"))) {
            UserDTO user = userService.getUser(request);
//            LoginResponse response = new LoginResponse("Logged In", user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse("Access Denied", null));
        }
    }
}
