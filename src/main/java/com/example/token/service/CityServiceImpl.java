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
    public  Map<Integer, ArrayList<Integer>> handleAllSumPossibilities(ArrayList<Integer> cityList, int budget, ArrayList<Integer> combination, Map<Integer, ArrayList<Integer>> qualifyItemsCombination) {

        System.out.println("COMBINATION FOR TEST: "+combination);

        int sum = 0;
        Integer remain=null;


        for (int x: combination){ sum += x;};

        if (sum <= budget && sum != 0){
            remain=(budget - sum);

            qualifyItemsCombination.put(remain,combination);
            System.out.println("ADD COMBINATION TO MAP: "+combination+"  CURRENT QUALIFIED COMBINATION: "+qualifyItemsCombination);
        }else{
            System.out.println("IGNORE COMBINATION: "+combination+"  NOT QUALIFY, THE COMBINATION IS EXCEEDED THE BALANCE");
        }
        System.out.println("_____________________________");


        for(int i=0;i<cityList.size();i++) {
            ArrayList<Integer> remainingItems = new ArrayList<Integer>();

            int pointingItem = cityList.get(i);
            for (int j=i+1; j<cityList.size();j++) remainingItems.add(cityList.get(j));

            ArrayList<Integer> combinationRecord = new ArrayList<Integer>(combination);

            combinationRecord.add(pointingItem);

            Map<Integer, ArrayList<Integer>> retrievedItemsCombination = handleAllSumPossibilities( remainingItems, budget, combinationRecord, qualifyItemsCombination);
            qualifyItemsCombination = retrievedItemsCombination;

        }
        return qualifyItemsCombination;
    }


    @Override
    public  Map<Integer, ArrayList<Integer>> findBestCombination(ArrayList<Integer> cityList, int budget) {

        Map<Integer, ArrayList<Integer>> qualifyItemsCombination;
        qualifyItemsCombination = handleAllSumPossibilities(cityList,budget,new ArrayList<Integer>(),new HashMap<>());

        System.out.println("THE FINAL QUALIFIED COMBINATION: "+qualifyItemsCombination);

        //sort the key (remaining budget)
        List<Map.Entry< Integer, ArrayList<Integer>>> qualifyItemsCombinationList = new ArrayList<>(qualifyItemsCombination.entrySet());
        qualifyItemsCombinationList.sort(Map.Entry.comparingByKey());

        //place the sort result
        Map<Integer, ArrayList<Integer>> sortedResult = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : qualifyItemsCombinationList) {
            sortedResult.put(entry.getKey(), entry.getValue());
        }
        System.out.println("QUALIFIED COMBINATION AFTER SORTED: "+sortedResult);

        //iterate to get the first combination = the combination with lesser remaining.
        Map.Entry<Integer, ArrayList<Integer>> entry = sortedResult.entrySet().iterator().next();
        Integer getMapKey = entry.getKey();
        ArrayList<Integer> getMapValue=entry.getValue();

        //remove all the combination that contains the remaining(key)
        //different to the lesser remaining
        //the reason of doing this is to filter the combinations and ensure the map only left the combinations with the lesser remaining
        //since it might contains more than one combination are having the lesser remaining
        sortedResult.entrySet().removeIf(key -> key.getKey() != getMapKey);
        System.out.println("THE COMBINATION WITH LESSER BALANCE: "+sortedResult);

        return sortedResult;
    }


    @Override
    public TripPlan[] getOptimalTripPlans(Map<String, Integer> preferedCities, int budget) {
        List<City> allCities = getAllCities();

        AtomicInteger remainingBudget = new AtomicInteger(budget);
        AtomicInteger tempRemainingBudget = new AtomicInteger(remainingBudget.get());
        TripPlan[] optimalTripPlans = new TripPlan[3];
        List<Trip> currentTrip = new ArrayList<>();
        for(int i =0 ; i<3; i++) {
            while (tempRemainingBudget.get() >= findMinPrice(preferedCities)) {
                int finalI = i;
                preferedCities.forEach((city, price) -> {
                    tempRemainingBudget.addAndGet(-price);
                    if (tempRemainingBudget.get() >= 0) {
                        List<String> photos;
                        try {
                            photos = photos();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (containsName(currentTrip, city)) {
                            Trip existingTrip = currentTrip.stream().filter(o -> o.getCityName().equals(city)).findFirst().get();
                            currentTrip.stream().filter(o -> o.getCityName().equals(city)).findFirst().get().setDay(existingTrip.getDay() + 1);
                        } else currentTrip.add(new Trip(city, 1, photos));
                        remainingBudget.addAndGet(-price);
                    }
                    tempRemainingBudget.set(remainingBudget.get());
                });
            }
            tempRemainingBudget.set(remainingBudget.get());

            TripPlan tripPlan = new TripPlan(currentTrip);
            optimalTripPlans[i] = tripPlan;

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
    public List<String> photos() throws IOException, JSONException {
        List<String> photoUrls = new ArrayList<>();
        // Create a neat value object to hold the URL
        URL url = new URL("https://api.pexels.com/v1/photos/2014422");

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

        return photoUrls;
    }



}
