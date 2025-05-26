package com.borisov.DZ_4.controllers;

import com.borisov.DZ_4.assembler.UserModelAssembler;
import com.borisov.DZ_4.dto.HateoasUserResponseDTO;
import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.service.UserService;
import com.borisov.DZ_4.util.UserEmailAlreadyExistException;
import com.borisov.DZ_4.dto.UserErrorResponse;
import com.borisov.DZ_4.util.UserNotFoundException;
import com.borisov.DZ_4.util.UserValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "User API", description = "Операции с пользователями")
@RestController
@RequestMapping("/users")
public class UsersController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;
    private final UserModelAssembler assembler;

    @Autowired
    public UsersController(UserService userService, UserModelAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }



    @Operation(summary = "Получить список пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = HateoasUserResponseDTO.class)))
    @GetMapping()
    public CollectionModel<EntityModel<UserResponseDTO>> getUsers() {

        List<EntityModel<UserResponseDTO>> users = userService.findAll().stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(users,
                linkTo(methodOn(UsersController.class).getUsers()).withSelfRel(),
                linkTo(methodOn(UsersController.class).create(null, null)).withRel("create")
        );
    }



    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HateoasUserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponseDTO> getUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable("id") int id) {
        UserResponseDTO user = userService.findById(id);
        return assembler.toModel(user);
    }
    /*@GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable("id") int id){
        UserResponseDTO user = userService.findById(id);
        return user;
    }*/



    @Operation(summary = "Удалит пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable("id") int id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    @Operation(summary = "Добавить нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HateoasUserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<EntityModel<UserResponseDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCreateDTO.class)))
            @RequestBody @Valid UserCreateDTO userCreateDTO,
            BindingResult bindingResult
    ) {
        //check valid and check email unique
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        if (userService.existsByEmail(userCreateDTO.getEmail(), null))
            throw new UserEmailAlreadyExistException();

        UserResponseDTO created  = userService.save(userCreateDTO);
        EntityModel<UserResponseDTO> model = assembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }



    @Operation(summary = "Обновить данные пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HateoasUserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public EntityModel<UserResponseDTO> update(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable("id") int id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для обновления",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCreateDTO.class)))
            @RequestBody @Valid UserCreateDTO userCreateDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) throw new UserValidationException(getValidErrMsg(bindingResult));
        if (userService.existsByEmail(userCreateDTO.getEmail(), id))
            throw new UserEmailAlreadyExistException();
        UserResponseDTO updated = userService.updateById(id, userCreateDTO);
        return assembler.toModel(updated);
    }


    private String getValidErrMsg(BindingResult bindingResult) {
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
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        LOGGER.error(e.getMessage());
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserValidationException e) {
        LOGGER.error(e.getMessage());
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserEmailAlreadyExistException e) {
        LOGGER.error(e.getMessage());
        UserErrorResponse response = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
