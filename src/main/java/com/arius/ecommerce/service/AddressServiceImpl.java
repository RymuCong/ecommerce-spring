package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AddressDTO;
import com.arius.ecommerce.entity.Address;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.AddressRepository;
import com.arius.ecommerce.repository.UserRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressDTO createAddress(String email, Address address) {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new ResourceNotFoundException("User","email",email);
        }

        Address newAddress = new Address();
        newAddress.setAddressDetail(address.getAddressDetail());
        newAddress.setUser(user);

        addressRepository.save(newAddress);

        return CommonMapper.INSTANCE.toAddressDTO(newAddress);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();

        List<AddressDTO> addressDTOS = addresses.stream().map(CommonMapper.INSTANCE::toAddressDTO).toList();

        return addressDTOS;
    }

    @Override
    public List<AddressDTO> getAddressesOfUser(String emailId) {
        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        List<Address> addresses = addressRepository.findByUserUserId(user.getUserId());

        return addresses.stream().map(CommonMapper.INSTANCE::toAddressDTO).toList();
    }

    @Override
    public AddressDTO getAddress(String emailId, Long addressId) {
        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        Address address = addressRepository.findByAddressIdAndUserUserId(addressId,user.getUserId());

        return CommonMapper.INSTANCE.toAddressDTO(address);
    }

    @Override
    public AddressDTO updateAddress(String emailId, Long addressId, Address address) {
        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        Address savedAddress = addressRepository.findByAddressIdAndUserUserId(addressId,user.getUserId());

        savedAddress.setAddressDetail(address.getAddressDetail());

        addressRepository.save(savedAddress);

        return CommonMapper.INSTANCE.toAddressDTO(savedAddress);
    }

    @Override
    public String deleteAddress(Long addressId, String emailId) {
        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        Address address = addressRepository.findByAddressIdAndUserUserId(addressId,user.getUserId());

        addressRepository.delete(address);

        return "Address with " + address.getAddressId() + " deleted successfully";
    }
}
