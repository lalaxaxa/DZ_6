package com.borisov.DZ_4.messaging.events;

public class UserCreatedEvent {
    private final String email;

    public UserCreatedEvent(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
