package com.homeflix.controller;

import com.homeflix.model.dto.VideoRequestDTO;
import com.homeflix.model.dto.VideoResponseDTO;
import com.homeflix.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Tag(name = "Vídeos", description = "Gerenciamento do catálogo de vídeos")
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    @Operation(summary = "Listar todos os vídeos", description = "Retorna lista paginada de vídeos. Use page, size e sort como parâmetros.")
    public ResponseEntity<Page<VideoResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(videoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vídeo por ID")
    public ResponseEntity<VideoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar vídeos por título", description = "Busca case-insensitive por parte do título")
    public ResponseEntity<Page<VideoResponseDTO>> searchByTitle(
            @RequestParam String title,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(videoService.searchByTitle(title, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtrar vídeos", description = "Filtra por watched, favorite e/ou categoryId")
    public ResponseEntity<Page<VideoResponseDTO>> filter(
            @RequestParam(required = false) Boolean watched,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(videoService.filter(watched, favorite, categoryId, pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo vídeo")
    public ResponseEntity<VideoResponseDTO> create(@Valid @RequestBody VideoRequestDTO dto) {
        VideoResponseDTO created = videoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar vídeo")
    public ResponseEntity<VideoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody VideoRequestDTO dto) {
        return ResponseEntity.ok(videoService.update(id, dto));
    }

    @PatchMapping("/{id}/watched")
    @Operation(summary = "Marcar/desmarcar como assistido")
    public ResponseEntity<VideoResponseDTO> toggleWatched(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.toggleWatched(id));
    }

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Marcar/desmarcar como favorito")
    public ResponseEntity<VideoResponseDTO> toggleFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.toggleFavorite(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover vídeo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
