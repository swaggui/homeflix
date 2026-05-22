package com.homeflix.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    private String title;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String description;

    private String filePath;

    private String coverImageUrl;

    private Integer releaseYear;

    private Double rating;

    private Integer durationMinutes;

    private Boolean watched;

    private Boolean favorite;

    private Set<Long> categoryIds;
}
