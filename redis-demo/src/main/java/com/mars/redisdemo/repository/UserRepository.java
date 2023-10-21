package com.mars.redisdemo.repository;

import com.mars.redisdemo.models.User;

import java.util.Map;

public interface UserRepository {
    void save(User user);
    Map<String,User> findAll();
    User findById(String id);
    void update(User user);
    void delete(String id);
}