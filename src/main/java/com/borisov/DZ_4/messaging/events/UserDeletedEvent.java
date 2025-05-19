package com.borisov.DZ_4.messaging.events;

public class UserDeletedEvent {
    private final String email;

    public UserDeletedEvent(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
