package com.example.token.service;

import com.example.token.entity.City;
import com.example.token.model.TripPlan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public interface CityService {
    List<City> getAllCities();
    TripPlan[] getOptimalTripPlans(Map<String, Integer> preferedCities, int budget);
    }
