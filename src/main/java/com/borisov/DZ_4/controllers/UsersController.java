package com.borisov.DZ_4.controllers;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.mappers.UserMapper;
import com.borisov.DZ_4.models.User;
import com.borisov.DZ_4.service.UserService;
import com.borisov.DZ_4.util.UserEmailAlreadyExistException;
import com.borisov.DZ_4.util.UserErrorResponse;
import com.borisov.DZ_4.util.UserNotFoundException;
import com.borisov.DZ_4.util.UserValidationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UsersController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping()
    public List<UserResponseDTO> getUsers(){
        List<User> users = userService.findAll();
        return users.stream().map(user -> userMapper.toResponseDTO(user))
                .collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable("id") int id){
        User user = userService.findById(id);
        return userMapper.toResponseDTO(user);
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserCreateDTO userCreateDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        userService.isEmailAlreadyExistThrowException(userCreateDTO.getEmail(), Optional.empty());

        int id = userService.save(userMapper.toEntity(userCreateDTO));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()        // → /users
                .path("/{id}")               // → /users/{id}
                .buildAndExpand(id)          // → /users/123
                .toUri();
        //return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public UserResponseDTO update(@PathVariable("id") int id,
                                             @RequestBody @Valid UserCreateDTO userCreateDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        userService.isEmailAlreadyExistThrowException(userCreateDTO.getEmail(), Optional.of(id));

        User updatedUser = userService.updateById(id, userMapper.toEntity(userCreateDTO));
        return userMapper.toResponseDTO(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id){
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
