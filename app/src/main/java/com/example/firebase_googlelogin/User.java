package com.example.firebase_googlelogin;

public class User {
    private String name;
    private String id;
    private String password;

    public User(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    // Getter 메서드 추가
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}