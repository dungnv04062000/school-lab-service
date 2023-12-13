package com.schoollab.repository;

import com.schoollab.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    District findByName(String districtName);

    District findByCode(String districtCode);
}
