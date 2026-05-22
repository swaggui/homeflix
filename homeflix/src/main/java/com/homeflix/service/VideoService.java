package com.homeflix.service;

import com.homeflix.model.dto.VideoRequestDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VideoService {

    Page<VideoResponseDTO> findAll(Pageable pageable);

    VideoResponseDTO findById(Long id);

    Page<VideoResponseDTO> searchByTitle(String title, Pageable pageable);

    Page<VideoResponseDTO> filter(Boolean watched, Boolean favorite, Long categoryId, Pageable pageable);

    VideoResponseDTO create(VideoRequestDTO dto);

    VideoResponseDTO update(Long id, VideoRequestDTO dto);

    VideoResponseDTO toggleWatched(Long id);

    VideoResponseDTO toggleFavorite(Long id);

    void delete(Long id);
}
