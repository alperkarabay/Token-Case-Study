package com.example.token.service;

import com.example.token.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void register(User user);
    boolean login(User user);
    boolean isUserSignedIn();
    Long getCurrentUserId();
    boolean signOut();
    void postBudget(int budget);
}
