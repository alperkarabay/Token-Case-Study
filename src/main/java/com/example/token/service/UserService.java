package com.example.token.service;

import com.example.token.entity.User;

public interface UserService {
    void register(User user);
    boolean login(User user);
    boolean isUserSignedIn();
    Long getCurrentUserId();
    boolean signOut();
}
