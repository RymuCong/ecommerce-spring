package com.arius.ecommerce.service;

import com.arius.ecommerce.entity.Role;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        Role role = roleRepository.findByRoleName(name);
        if (role == null) {
            throw new APIException("Role not found");
        }
        return role;
    }
}