package com.example.token;

import com.example.token.entity.User;
import com.example.token.model.TripPlan;
import com.example.token.repo.CityRepository;
import com.example.token.repo.UserRepository;
import com.example.token.service.CityServiceImpl;
import com.example.token.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class TokenApplicationTests {
	@Mock
	private UserServiceImpl userService;
	@Mock
	private CityServiceImpl cityService;
	@Mock
	private UserRepository userRepo;
	@Mock
	private CityRepository cityRepo;
	@Test
	public void register() {
		User registerTest = new User();
		registerTest.setUsername("test-name");
		registerTest.setPassword("password");
		userService.register(registerTest);
		verify(userService , times(1)).register(registerTest);

	}

	@Test
	public void login(){
		User loginTest = new User();
		loginTest.setUsername("test-name");
		loginTest.setPassword("password");
		userService.register(new User(0L,"test-name","password",500));
		userService.login(loginTest);
		verify(userService , times(1)).login(loginTest);

	}
	@Test
	public void addUserBudget() {
		int testBudget = 5000;
		userService.setCurrentUserId(0L);
		userService.postBudget(testBudget);
		verify(userService , times(1)).postBudget(5000);
	}
	@Test
	public void getOptimalPlans(){
		Map<String,Integer> preferedCities = new HashMap<>();
		preferedCities.put("Istanbul",50);
		preferedCities.put("Paris",500);
		int budget = 500;
		TripPlan[] optimalTripPlans = cityService.getOptimalTripPlans(preferedCities,budget);
		verify(cityService , times(1)).getOptimalTripPlans(preferedCities,budget);
	}
	@Test
	void contextLoads() {
	}

}
