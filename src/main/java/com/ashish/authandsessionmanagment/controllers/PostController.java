package com.ashish.authandsessionmanagment.controllers;

import com.ashish.authandsessionmanagment.advices.ApiResponse;
import com.ashish.authandsessionmanagment.dto.PostDto;
import com.ashish.authandsessionmanagment.repositories.PostRepository;
import com.ashish.authandsessionmanagment.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {


    private final PostService postService;

    @GetMapping("/{slug}")
    public ResponseEntity<PostDto> getPostBySlug( @PathVariable String slug){
       PostDto postDto =  postService.getPostBySlug(slug);
         if(postDto == null) return ResponseEntity.notFound().build();
         return ResponseEntity.ok(postDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String id, @RequestBody PostDto postDto){
        PostDto post = postService.updatePostById(id, postDto);
        if(post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }

    @GetMapping()
    public ResponseEntity<List<PostDto>> getAllPosts(){
        List<PostDto> posts = postService.getAllPosts();
        if(posts == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable String authorId){
        List<PostDto> posts = postService.getPostsByAuthorId(authorId);
        if(posts == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(posts);
    }

    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto){
        PostDto post = postService.createPost(postDto);
        if(post == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deletePost(@PathVariable String id){
        boolean isDeleted = postService.deletePostById(id);
        if(!isDeleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
