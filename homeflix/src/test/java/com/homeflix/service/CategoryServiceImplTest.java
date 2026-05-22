package com.homeflix.service;

import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.CategoryRequestDTO;
import com.homeflix.model.dto.CategoryResponseDTO;
import com.homeflix.model.entity.Category;
import com.homeflix.repository.CategoryRepository;
import com.homeflix.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Ação")
                .description("Filmes de ação")
                .color("#E53935")
                .build();

        requestDTO = CategoryRequestDTO.builder()
                .name("Ação")
                .description("Filmes de ação")
                .color("#E53935")
                .build();
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void findAll_DeveRetornarListaDeCategorias() {
        Category category2 = Category.builder()
                .id(2L).name("Comédia").description("Filmes de comédia").color("#FDD835").build();

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));

        List<CategoryResponseDTO> result = categoryService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Ação");
        assertThat(result.get(1).getName()).isEqualTo("Comédia");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar categoria por ID com sucesso")
    void findById_DeveRetornarCategoria() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponseDTO result = categoryService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ação");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada")
    void findById_DeveLancarExcecaoQuandoNaoEncontrada() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void create_DeveCriarCategoria() {
        when(categoryRepository.existsByNameIgnoreCase("Ação")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.create(requestDTO);

        assertThat(result.getName()).isEqualTo("Ação");
        assertThat(result.getColor()).isEqualTo("#E53935");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void create_DeveLancarExcecaoQuandoNomeDuplicado() {
        when(categoryRepository.existsByNameIgnoreCase("Ação")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe");
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void update_DeveAtualizarCategoria() {
        CategoryRequestDTO updateDTO = CategoryRequestDTO.builder()
                .name("Ação Atualizada")
                .description("Nova descrição")
                .color("#FF0000")
                .build();

        Category updatedCategory = Category.builder()
                .id(1L).name("Ação Atualizada").description("Nova descrição").color("#FF0000").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponseDTO result = categoryService.update(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Ação Atualizada");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void delete_DeveDeletarCategoria() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria inexistente")
    void delete_DeveLancarExcecaoQuandoNaoExistir() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
