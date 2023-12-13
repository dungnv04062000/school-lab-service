package com.schoollab.repository;

import com.schoollab.model.Subject;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Boolean existsSubjectByName(String name);

    Optional<Subject> findSubjectByName(String name);
}
