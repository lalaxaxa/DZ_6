package com.borisov.DZ_4.dto;

import jakarta.validation.constraints.*;

public class UserCreateDTO {
    //spring.web.locale=en
    //spring.web.locale=ru
    @NotEmpty
    @Size(min = 2, max = 100)
    private String name;

    @NotEmpty
    @Size(min = 6, max = 100)
    @Email
    private String email;

    @NotNull
    @Min(value = 0)
    @Max(value = 150)
    private Integer age;

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

}
