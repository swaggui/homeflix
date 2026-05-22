package com.homeflix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.VideoRequestDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import com.homeflix.service.VideoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    @Autowired
    private ObjectMapper objectMapper;

    private VideoResponseDTO createVideoResponse() {
        return VideoResponseDTO.builder()
                .id(1L)
                .title("Interestelar")
                .description("Filme de ficção científica")
                .releaseYear(2014)
                .rating(9.0)
                .durationMinutes(169)
                .watched(false)
                .favorite(true)
                .categories(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/videos - Deve retornar página de vídeos")
    void findAll_DeveRetornar200() throws Exception {
        Page<VideoResponseDTO> page = new PageImpl<>(List.of(createVideoResponse()));
        when(videoService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Interestelar"))
                .andExpect(jsonPath("$.content[0].rating").value(9.0));
    }

    @Test
    @DisplayName("GET /api/videos/{id} - Deve retornar vídeo por ID")
    void findById_DeveRetornar200() throws Exception {
        when(videoService.findById(1L)).thenReturn(createVideoResponse());

        mockMvc.perform(get("/api/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Interestelar"));
    }

    @Test
    @DisplayName("GET /api/videos/{id} - Deve retornar 404")
    void findById_DeveRetornar404() throws Exception {
        when(videoService.findById(99L)).thenThrow(new ResourceNotFoundException("Vídeo", 99L));

        mockMvc.perform(get("/api/videos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/videos/search - Deve buscar por título")
    void searchByTitle_DeveRetornar200() throws Exception {
        Page<VideoResponseDTO> page = new PageImpl<>(List.of(createVideoResponse()));
        when(videoService.searchByTitle(eq("inter"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/videos/search").param("title", "inter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Interestelar"));
    }

    @Test
    @DisplayName("GET /api/videos/filter - Deve filtrar por watched")
    void filter_DeveRetornar200() throws Exception {
        Page<VideoResponseDTO> page = new PageImpl<>(List.of(createVideoResponse()));
        when(videoService.filter(eq(false), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/videos/filter").param("watched", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].watched").value(false));
    }

    @Test
    @DisplayName("POST /api/videos - Deve criar vídeo e retornar 201")
    void create_DeveRetornar201() throws Exception {
        VideoRequestDTO request = VideoRequestDTO.builder()
                .title("Interestelar")
                .description("Filme de ficção científica")
                .releaseYear(2014)
                .rating(9.0)
                .build();

        when(videoService.create(any(VideoRequestDTO.class))).thenReturn(createVideoResponse());

        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Interestelar"));
    }

    @Test
    @DisplayName("POST /api/videos - Deve retornar 400 sem título")
    void create_DeveRetornar400SemTitulo() throws Exception {
        VideoRequestDTO request = VideoRequestDTO.builder()
                .title("")
                .description("Sem título")
                .build();

        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/videos/{id}/watched - Deve alternar assistido")
    void toggleWatched_DeveRetornar200() throws Exception {
        VideoResponseDTO toggled = createVideoResponse();
        toggled.setWatched(true);
        when(videoService.toggleWatched(1L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/videos/1/watched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watched").value(true));
    }

    @Test
    @DisplayName("PATCH /api/videos/{id}/favorite - Deve alternar favorito")
    void toggleFavorite_DeveRetornar200() throws Exception {
        VideoResponseDTO toggled = createVideoResponse();
        toggled.setFavorite(false);
        when(videoService.toggleFavorite(1L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/videos/1/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false));
    }

    @Test
    @DisplayName("DELETE /api/videos/{id} - Deve deletar e retornar 204")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(videoService).delete(1L);

        mockMvc.perform(delete("/api/videos/1"))
                .andExpect(status().isNoContent());
    }
}
