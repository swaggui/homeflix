package com.homeflix.service;

import com.homeflix.model.dto.CategoryRequestDTO;
import com.homeflix.model.dto.CategoryResponseDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO findById(Long id);

    CategoryResponseDTO create(CategoryRequestDTO dto);

    CategoryResponseDTO update(Long id, CategoryRequestDTO dto);

    void delete(Long id);

    Page<VideoResponseDTO> findVideosByCategoryId(Long id, Pageable pageable);
}
