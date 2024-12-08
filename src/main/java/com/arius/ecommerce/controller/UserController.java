package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.request.RegisterForAdminRequest;
import com.arius.ecommerce.dto.request.UserRequest;
import com.arius.ecommerce.dto.response.LoginResponse;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.service.UserService;
import com.arius.ecommerce.utils.UserExcelExportUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

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

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest editUser, HttpServletRequest request){

        UserRequest user = userService.updateUser(editUser,request);

        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId){
        return new ResponseEntity<>(userService.deleteUser(userId),HttpStatus.OK);
    }

    @GetMapping("/admin/is-login")
    public ResponseEntity<?> getAdminIsLogin(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            UserDTO user = userService.getUser(request);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new LoginResponse("Access Denied", null));
        }
    }

    @GetMapping("/user/is-login")
    public ResponseEntity<?> getUserIsLogin(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("USER"))) {
            UserDTO user = userService.getUser(request);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse("Access Denied", null));
        }
    }

    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId){
        UserDTO userDTO = userService.getUserById(userId);
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @PostMapping("/admin/users/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterForAdminRequest registerRequest) {
        UserDTO registeredUser = userService.registerUserForAdmin(registerRequest);
        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @PostMapping("/admin/users/importExcelData")
    public ResponseEntity<?> importExcelData(@RequestParam("file") MultipartFile file) {
        List<UserDTO> savedUsers = userService.importDataInExcelFile(file);
        return new ResponseEntity<>(savedUsers, HttpStatus.OK);
    }

    @PostMapping("/admin/users/exportExcelData")
    public ResponseEntity<?> exportExcelData(HttpServletResponse response) {
        response.setContentType(("application/octet-stream"));
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment;filename=\"users-data-%s.xlsx\"", System.currentTimeMillis());

        response.setHeader(headerKey, headerValue);

        List<UserDTO> users = userService.getAllUsers();

        UserExcelExportUtil excelExporter = new UserExcelExportUtil(users);
        excelExporter.export(response);
        return new ResponseEntity<>(
                Collections.singletonMap("message", "User data exported successfully"),
                HttpStatus.OK
        );
    }
}
