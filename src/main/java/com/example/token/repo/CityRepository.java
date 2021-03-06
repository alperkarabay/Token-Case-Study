package com.example.token.repo;


import com.example.token.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City,Long>{
    City findByName(String name);
}
