package com.borisov.DZ_4.service;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.mappers.UserMapper;
import borisov.core.UserChangedEvent;
import com.borisov.DZ_4.models.User;
import com.borisov.DZ_4.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.borisov.DZ_4.util.UserNotFoundException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.publisher = publisher;
    }

    public List<UserResponseDTO> findAll(){
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(u -> userMapper.toResponseDTO(u))
                .collect(Collectors.toList());
    }

    public UserResponseDTO findById(int id){
        return userMapper.toResponseDTO(findEntityById(id));
    }

    private User findEntityById(int id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", Integer.toString(id)));
    }

    public boolean existsByEmail(String email, Integer excludeId){
        if (excludeId == null) return userRepository.findByEmail(email).isPresent();
        else {
            Optional<User> user = userRepository.findByEmail(email);
            return user.filter(value -> value.getId() != excludeId).isPresent();
        }
    }

    @Transactional
    public UserResponseDTO save(UserCreateDTO userCreateDTO){
        User user = userMapper.toEntity(userCreateDTO);
        completeUserCreation(user);
        userRepository.save(user);
        publisher.publishEvent(new UserChangedEvent(user.getId(), user.getEmail(),
                UserChangedEvent.Operation.CREATE));
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateById(int id, UserCreateDTO newUserCreateDTO){
        User oldUser = findEntityById(id);
        User newUser = userMapper.toEntity(newUserCreateDTO);
        completeUserUpdate(newUser, oldUser);
        userRepository.save(newUser);
        return userMapper.toResponseDTO(newUser);
    }

    @Transactional
    public void deleteById(int id){
        User deleteUser = findEntityById(id);
        userRepository.deleteById(id);
        publisher.publishEvent(new UserChangedEvent(deleteUser.getId(), deleteUser.getEmail(),
                UserChangedEvent.Operation.DELETE));
    }



    private void completeUserCreation(User user) {
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    }
    private void completeUserUpdate(User newUser, User oldUser) {
        newUser.setId(oldUser.getId());
        newUser.setCreatedAt(oldUser.getCreatedAt());
    }

}
