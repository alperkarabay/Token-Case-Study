package com.example.token.controller;

import com.example.token.model.Trip;
import com.example.token.model.TripPlan;
import com.example.token.repo.UserRepository;
import com.example.token.service.CityServiceImpl;
import com.example.token.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toCollection;

@RestController
@RequestMapping(value = "${city.base-url}")
@CrossOrigin(origins = "http://localhost:3000")
public class CityConroller {
    @Autowired
    CityServiceImpl cityService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;


    @GetMapping("${city.get-url}")
    public ResponseEntity getOptimalTripPlans(@RequestBody Map<String,Integer> preferedCities){
        if(!userService.isUserSignedIn())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have to sign in first");
        int budget = userRepository.findById(userService.getCurrentUserId()).get().getBudget();
        TripPlan[] optimalTripPlans = cityService.getOptimalTripPlans(preferedCities,budget);
        return ResponseEntity.ok().body(optimalTripPlans);
    }
}
