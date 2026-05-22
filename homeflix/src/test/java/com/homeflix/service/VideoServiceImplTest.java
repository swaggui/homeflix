package com.homeflix.service;

import com.homeflix.exception.ResourceNotFoundException;
import com.homeflix.model.dto.VideoRequestDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import com.homeflix.model.entity.Category;
import com.homeflix.model.entity.Video;
import com.homeflix.repository.CategoryRepository;
import com.homeflix.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    private Video video;
    private VideoRequestDTO requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        video = Video.builder()
                .id(1L)
                .title("Interestelar")
                .description("Filme de ficção científica")
                .filePath("/videos/interestelar.mp4")
                .releaseYear(2014)
                .rating(9.0)
                .durationMinutes(169)
                .watched(false)
                .favorite(true)
                .categories(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = VideoRequestDTO.builder()
                .title("Interestelar")
                .description("Filme de ficção científica")
                .filePath("/videos/interestelar.mp4")
                .releaseYear(2014)
                .rating(9.0)
                .durationMinutes(169)
                .watched(false)
                .favorite(true)
                .categoryIds(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Deve listar vídeos com paginação")
    void findAll_DeveRetornarPaginaDeVideos() {
        Page<Video> page = new PageImpl<>(List.of(video));
        when(videoRepository.findAll(pageable)).thenReturn(page);

        Page<VideoResponseDTO> result = videoService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Interestelar");
        verify(videoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar vídeo por ID com sucesso")
    void findById_DeveRetornarVideo() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        VideoResponseDTO result = videoService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Interestelar");
    }

    @Test
    @DisplayName("Deve lançar exceção quando vídeo não encontrado")
    void findById_DeveLancarExcecaoQuandoNaoEncontrado() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve buscar vídeos por título")
    void searchByTitle_DeveRetornarVideosPorTitulo() {
        Page<Video> page = new PageImpl<>(List.of(video));
        when(videoRepository.findByTitleContainingIgnoreCase("inter", pageable)).thenReturn(page);

        Page<VideoResponseDTO> result = videoService.searchByTitle("inter", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Interestelar");
    }

    @Test
    @DisplayName("Deve filtrar vídeos por watched")
    void filter_DeveFiltrarPorWatched() {
        Page<Video> page = new PageImpl<>(List.of(video));
        when(videoRepository.findByWatched(false, pageable)).thenReturn(page);

        Page<VideoResponseDTO> result = videoService.filter(false, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(videoRepository).findByWatched(false, pageable);
    }

    @Test
    @DisplayName("Deve filtrar vídeos por favorite")
    void filter_DeveFiltrarPorFavorite() {
        Page<Video> page = new PageImpl<>(List.of(video));
        when(videoRepository.findByFavorite(true, pageable)).thenReturn(page);

        Page<VideoResponseDTO> result = videoService.filter(null, true, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(videoRepository).findByFavorite(true, pageable);
    }

    @Test
    @DisplayName("Deve criar vídeo com sucesso")
    void create_DeveCriarVideo() {
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        VideoResponseDTO result = videoService.create(requestDTO);

        assertThat(result.getTitle()).isEqualTo("Interestelar");
        assertThat(result.getRating()).isEqualTo(9.0);
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Deve criar vídeo com categorias")
    void create_DeveCriarVideoComCategorias() {
        Category category = Category.builder().id(1L).name("Ação").build();
        Set<Long> categoryIds = new HashSet<>(Set.of(1L));
        requestDTO.setCategoryIds(categoryIds);

        Video videoWithCategory = Video.builder()
                .id(1L).title("Interestelar").categories(Set.of(category))
                .watched(false).favorite(true)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(videoRepository.save(any(Video.class))).thenReturn(videoWithCategory);

        VideoResponseDTO result = videoService.create(requestDTO);

        assertThat(result.getCategories()).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar vídeo com sucesso")
    void update_DeveAtualizarVideo() {
        VideoRequestDTO updateDTO = VideoRequestDTO.builder()
                .title("Interestelar - Director's Cut")
                .description("Versão do diretor")
                .durationMinutes(180)
                .build();

        Video updatedVideo = Video.builder()
                .id(1L).title("Interestelar - Director's Cut").description("Versão do diretor")
                .durationMinutes(180).watched(false).favorite(true)
                .categories(new HashSet<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(updatedVideo);

        VideoResponseDTO result = videoService.update(1L, updateDTO);

        assertThat(result.getTitle()).isEqualTo("Interestelar - Director's Cut");
    }

    @Test
    @DisplayName("Deve marcar/desmarcar como assistido")
    void toggleWatched_DeveAlternarWatched() {
        Video toggledVideo = Video.builder()
                .id(1L).title("Interestelar").watched(true).favorite(true)
                .categories(new HashSet<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(toggledVideo);

        VideoResponseDTO result = videoService.toggleWatched(1L);

        assertThat(result.getWatched()).isTrue();
    }

    @Test
    @DisplayName("Deve marcar/desmarcar como favorito")
    void toggleFavorite_DeveAlternarFavorite() {
        Video toggledVideo = Video.builder()
                .id(1L).title("Interestelar").watched(false).favorite(false)
                .categories(new HashSet<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(toggledVideo);

        VideoResponseDTO result = videoService.toggleFavorite(1L);

        assertThat(result.getFavorite()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar vídeo com sucesso")
    void delete_DeveDeletarVideo() {
        when(videoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(videoRepository).deleteById(1L);

        videoService.delete(1L);

        verify(videoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar vídeo inexistente")
    void delete_DeveLancarExcecaoQuandoNaoExistir() {
        when(videoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> videoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
