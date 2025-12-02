package com.example.Rams.models;

import java.time.LocalDateTime;

public class ContactRequest {
    private String name;
    private String phone;
    private String email;
    private String message;
    private LocalDateTime createdAt;

    // Конструкторы
    public ContactRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public ContactRequest(String name, String phone, String email, String message) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}