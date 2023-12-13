package com.schoollab.service;

import com.schoollab.model.UserRole;

public interface UserRoleService {

    UserRole addUserRole(String userId, String role);

    UserRole deleteUserRole(String userId, String role);
}
