package com.arius.ecommerce.repository;


import com.arius.ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByAddressIdAndUserUserId(Long addressId, Long userId);

    List<Address> findByUserUserId(Long userId);
}
