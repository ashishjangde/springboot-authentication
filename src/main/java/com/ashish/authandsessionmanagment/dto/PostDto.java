package com.ashish.authandsessionmanagment.dto;


import com.ashish.authandsessionmanagment.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {

    private String id;

    private String slug;

    private String title;

    private String content;

    private UserEntity author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
