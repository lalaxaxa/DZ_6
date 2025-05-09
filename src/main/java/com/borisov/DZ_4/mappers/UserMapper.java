package com.borisov.DZ_4.mappers;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserResponseDTO toResponseDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public User toEntity(UserResponseDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public User toEntity(UserCreateDTO dto) {
        return modelMapper.map(dto, User.class);
    }
}
