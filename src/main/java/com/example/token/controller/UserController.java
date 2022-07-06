package com.example.token.controller;

import com.example.token.entity.User;
import com.example.token.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${user.base-url}")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
        @Autowired
        private UserServiceImpl userService;


        @PostMapping("${user.validation-url}")
        public ResponseEntity<Boolean> validateUser(@RequestBody User user){
            boolean isValidate = userService.login(user);
            return ResponseEntity.ok().body(isValidate);
        }

        @PostMapping("${user.post-url}")
        public ResponseEntity<String> addUser(@RequestBody User user){
            userService.register(user);
            return ResponseEntity.ok("User registered successfully"); }

        @GetMapping("${user.sign-out-url}")
        public ResponseEntity<String> signOut(){
            userService.signOut();
            return  ResponseEntity.ok("Signed out");
        }
        @PostMapping("${user.post-budget-url}")
        public ResponseEntity<String> addUserBudget(@RequestBody int budget){
            if(!userService.isUserSignedIn())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You have to sign in first");
            userService.postBudget(budget);
            return ResponseEntity.ok("Budget added successfully"); }
}


