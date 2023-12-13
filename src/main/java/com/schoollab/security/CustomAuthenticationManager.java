package com.schoollab.security;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.Account;
import com.schoollab.model.Role;
import com.schoollab.model.User;
import com.schoollab.model.UserRole;
import com.schoollab.repository.AccountRepository;
import com.schoollab.repository.RoleRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    GenericMapper genericMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            //đăng nhập bằng google
            String email = (String) authentication.getPrincipal();

            User user = userRepository.findByEmail(email);
            if (user == null) {
                log.error("Không tìm thấy người dùng này");
                throw new BadCredentialsException("Không tìm thấy người dùng này");
            }

            List<UserRole> roles = userRoleRepository.findAllByUserId(user.getId());
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            roles.stream().forEach(r -> {
                Optional<Role> optRole = roleRepository.findById(r.getRoleId());
                if (optRole.isPresent()) {
                    authorities.add(new SimpleGrantedAuthority(optRole.get().getName()));
                }
            });
            return new UsernamePasswordAuthenticationToken(user.getId(), null, authorities);
        } else {
            //đăng nhập bằng username password
            if (authentication.getPrincipal() == null || authentication.getCredentials() == null) {
                throw new BadCredentialsException("Vui lòng điền đầy đủ thông tin tài khoản");
            }

            String username = (String) authentication.getPrincipal();
            String password = (String) authentication.getCredentials();

            Account account = accountRepository.findByUsername(username);
            if (account == null) {
                log.error("Không tìm thấy tài khoản này");
                throw new BadCredentialsException("Không tìm thấy tài khoản này");
            }
            String salt = account.getSalt();
            if (!passwordEncoder.matches(password + salt, account.getPassword())) {
                log.error("Sai mật khẩu");
                throw new BadCredentialsException("Sai mật khẩu");
            }
            log.info("Correct password");

            if(!account.getIsVerify()){
                throw new UnKnownException("Tài khoản này chưa được xác thực!");
            }

            List<UserRole> roles = userRoleRepository.findAllByUserId(account.getUserId());
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            roles.stream().forEach(r -> {
                Optional<Role> optRole = roleRepository.findById(r.getRoleId());
                if (optRole.isPresent()) {
                    authorities.add(new SimpleGrantedAuthority(optRole.get().getName()));
                }
            });
            return new UsernamePasswordAuthenticationToken(account.getUserId(), null, authorities);
        }
    }
}
