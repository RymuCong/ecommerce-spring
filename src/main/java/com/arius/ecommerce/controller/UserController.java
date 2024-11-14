package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
