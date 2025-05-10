package com.borisov.DZ_4.util;

//@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyExistException extends RuntimeException{
    public UserEmailAlreadyExistException() {
        super("Email уже используется");
    }
}
