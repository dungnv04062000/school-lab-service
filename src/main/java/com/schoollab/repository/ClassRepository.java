package com.schoollab.repository;

import com.schoollab.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {

    List<Class> findAllBySemesterId(String semesterId);
    Class findByFormTeacherId(String formTeacherId);
    Class findByFormTeacherIdAndSemesterId(String formTeacherId, String semesterId);
    Class findByName(String name);
    Class findByNameAndAndSemesterId(String name, String semesterId);
}
