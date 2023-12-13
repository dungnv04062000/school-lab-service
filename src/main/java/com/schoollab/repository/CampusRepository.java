package com.schoollab.repository;

import com.schoollab.model.Campus;
import com.schoollab.model.Note;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusRepository extends JpaRepository<Campus, String> {
    @Query("SELECT c FROM Campus c WHERE c.name LIKE CONCAT('%', ?1, '%')  ORDER BY c.createAt ASC")
    List<Campus> findCampusByName(String name);

    @Query("SELECT c FROM Campus c WHERE c.name LIKE ?1")
    Campus findCorrectCampusByName(String name);
}
