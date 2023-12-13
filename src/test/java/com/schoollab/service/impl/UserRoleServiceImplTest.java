package com.schoollab.service.impl;

import com.schoollab.model.Role;
import com.schoollab.model.UserRole;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.RoleRepository;
import com.schoollab.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserRoleServiceImplTest {
    @InjectMocks
    UserRoleServiceImpl userRoleService;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    RoleRepository roleRepository;
    UserRole userRole;
    Role role;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        role = SetUpModelTest.setUpRole(3, "teacher");
        userRole = SetUpModelTest.setUpUserRole(role);
    }

    @Test
    void addUserRole_roleStudent() {
        role = SetUpModelTest.setUpRole(4, "student");
        userRole = SetUpModelTest.setUpUserRole(role);
        List<UserRole> list = new ArrayList<>();
        list.add(userRole);
        when(userRoleRepository.findAllByUserId(Mockito.any())).thenReturn(list);
        UserRole response = null;
        try {
            response = userRoleService.addUserRole("HE140705", "teacher");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không thể thêm role khác vào học sinh");
            assertNull(response);
        }
    }

    @Test
    void addUserRole_roleNull() {
        List<UserRole> list = new ArrayList<>();
        list.add(userRole);
        when(userRoleRepository.findAllByUserId(Mockito.any())).thenReturn(list);
        when(roleRepository.findByName(Mockito.any())).thenReturn(null);
        UserRole response = null;
        try {
            response = userRoleService.addUserRole("HE140705", "teacher");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Role không hợp lệ!");
            assertNull(response);
        }
    }

    @Test
    void addUserRole_existsUserRole() {
        List<UserRole> list = new ArrayList<>();
        list.add(userRole);
        when(userRoleRepository.findAllByUserId(Mockito.any())).thenReturn(list);
        when(roleRepository.findByName(Mockito.any())).thenReturn(role);
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.eq("HE140705"), Mockito.eq(role.getId()))).thenReturn(userRole);
        UserRole response = null;
        try {
            response = userRoleService.addUserRole("HE140705", "teacher");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "User_role này đã tồn tại");
            assertNull(response);
        }
    }

    @Test
    void addUserRole_success() {
        List<UserRole> list = new ArrayList<>();
        list.add(userRole);
        when(userRoleRepository.findAllByUserId(Mockito.any())).thenReturn(list);
        when(roleRepository.findByName(Mockito.any())).thenReturn(role);
        when(userRoleRepository.findByUserIdAndAndRoleId(Mockito.eq("HE140705"), Mockito.eq(role.getId()))).thenReturn(null);
        when(userRoleRepository.save(Mockito.any())).thenReturn(userRole);
        UserRole response  = userRoleService.addUserRole("HE140705", "teacher");

        assertNotNull(response);
        assertEquals(response, userRole);
    }

    @Test
    void deleteUserRole() {
        UserRole response = userRoleService.deleteUserRole("HE140705", "1");
        assertNull(response);
    }
}