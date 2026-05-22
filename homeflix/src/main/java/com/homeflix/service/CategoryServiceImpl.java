package com.homeflix.service;

import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.CategoryRequestDTO;
import com.homeflix.model.dto.CategoryResponseDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import com.homeflix.model.entity.Category;
import com.homeflix.model.entity.Video;
import com.homeflix.repository.CategoryRepository;
import com.homeflix.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final VideoRepository videoRepository;

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
        return toResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + dto.getName());
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .color(dto.getColor())
                .build();

        Category saved = categoryRepository.save(category);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());

        Category saved = categoryRepository.save(category);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Page<VideoResponseDTO> findVideosByCategoryId(Long id, Pageable pageable) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", id);
        }
        return videoRepository.findByCategoryId(id, pageable)
                .map(this::videoToResponseDTO);
    }


    private CategoryResponseDTO toResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .build();
    }

    private VideoResponseDTO videoToResponseDTO(Video video) {
        return VideoResponseDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .filePath(video.getFilePath())
                .coverImageUrl(video.getCoverImageUrl())
                .releaseYear(video.getReleaseYear())
                .rating(video.getRating())
                .durationMinutes(video.getDurationMinutes())
                .watched(video.getWatched())
                .favorite(video.getFavorite())
                .categories(video.getCategories().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toSet()))
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }
}
