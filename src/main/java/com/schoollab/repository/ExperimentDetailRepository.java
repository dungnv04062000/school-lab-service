package com.schoollab.repository;

import com.schoollab.model.ExperimentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentDetailRepository extends JpaRepository<ExperimentDetail, String> {

    List<ExperimentDetail> findAllByExperimentIdOrderByCreateAtAsc(String experimentId);
}
