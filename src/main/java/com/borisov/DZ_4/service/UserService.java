package com.borisov.DZ_4.service;

import com.borisov.DZ_4.models.User;
import com.borisov.DZ_4.repositories.UserRepository;
import com.borisov.DZ_4.util.UserEmailAlreadyExistException;
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

    public Optional<Integer> findUserIdByEmail(String email){
        return userRepository.findUserIdByEmail(email);
    }

    @Transactional
    public int save(User user){
        enrichUser(user);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public User updateById(int id, User updatedUser){
        findById(id);
        updatedUser.setId(id);
        userRepository.save(updatedUser);
        return updatedUser;
    }

    @Transactional
    public void deleteById(int id){
        findById(id);
        userRepository.deleteById(id);
    }

    public void isEmailAlreadyExistThrowException(String email, Optional<Integer> updatedId){
        boolean isUpdateMode = updatedId.isPresent();
        boolean isEmailAlreadyExist;

        Optional<Integer> idSameEmail = findUserIdByEmail(email);
        if (!isUpdateMode){
            //create
            isEmailAlreadyExist = idSameEmail.isPresent();
        }else {
            //update
            isEmailAlreadyExist =  idSameEmail.isPresent() && !updatedId.get().equals(idSameEmail.get());
        }
        if (isEmailAlreadyExist) throw new UserEmailAlreadyExistException(email);
    }





    private void enrichUser(User user) {
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    }

}
