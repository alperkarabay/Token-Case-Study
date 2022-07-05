package com.example.token.service;

import org.springframework.stereotype.Service;

@Service
public interface CityService {
    void getOptimalTripPlan(String preferedCities[]);
}
