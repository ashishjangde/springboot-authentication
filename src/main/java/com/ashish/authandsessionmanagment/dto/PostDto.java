package com.ashish.authandsessionmanagment.dto;


import com.ashish.authandsessionmanagment.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;


import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {

    private String id;

    private String slug;

    private String title;

    private String content;

    private UserDto author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
