package com.borisov.DZ_4.util;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String fieldName, String fieldValue) {
        super(String.format(
                "Не удалось найти пользователя с %s=%s",
                fieldName,
                fieldValue
        ));
    }
}
