package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Long> {

    // 基本查詢
    Optional<MovieCategory> findByCategoryName(String categoryName);

    // 電影數量統計
    @Query("SELECT mc.categoryName, COUNT(m) " +
            "FROM MovieCategory mc LEFT JOIN mc.movies m " +
            "GROUP BY mc.categoryId")
    List<Object[]> getCategoryMovieCounts();

    // 活躍類別查詢
    @Query("SELECT DISTINCT mc FROM MovieCategory mc " +
            "JOIN mc.movies m " +
            "WHERE m.movieStatus = 'SHOWING'")
    List<MovieCategory> findActiveCategories();

    // 特定電影的類別
    @Query("SELECT mc FROM MovieCategory mc " +
            "JOIN mc.movies m " +
            "WHERE m.movieId = :movieId")
    Optional<MovieCategory> findByMovieId(@Param("movieId") Long movieId);

    // 複合查詢
    @Query("SELECT mc FROM MovieCategory mc " +
            "WHERE mc.categoryId IN " +
            "(SELECT m.category.categoryId FROM Movie m " +
            "WHERE m.movieStatus = 'SHOWING' " +
            "GROUP BY m.category.categoryId " +
            "HAVING COUNT(m) >= :minMovies)")
    List<MovieCategory> findPopularCategories(@Param("minMovies") long minMovies);
}