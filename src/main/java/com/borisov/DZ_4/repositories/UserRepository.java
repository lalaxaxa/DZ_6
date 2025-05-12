package com.borisov.DZ_4.repositories;

import com.borisov.DZ_4.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /*@Query("""
            select u.id
            from User u
            where u.email= :email
            and (:excludeId is null or u.id <> :excludeId)
            """)
    Optional<Integer> findUserIdByEmail(@Param("email") String email, @Param("excludeId") Integer excludeId);*/
    Optional<User> findByEmail(String email);


}
