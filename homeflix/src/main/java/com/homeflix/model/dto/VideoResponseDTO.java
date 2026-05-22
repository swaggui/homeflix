package com.homeflix.model.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String coverImageUrl;
    private Integer releaseYear;
    private Double rating;
    private Integer durationMinutes;
    private Boolean watched;
    private Boolean favorite;
    private Set<CategoryResponseDTO> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
