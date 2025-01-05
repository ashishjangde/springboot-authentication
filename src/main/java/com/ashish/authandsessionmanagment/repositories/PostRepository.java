package com.ashish.authandsessionmanagment.repositories;

import com.ashish.authandsessionmanagment.entities.PostEntity;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, String> {

    Optional<PostEntity> findBySlug(String slug);
    List<PostEntity> findAllByAuthor(UserEntity author);

    PostEntity getPostEntityBySlug(String slug);

}

