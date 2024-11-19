package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AddressDTO;
import com.arius.ecommerce.entity.Address;

import java.util.List;

public interface AddressService {

    AddressDTO createAddress(String emailId, Address address);

    List<AddressDTO> getAddresses();

    List<AddressDTO> getAddressesOfUser(String emailId);

    AddressDTO getAddress(String emailId, Long addressId);

    AddressDTO updateAddress(String emailId, Long addressId, Address address);

    String deleteAddress(Long addressId, String emailId);
}
