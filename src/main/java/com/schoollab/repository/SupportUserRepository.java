package com.schoollab.repository;

import com.schoollab.model.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportUserRepository extends JpaRepository<Support, String> {
}
