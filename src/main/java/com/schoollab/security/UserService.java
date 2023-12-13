package com.schoollab.security;


import com.schoollab.model.Account;
import com.schoollab.model.Role;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.repository.AccountRepository;
import com.schoollab.repository.RoleRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Get Arrays (https://www.getarrays.io/)
 * @version 1.0
 * @since 7/10/2021
 */
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if(account == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("Account not found in the database");
        } else {
            log.info("User found in the database: {}", username);

            List<UserRole> userRoles = userRoleRepository.findAllByUserId(account.getUserId());
            List<String> roles = new ArrayList<>();
            userRoles.forEach(r -> {
                Optional<Role> role = roleRepository.findById(r.getRoleId());
                if (role.isPresent()) {
                    roles.add(role.get().getName());
                }
            });
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            roles.stream().forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
            return new org.springframework.security.core.userdetails.User(account.getUserId(), null, authorities);
        }
    }
}