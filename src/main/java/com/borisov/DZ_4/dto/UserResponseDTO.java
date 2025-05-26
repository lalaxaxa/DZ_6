package com.borisov.DZ_4.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Пользователь")
public class UserResponseDTO {
    @Schema(description = "ID", example = "1")
    private int id;
    @Schema(description = "Имя", example = "Bob")
    private String name;
    @Schema(description = "Электронная почта", example = "bob@example.com")
    private String email;
    @Schema(description = "Возраст", example = "30")
    private Integer age;
    @Schema(description = "Дата и время создания записи" , example = "2025-05-22T07:47:07.350053Z")
    private OffsetDateTime createdAt;

    public UserResponseDTO() {
    }

    public UserResponseDTO(int id, String name, String email, Integer age, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
