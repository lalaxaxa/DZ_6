package com.borisov.DZ_4.service;

import com.borisov.DZ_4.models.User;
import com.borisov.DZ_4.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.borisov.DZ_4.util.UserNotFoundException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(int id){
        return userRepository.findById(id).orElseThrow(()->
                new UserNotFoundException("id", Integer.toString(id)));
    }

    public boolean existsByEmail(String email, Integer excludeId){
        if (excludeId == null) return userRepository.findByEmail(email).isPresent();
        else {
            Optional<User> user = userRepository.findByEmail(email);
            return user.filter(value -> value.getId() != excludeId).isPresent();
        }
    }

    @Transactional
    public int save(User user){
        completeUserCreation(user);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public User updateById(int id, User newUser){
        User oldUser = findById(id);
        completeUserUpdation(newUser, oldUser);
        userRepository.save(newUser);
        return newUser;
    }

    @Transactional
    public void deleteById(int id){
        findById(id);
        userRepository.deleteById(id);
    }



    private void completeUserCreation(User user) {
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    }
    private void completeUserUpdation(User newUser, User oldUser) {
        newUser.setId(oldUser.getId());
        newUser.setCreatedAt(oldUser.getCreatedAt());
    }

}
