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
    Map<Integer, ArrayList<Integer>> findBestCombination(ArrayList<Integer> itemList, int balance);
    Map<Integer, ArrayList<Integer>> handleAllSumPossibilities(ArrayList<Integer> itemList, int balance, ArrayList<Integer> combination, Map<Integer, ArrayList<Integer>> qualifyItemsCombination);
}
