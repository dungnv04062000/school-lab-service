package com.schoollab.service.impl;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.model.Role;
import com.schoollab.model.UserRole;
import com.schoollab.repository.RoleRepository;
import com.schoollab.repository.UserRoleRepository;
import com.schoollab.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserRole addUserRole(String userId, String roleName) {
        List<UserRole> roles = userRoleRepository.findAllByUserId(userId);
        roles.stream().forEach(r -> {
            if (r.getRoleId() == Constants.ROLE_STUDENT_ID) {
                throw new BadRequestException("Không thể thêm role khác vào học sinh");
            }
        });

        Role role = roleRepository.findByName(roleName);
        if (role == null || role.getId() == 4) {
            throw new BadRequestException("Role không hợp lệ!");
        }

        UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(userId, role.getId());
        if (userRole != null) {
            throw new BadRequestException("User_role này đã tồn tại");
        }
        UserRole newUserRole = new UserRole().setUserId(userId).setRoleId(role.getId());
        UserRole savedUserRole = userRoleRepository.save(newUserRole);

        return savedUserRole;
    }

    @Override
    public UserRole deleteUserRole(String userId, String role) {
        //TODO: delete user role

        return null;
    }
}
