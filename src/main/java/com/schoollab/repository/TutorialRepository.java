package com.schoollab.repository;


import com.schoollab.model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, String> {
    Tutorial findByTitle(String title);
}
