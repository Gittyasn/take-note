package com.mdtalalwasim.takenote.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class NoteDTO {
    private int id;
    
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;
    
    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;
    
    private LocalDate createdAt;
    private boolean favorite;
    private Set<String> tags;
}
