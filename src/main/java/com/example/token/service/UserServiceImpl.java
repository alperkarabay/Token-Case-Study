package com.example.token.service;

import com.example.token.entity.User;
import com.example.token.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    private boolean isSignedIn;
    private Long currentUserId;
    @Override
    public boolean isUserSignedIn(){
        return this.isSignedIn;
    }
    @Override
    public Long getCurrentUserId(){
        return this.currentUserId;
    }
    @Override
    public void register(User user) {
        User registeredUser = new User();
        registeredUser.setUsername(user.getUsername());
        registeredUser.setPassword(Base64.getEncoder().encodeToString(user.getPassword().getBytes()));
        if(userRepository.findByUsername(user.getUsername())!=null)  System.out.println("kayıtlı kullanıcı");
        else  userRepository.save(registeredUser);
    }

    @Override
    public boolean login(User user) {
        User currentUser = userRepository.findByUsername(user.getUsername());
        this.currentUserId = currentUser.getId();

        String pass = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
        if(currentUser.getPassword().equals(pass)){
            this.isSignedIn =true;
            return true;
        }
        else return false;

    }
    @Override
    public boolean signOut(){
        this.isSignedIn = false;
        return true;
    }

    @Override
    public void postBudget(int budget) {
        User currentUser = userRepository.findById(this.currentUserId).get();
        currentUser.setBudget(budget);
        userRepository.save(currentUser);


    }
}
