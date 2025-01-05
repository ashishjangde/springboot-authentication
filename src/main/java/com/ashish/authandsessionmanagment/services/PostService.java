package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.PostCreateDto;
import com.ashish.authandsessionmanagment.dto.PostDto;
import com.ashish.authandsessionmanagment.entities.PostEntity;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.exceptions.ResourceNotFoundException;
import com.ashish.authandsessionmanagment.repositories.PostRepository;
import com.ashish.authandsessionmanagment.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public PostDto getPostBySlug(String slug) {
       PostEntity post = postRepository.findBySlug(slug)
               .orElseThrow(()-> new ResourceNotFoundException("Post not found"));
     return  modelMapper.map(post, PostDto.class);
    }

    public PostDto updatePostById(String id, PostCreateDto postCreateDto) {
        UserEntity author = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       PostEntity postToBeSaved = modelMapper.map(postCreateDto , PostEntity.class);
       postToBeSaved.setId(id);
       postToBeSaved.setAuthor(author);
        PostEntity savedPost = postRepository.save(postToBeSaved);
        return modelMapper.map(savedPost, PostDto.class);

    }

    public List<PostDto> getAllPosts() {
       UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PostEntity> posts = postRepository.findAllByAuthor(userEntity);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }

    public List<PostDto> getPostsByAuthorId(String authorId) {
        UserEntity userEntity = userRepository.findById(authorId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        List<PostEntity> posts = postRepository.findAllByAuthor(userEntity);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }

    public PostDto createPost(PostCreateDto postDto)  {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostEntity existingPost = postRepository.getPostEntityBySlug(postDto.getSlug());
        if (existingPost != null) throw new RuntimeException("Post exist with this slug try to use another slug");
        PostEntity postEntity = modelMapper.map(postDto, PostEntity.class);
        postEntity.setAuthor(userEntity);
        PostEntity savedPost = postRepository.save(postEntity);
        return modelMapper.map(savedPost, PostDto.class);
    }

    public boolean deletePostById(String id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found"));
        postRepository.delete(post);
        return true;
    }
}
