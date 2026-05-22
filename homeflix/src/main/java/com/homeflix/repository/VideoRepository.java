package com.homeflix.repository;

import com.homeflix.model.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Video> findByWatched(Boolean watched, Pageable pageable);

    Page<Video> findByFavorite(Boolean favorite, Pageable pageable);

    Page<Video> findByWatchedAndFavorite(Boolean watched, Boolean favorite, Pageable pageable);

    @Query("SELECT v FROM Video v JOIN v.categories c WHERE c.id = :categoryId")
    Page<Video> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT v FROM Video v JOIN v.categories c WHERE c.id = :categoryId AND v.watched = :watched")
    Page<Video> findByCategoryIdAndWatched(
        @Param("categoryId") Long categoryId,
        @Param("watched") Boolean watched,
        Pageable pageable
    );
}
