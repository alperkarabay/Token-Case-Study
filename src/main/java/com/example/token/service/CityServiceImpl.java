package com.example.token.service;

import com.example.token.entity.City;
import com.example.token.model.Trip;
import com.example.token.model.TripPlan;
import com.example.token.repo.CityRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService{
    @Autowired
    CityRepository cityRepository;
    @Override
    public List<City> getAllCities(){
        return cityRepository.findAll();
    }

    @Override
    public TripPlan[] getOptimalTripPlans(Map<String, Integer> preferedCities, int budget) {

        AtomicInteger remainingBudget = new AtomicInteger(budget);
        AtomicInteger tempRemainingBudget = new AtomicInteger(remainingBudget.get());
        TripPlan[] optimalTripPlans = new TripPlan[3];
        List<Trip> currentTrip = new ArrayList<>();
        for(int i =0 ; i<3; i++) {
            remainingBudget.set(budget);
            tempRemainingBudget.set(remainingBudget.get());
            while (tempRemainingBudget.get() >= findMinPrice(preferedCities)) {
                int finalI = i;
                preferedCities.forEach((city, price) -> {
                    tempRemainingBudget.addAndGet(-price);
                    if (tempRemainingBudget.get() >= 0) {
                        List<String> photos = new ArrayList<>();
                        try {
                            photos = photos(city);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (containsName(currentTrip, city)) {
                            Trip existingTrip = currentTrip.stream().filter(o -> o.getCityName().equals(city)).findFirst().get();
                            currentTrip.stream().filter(o -> o.getCityName().equals(city)).findFirst().get().setDay(existingTrip.getDay() + 1);
                        } else{
                            if(finalI == 0)
                                currentTrip.add(new Trip(city, 1, photos));
                            if(finalI==1){
                                int day = remainingBudget.intValue()/price;
                                currentTrip.add(new Trip(city, day, photos));
                                remainingBudget.addAndGet(-(day-1)*price);
                            }
                            if(finalI==2){
                                if(price  != findMaxPrice(preferedCities)){
                                    int day = remainingBudget.intValue()/price;
                                    currentTrip.add(new Trip(city, day/2,photos));
                                    remainingBudget.addAndGet(-(day/2-1)*price);}
                                else remainingBudget.addAndGet(price);
                            }

                        }
                        remainingBudget.addAndGet(-price);
                    }
                    tempRemainingBudget.set(remainingBudget.get());
                });
            }
            tempRemainingBudget.set(remainingBudget.get());

            TripPlan tripPlan = new TripPlan();
            List<Trip> temp = new ArrayList<>();
            currentTrip.forEach(e -> {
                temp.add(e);
            });
            tripPlan.setTripList(temp);
            optimalTripPlans[i] = tripPlan;
            currentTrip.clear();


        }
        return optimalTripPlans;
    }

    public boolean containsName(final List<Trip> list, final String name){
        return list.stream().filter(o -> o.getCityName().equals(name)).findFirst().isPresent();
    }
    public int findMinPrice(Map<String,Integer> map){
        AtomicInteger min = new AtomicInteger(1000000000);
        map.forEach((k,v)->{
            if(v< min.get()) min.set(v);
        });
        return min.intValue();
    }
    public int findMaxPrice(Map<String,Integer> map){
        AtomicInteger max = new AtomicInteger(0);
        map.forEach((k,v)->{
            if(v> max.get()) max.set(v);
        });
        return max.intValue();
    }
    public List<String> photos(String cityName) throws IOException, JSONException {
        List<String> photoUrls = new ArrayList<>();
        Map<String,List<String>> urls = setUrls();
        for(int i=0; i<3; i++) {
            List<String> usedUrls = urls.get(cityName.toLowerCase());
            // Create a neat value object to hold the URL
            URL url = new URL("https://api.pexels.com/v1/photos/" + usedUrls.get(i));
// Open a connection(?) on the URL(??) and cast the response(???)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

// Now it's "open", we can set the request method, headers etc.
            connection.setRequestProperty("Authorization", "563492ad6f9170000100000148bd55a88b0f4544b98bf09c9f5a05c8");

// This line makes the request
            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(responseStream, Map.class);
// Manually converting the response body InputStream to APOD using Jackson
            photoUrls.add(jsonMap.get("url").toString());
        }
        return photoUrls;
    }
    Map<String,List<String>> setUrls(){
        Map<String,List<String>> urls = new HashMap<String, List<String>>() {};
        List<String> urlList = new ArrayList<>();
        urls.put("istanbul" , Arrays.asList("1549326", "11734760", "45189"));
        urls.put("paris" , Arrays.asList("699466", "10410200", "2574631"));
        urls.put("rome" , Arrays.asList("753639", "1797161", "2064827"));
        urls.put("london" , Arrays.asList("460672", "672532", "427679"));
        urls.put("barcelona" , Arrays.asList("1388030", "819764", "1874675"));
        urls.put("madrid" , Arrays.asList("670261", "930595", "3254729"));
        urls.put("plague" , Arrays.asList("126292", "1269805", "753337"));
        urls.put("brussels" , Arrays.asList("1595085", "2587789", "1553309"));
        urls.put("budapest" , Arrays.asList("732057", "2350351", "696288"));
        return urls;


    }


}
