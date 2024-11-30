package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.AddressDTO;
import com.arius.ecommerce.entity.Address;
import com.arius.ecommerce.security.JwtUtils;
import com.arius.ecommerce.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;

    private final JwtUtils jwtUtils;

    @Autowired
    public AddressController(AddressService addressService, JwtUtils jwtUtils) {
        this.addressService = addressService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/user/address")
    public ResponseEntity<AddressDTO> createAddress(HttpServletRequest request, @Valid @RequestBody Address address){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        AddressDTO addressDTO = addressService.createAddress(emailId,address);

        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/address")
    public ResponseEntity<List<AddressDTO>> getAddresses(){

        List<AddressDTO> addressDTOS = addressService.getAddresses();

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/user/address")
    public ResponseEntity<List<AddressDTO>> getAddressesOfUser(HttpServletRequest request){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        List<AddressDTO> addressDTOS = addressService.getAddressesOfUser(emailId);

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/user/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("addressId") Long addressId, HttpServletRequest request){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        AddressDTO addressDTO = addressService.getAddress(emailId,addressId);

        return new ResponseEntity<>(addressDTO,HttpStatus.OK);

    }

    @PutMapping("/user/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable("addressId") Long addressId, @Valid @RequestBody Address address, HttpServletRequest request){
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        AddressDTO addressDTO = addressService.updateAddress(emailId,addressId,address);

        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @DeleteMapping("/user/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId,HttpServletRequest request){
        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        String response = addressService.deleteAddress(addressId,emailId);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
