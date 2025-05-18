package com.borisov.DZ_4.controllers;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.service.UserService;
import com.borisov.DZ_4.util.UserEmailAlreadyExistException;
import com.borisov.DZ_4.util.UserErrorResponse;
import com.borisov.DZ_4.util.UserNotFoundException;
import com.borisov.DZ_4.util.UserValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping()
    public List<UserResponseDTO> getUsers(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable("id") int id){
        UserResponseDTO user = userService.findById(id);
        return user;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id){
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(
            @RequestBody @Valid UserCreateDTO userCreateDTO,
            BindingResult bindingResult){
        //check valid and check email unique
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        if (userService.existsByEmail(userCreateDTO.getEmail(), null))
            throw new UserEmailAlreadyExistException();

        int id = userService.save(userCreateDTO);
        String locationPath = ServletUriComponentsBuilder
                .fromCurrentRequest()       // "/users"
                .path("/{id}")              // "/users/{id}"
                .buildAndExpand(id)         // подставляем id
                .toUri()                    // получается URI "http://localhost/users/123"
                .getPath();                 // берём только "/users/123"

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, locationPath)
                .build();
    }

    @PatchMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable("id") int id,
            @RequestBody @Valid UserCreateDTO userCreateDTO,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        if (userService.existsByEmail(userCreateDTO.getEmail(), id))
            throw new UserEmailAlreadyExistException();

        return userService.updateById(id, userCreateDTO);
    }



    private String getValidErrMsg(BindingResult bindingResult){
        StringBuilder errMsg = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errMsg.append(fieldError.getField())
                    .append(" - ").append(fieldError.getDefaultMessage())
                    .append(";");
        }
        return errMsg.toString();
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e){
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserValidationException e) {
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserEmailAlreadyExistException e) {
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
