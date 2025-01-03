package com.ashish.authandsessionmanagment.repositories;

import com.ashish.authandsessionmanagment.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, String> {

    Optional<PostEntity> findBySlug(String slug);
}
