package com.borisov.DZ_4.testdata;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.models.User;

import java.time.OffsetDateTime;

public class UserTestData {

    public static final OffsetDateTime TIMESTAMP_1 = OffsetDateTime.parse("2000-01-01T00:00:00Z");
    public static final OffsetDateTime TIMESTAMP_2 = OffsetDateTime.parse("2001-01-01T00:00:00Z");

    public static User user( int id, String name, String email, int age, OffsetDateTime timestamp) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setAge(age);
        u.setCreatedAt(timestamp);
        return u;
    }

    public static UserResponseDTO userResponseDTO( int id, String name, String email, int age, OffsetDateTime timestamp) {
        UserResponseDTO u = new UserResponseDTO();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setAge(age);
        u.setCreatedAt(timestamp);
        return u;
    }

    public static UserCreateDTO userCreateDTO(String name, String email, int age) {
        UserCreateDTO u = new UserCreateDTO();
        u.setName(name);
        u.setEmail(email);
        u.setAge(age);
        return u;
    }

    /*

    public static User userAlice() {
        User u = new User();
        u.setId(1);
        u.setName("Alice");
        u.setEmail("alice@example.com");
        u.setAge(11);
        u.setCreatedAt(TIMESTAMP_1);
        return u;
    }

    public static UserResponseDTO userResponseDTOAlice() {
        UserResponseDTO u = new UserResponseDTO();
        u.setId(1);
        u.setName("Alice");
        u.setEmail("alice@example.com");
        u.setAge(11);
        u.setCreatedAt(TIMESTAMP_1);
        return u;
    }

    public static UserCreateDTO userCreateDTOAlice() {
        UserCreateDTO u = new UserCreateDTO();
        u.setName("Alice");
        u.setEmail("alice@example.com");
        u.setAge(11);
        return u;
    }




    public static User userBob() {
        User u = new User();
        u.setId(2);
        u.setName("Bob");
        u.setEmail("bob@example.com");
        u.setAge(22);
        u.setCreatedAt(TIMESTAMP_2);
        return u;
    }

    public static UserResponseDTO userResponseDTOBob() {
        UserResponseDTO u = new UserResponseDTO();
        u.setId(2);
        u.setName("Bob");
        u.setEmail("bob@example.com");
        u.setAge(22);
        u.setCreatedAt(TIMESTAMP_2);
        return u;
    }

    public static UserCreateDTO userCreateDTOBob() {
        UserCreateDTO u = new UserCreateDTO();
        u.setName("Bob");
        u.setEmail("bob@example.com");
        u.setAge(22);
        return u;
    }*/


}
