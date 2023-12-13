package com.schoollab.repository;

import com.schoollab.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByUsername(String username);

    Account findByUserId(String userId);
}
