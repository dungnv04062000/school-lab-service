package com.schoollab.repository;

import com.schoollab.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    List<UserRole> findAllByUserId(String userId);

    UserRole findByUserIdAndAndRoleId(String userId, int roleId);
}
