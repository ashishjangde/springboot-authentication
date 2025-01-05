package com.ashish.authandsessionmanagment.utlis;

import com.ashish.authandsessionmanagment.entities.PostEntity;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostOwnByAuthor {

    private final PostRepository postRepository;

    public  boolean isPostOwnByAuthor(String postId){
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        return postEntity.getAuthor().getId().equals(userEntity.getId());
    }
}
