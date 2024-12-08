package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.*;
import com.arius.ecommerce.dto.request.RegisterForAdminRequest;
import com.arius.ecommerce.dto.request.UserRequest;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.entity.Address;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
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
//            throw new NotFoundUserException("User not found");
            throw new APIException("User not found");
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(AppConstants.ROLE_ADMIN));
        if (isAdmin) {
            throw new APIException("Admin users cannot log in using this method");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new APIException("Invalid email or password");
        }

        String token = null;

        if (auth.isAuthenticated()) {
            token = jwtUtils.generateToken(loginRequest.getEmail(), user.getRoles());
        }

        return new AuthResponse(token, "User logged in successfully", getReadUserDTO(user));
    }

    @Override
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        User existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            throw  new APIException("User already exists with email " + registerRequest.getEmail());
        }

        User user = CommonMapper.INSTANCE.toUser(registerRequest);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByRoleName(AppConstants.ROLE_USER);
        user.getRoles().add(role);

        userRepository.save(user);
        cartService.createCart(user.getEmail());
        return new AuthResponse(jwtUtils.generateToken(user.getEmail(), user.getRoles()), "User registered successfully", getReadUserDTO(user));
    }

    @Override
    public UserDTO registerUserForAdmin(RegisterForAdminRequest registerForAdminRequest) {
        User existingUser = userRepository.findByEmail(registerForAdminRequest.getEmail());
        if (existingUser != null) {
            throw new APIException("User already exists with email " + registerForAdminRequest.getEmail());
        }

        User user = CommonMapper.INSTANCE.toUser(registerForAdminRequest);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(registerForAdminRequest.getPassword()));

        Set<Role> roles = registerForAdminRequest.getRoles().stream()
                .map(roleRepository::findByRoleName)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userRepository.save(user);
        cartService.createCart(user.getEmail());

        return getReadUserDTO(user);
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

        List<UserDTO> userDTOS = users.stream().map(UserServiceImpl::getReadUserDTO).toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setUsers(userDTOS);
        userResponse.setPageNumber(pagedUser.getNumber());
        userResponse.setPageSize(pagedUser.getSize());
        userResponse.setTotalPages(pagedUser.getTotalPages());
        userResponse.setTotalElements(pagedUser.getTotalElements());
        userResponse.setLastPage(pagedUser.isLast());

        return userResponse;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        if(users.isEmpty()){
            throw new APIException("No User exists");
        }

        return users.stream().map(UserServiceImpl::getReadUserDTO).toList();
    }

    @Override
    public UserDTO getUser(HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        User user = userRepository.findByEmail(emailId);

        if(user == null){
            throw new ResourceNotFoundException("User","email",emailId);
        }

        return getReadUserDTO(user);
    }

    @Override
    public UserRequest updateUser(UserRequest dto, HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        User user = userRepository.findByEmail(emailId);

        if (user == null) {
            throw new ResourceNotFoundException("User", "emailId", emailId);
        }

        try {
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setMobileNumber(dto.getMobileNumber());
        } catch (Exception e) {
            throw new APIException("Error updating user details");
        }

        try {
            if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
                user.setEmail(dto.getEmail());
            }
        } catch (Exception e) {
            throw new APIException("Error updating user email");
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            user.setPassword(encoder.encode(dto.getPassword()));
        }

        try {
            if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
                List<Address> addresses = dto.getAddresses().stream()
                        .map(CommonMapper.INSTANCE::toAddress)
                        .collect(Collectors.toList());

                addresses.forEach(address -> address.setUser(user));

                user.setAddresses(addresses);
            }
        } catch (Exception e) {
            throw new APIException("Error updating user addresses");
        }

        userRepository.save(user);

        return CommonMapper.INSTANCE.toUserRequest(user);
    }

    @Override
    public Long deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(AppConstants.ROLE_ADMIN));
        if (isAdmin) {
            throw new APIException("Admin users cannot be deleted");
        }

        if (user.getCart() != null) {
            List<CartItem> cartItems = user.getCart().getCartItems();
            Long cartId = user.getCart().getCartId();

            cartItems.forEach(cartItem -> {
                Long productId = cartItem.getProduct().getProductId();
                cartService.deleteProductFromCartUsingCartId(cartId, productId);
            });
        }

        userRepository.delete(user);

        return userId;
    }

    @Override
    public AuthResponse loginAdmin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new NotFoundUserException("User not found");
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(AppConstants.ROLE_ADMIN));
        if (!isAdmin) {
            throw new APIException("User does not have admin privileges");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);

        String token = null;

        if (auth.isAuthenticated()) {
            token = jwtUtils.generateToken(loginRequest.getEmail(), user.getRoles());
        }

        return new AuthResponse(token, "Admin logged in successfully", getReadUserDTO(user));
    }

    @Override
    public AuthResponse registerAdmin(RegisterRequest registerRequest) {
        User existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            throw new APIException("User already exists with email " + registerRequest.getEmail());
        }

        User user = CommonMapper.INSTANCE.toUser(registerRequest);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(registerRequest.getPassword()));

        Role role = roleRepository.findByRoleName(AppConstants.ROLE_ADMIN);
        user.getRoles().add(role);

        userRepository.save(user);
        return new AuthResponse(jwtUtils.generateToken(user.getEmail(), user.getRoles()), "Admin registered successfully", getReadUserDTO(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User","userId",userId));

        return getUserDTO(user);
    }

    @Override
    public List<UserDTO> importDataInExcelFile(MultipartFile file) {
        // Check if file is empty
        if (file == null) {
            throw new NullPointerException("File is empty");
        }

        // Check if the file is an Excel file xlsx
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType())) {
            // Get extension file
            String extension = "";
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            throw new InvalidMediaTypeException(extension, "Invalid file format. Please upload an excel file extension xlsx");
        }

        // Check size file is less than 10MB
        if (file.getSize() > 10485760) {
            throw new MaxUploadSizeExceededException(10L * 1024 * 1024);
        }

        // Read data from Excel file
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Check template is valid

            // 1. check not empty
            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                throw new RuntimeException("Template is invalid");
            }
            // 2. check header is valid
            final String[] header = {"Email", "First Name", "Last Name", "Mobile Number", "Role", "Password"};
            for (int i = 0; i < header.length; i++) {
                if (!header[i].equals(getCellValue(headerRow.getCell(i + 1)).toString().trim())) { // getCell(i+1) because the first column is No.
                    System.out.println(getCellValue(headerRow.getCell(i + 1)).toString());
                    throw new RuntimeException("Template is invalid");
                }
            }

            // Read data from Excel file
            List<User> users = new ArrayList<>();
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 2; i <= lastRowNum; i++) { // for i = 2 because data start from row 3 -> index = 2
                Row row = sheet.getRow(i);
                User user = convertRowToUser(row);
                if (user != null) {
                    // Set role and bcrypt password
                    Role role = roleRepository.findByRoleName(user.getRoles().iterator().next().getRoleName());
                    user.getRoles().clear();
                    user.getRoles().add(role);
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
                    user.setPassword(encoder.encode(user.getPassword()));

                    users.add(user);
                    saveUserFromExcel(user);
                }
            }
            workbook.close();
            return users.stream().map(UserServiceImpl::getReadUserDTO).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void saveUserFromExcel(User user) {
        // Validate user data
        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new APIException("Invalid email format");
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty() || user.getFirstName().length() > 20) {
            throw new APIException("First Name should be within 1 to 20 characters:" + user.getEmail());
        }
        if (user.getLastName() == null || user.getLastName().isEmpty() || user.getLastName().length() > 20) {
            throw new APIException("Last Name should be within 1 to 20 characters: " + user.getEmail());
        }
        if (user.getMobileNumber() == null || !user.getMobileNumber().matches("^\\d{10}$")) {
            throw new APIException("Mobile Number must be 10 digits long and contain only numbers: " + user.getEmail());
        }
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            throw new APIException("Password should be at least 3 characters: " + user.getEmail());
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new APIException("User must have at least one role: " + user.getEmail());
        }

        // Save user to the database
        userRepository.save(user);
        cartService.createCart(user.getEmail());
    }

    private User convertRowToUser(Row row) {
        if (row != null) {
            User user = new User();
            user.setEmail(getCellValue(row.getCell(1)).toString());
            user.setFirstName(getCellValue(row.getCell(2)).toString());
            user.setLastName(getCellValue(row.getCell(3)).toString());
            user.setMobileNumber(getCellValue(row.getCell(4)).toString());
            user.setPassword(getCellValue(row.getCell(6)).toString());
            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setRoleName(getCellValue(row.getCell(5)).toString());
            roles.add(role);
            user.setRoles(roles);
            return user;
        }
        return null;
    }

    private Object getCellValue(Cell cell) {
        // Check cell is empty
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue();
                }
                yield cell.getNumericCellValue();
            }
            case FORMULA -> cell.getCellFormula();
            case ERROR -> cell.getErrorCellValue();
            default -> null;
        };
    }

    private static UserDTO getUserDTO(User user) {
        UserDTO userDTO = CommonMapper.INSTANCE.toUserDTO(user);

        CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(user.getCart());

        List<CartItemDTO> cartItemDTOS = user.getCart().getCartItems().stream().map(
                CommonMapper.INSTANCE::toCartItemDTO
        ).toList();

        Set<RoleDTO> roleDTOS = user.getRoles().stream()
                .map(CommonMapper.INSTANCE::toRoleDTO).collect(Collectors.toSet());

        List<AddressDTO> addressDTOS = user.getAddresses().stream().map(
                CommonMapper.INSTANCE::toAddressDTO
        ).toList();

        userDTO.setCartDTO(cartDTO);
        userDTO.getCartDTO().setCartItems(cartItemDTOS);
        userDTO.setRoles(roleDTOS);
        userDTO.setAddresses(addressDTOS);

        return userDTO;
    }

    private static UserDTO getReadUserDTO(User user) {
        UserDTO userDTO = CommonMapper.INSTANCE.toUserDTO(user);

        Set<RoleDTO> roleDTOS = user.getRoles().stream()
                .map(CommonMapper.INSTANCE::toRoleDTO).collect(Collectors.toSet());

        List<AddressDTO> addressDTOS = user.getAddresses().stream().map(
                CommonMapper.INSTANCE::toAddressDTO
        ).toList();

        userDTO.setRoles(roleDTOS);
        userDTO.setAddresses(addressDTOS);

        return userDTO;
    }
}
