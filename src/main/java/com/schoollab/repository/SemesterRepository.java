package com.schoollab.repository;

import com.schoollab.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String> {

    Semester findByNameAndYear(String name, Integer year);
}
