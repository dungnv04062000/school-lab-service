package com.schoollab.repository;

import com.schoollab.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    Ward findByName(String wardName);

    Ward findByCode(String wardCode);
}
