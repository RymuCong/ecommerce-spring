package com.arius.ecommerce.service;

import com.arius.ecommerce.entity.Role;

public interface RoleService {
    Role findByName(String name);
}