package com.schoollab.repository;

import com.schoollab.model.STEMKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface STEMKnowledgeRepository extends JpaRepository<STEMKnowledge, String> {
}
