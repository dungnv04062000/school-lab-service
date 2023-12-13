package com.schoollab.repository;

import com.schoollab.model.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, String> {

    List<Experiment> findAllByNoteIdOrderByCreateAtDesc(String noteId);
}
