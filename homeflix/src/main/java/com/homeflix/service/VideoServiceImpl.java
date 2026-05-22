package com.homeflix.service;

import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.CategoryResponseDTO;
import com.homeflix.model.dto.VideoRequestDTO;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<VideoResponseDTO> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Override
    public VideoResponseDTO findById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo", id));
        return toResponseDTO(video);
    }

    @Override
    public Page<VideoResponseDTO> searchByTitle(String title, Pageable pageable) {
        return videoRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::toResponseDTO);
    }

    @Override
    public Page<VideoResponseDTO> filter(Boolean watched, Boolean favorite, Long categoryId, Pageable pageable) {

        if (categoryId != null && watched != null) {
            return videoRepository.findByCategoryIdAndWatched(categoryId, watched, pageable)
                    .map(this::toResponseDTO);
        }
        if (categoryId != null) {
            return videoRepository.findByCategoryId(categoryId, pageable)
                    .map(this::toResponseDTO);
        }


        if (watched != null && favorite != null) {
            return videoRepository.findByWatchedAndFavorite(watched, favorite, pageable)
                    .map(this::toResponseDTO);
        }
        if (watched != null) {
            return videoRepository.findByWatched(watched, pageable)
                    .map(this::toResponseDTO);
        }
        if (favorite != null) {
            return videoRepository.findByFavorite(favorite, pageable)
                    .map(this::toResponseDTO);
        }


        return videoRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public VideoResponseDTO create(VideoRequestDTO dto) {
        Video video = Video.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .filePath(dto.getFilePath())
                .coverImageUrl(dto.getCoverImageUrl())
                .releaseYear(dto.getReleaseYear())
                .rating(dto.getRating())
                .durationMinutes(dto.getDurationMinutes())
                .watched(dto.getWatched() != null ? dto.getWatched() : false)
                .favorite(dto.getFavorite() != null ? dto.getFavorite() : false)
                .categories(findCategories(dto.getCategoryIds()))
                .build();

        Video saved = videoRepository.save(video);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public VideoResponseDTO update(Long id, VideoRequestDTO dto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo", id));

        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setFilePath(dto.getFilePath());
        video.setCoverImageUrl(dto.getCoverImageUrl());
        video.setReleaseYear(dto.getReleaseYear());
        video.setRating(dto.getRating());
        video.setDurationMinutes(dto.getDurationMinutes());

        if (dto.getWatched() != null) {
            video.setWatched(dto.getWatched());
        }
        if (dto.getFavorite() != null) {
            video.setFavorite(dto.getFavorite());
        }
        if (dto.getCategoryIds() != null) {
            video.setCategories(findCategories(dto.getCategoryIds()));
        }

        Video saved = videoRepository.save(video);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public VideoResponseDTO toggleWatched(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo", id));
        video.setWatched(!video.getWatched());
        Video saved = videoRepository.save(video);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public VideoResponseDTO toggleFavorite(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo", id));
        video.setFavorite(!video.getFavorite());
        Video saved = videoRepository.save(video);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vídeo", id);
        }
        videoRepository.deleteById(id);
    }



    private Set<Category> findCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        return categoryIds.stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoryId)))
                .collect(Collectors.toSet());
    }

    private VideoResponseDTO toResponseDTO(Video video) {
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
                        .map(this::categoryToResponseDTO)
                        .collect(Collectors.toSet()))
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }

    private CategoryResponseDTO categoryToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .build();
    }
}
