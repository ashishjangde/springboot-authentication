package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.dto.PostDto;
import com.ashish.authandsessionmanagment.entities.PostEntity;
import com.ashish.authandsessionmanagment.exceptions.ResourceNotFoundException;
import com.ashish.authandsessionmanagment.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostDto getPostBySlug(String slug) {
       PostEntity post = postRepository.findBySlug(slug)
               .orElseThrow(()-> new ResourceNotFoundException("Post not found"));
     return  modelMapper.map(post, PostDto.class);
    }

    public PostDto updatePostById(String id, PostDto postDto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found"));
       PostEntity postToBeSaved = modelMapper.map(postDto, PostEntity.class);
         postToBeSaved.setId(post.getId());
        PostEntity savedPost = postRepository.save(postToBeSaved);
        return modelMapper.map(savedPost, PostDto.class);

    }

    public List<PostDto> getAllPosts() {
    return null;
    }

    public List<PostDto> getPostsByAuthorId(String authorId) {
        return null;
    }

    public PostDto createPost(PostDto postDto) {
        return null;
    }

    public boolean deletePostById(String id) {
        return false;
    }
}
