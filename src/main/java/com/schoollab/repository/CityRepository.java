package com.schoollab.repository;

import com.schoollab.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, String> {
    City findByName(String cityName);

    City findByCode(String cityCode);
}
