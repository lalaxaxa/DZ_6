package com.borisov.DZ_4.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyExistException extends RuntimeException{
    public UserEmailAlreadyExistException(String email) {
        super(String.format(
                "Email %s уже используется",
                email
        ));
    }
}
