package com.ashish.authandsessionmanagment.repositories;

import com.ashish.authandsessionmanagment.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.posts p WHERE u.id = :userId AND p.id = :postId")
    Optional<UserEntity> findByIdWithSpecificPost(@Param("userId") String userId, @Param("postId") String postId);


}
