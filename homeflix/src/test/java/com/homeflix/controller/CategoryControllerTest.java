package com.homeflix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.exception.GlobalExceptionHandler;
import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.CategoryRequestDTO;
import com.homeflix.model.dto.CategoryResponseDTO;
import com.homeflix.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/categories - Deve retornar lista de categorias")
    void findAll_DeveRetornar200ComLista() throws Exception {
        List<CategoryResponseDTO> categories = Arrays.asList(
                CategoryResponseDTO.builder().id(1L).name("Ação").color("#E53935").build(),
                CategoryResponseDTO.builder().id(2L).name("Comédia").color("#FDD835").build()
        );
        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ação"))
                .andExpect(jsonPath("$[1].name").value("Comédia"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Deve retornar categoria por ID")
    void findById_DeveRetornar200() throws Exception {
        CategoryResponseDTO dto = CategoryResponseDTO.builder()
                .id(1L).name("Ação").description("Filmes de ação").color("#E53935").build();
        when(categoryService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ação"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Deve retornar 404 quando não encontrada")
    void findById_DeveRetornar404() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new ResourceNotFoundException("Categoria", 99L));

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/categories - Deve criar categoria e retornar 201")
    void create_DeveRetornar201() throws Exception {
        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("Terror").description("Filmes de terror").color("#6A1B9A").build();
        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(1L).name("Terror").description("Filmes de terror").color("#6A1B9A").build();

        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Terror"));
    }

    @Test
    @DisplayName("POST /api/categories - Deve retornar 400 com nome vazio")
    void create_DeveRetornar400SemNome() throws Exception {
        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("").description("Sem nome").build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - Deve atualizar categoria")
    void update_DeveRetornar200() throws Exception {
        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("Ação Atualizada").description("Nova desc").color("#FF0000").build();
        CategoryResponseDTO response = CategoryResponseDTO.builder()
                .id(1L).name("Ação Atualizada").description("Nova desc").color("#FF0000").build();

        when(categoryService.update(eq(1L), any(CategoryRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ação Atualizada"));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - Deve deletar e retornar 204")
    void delete_DeveRetornar204() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
