package com.example.myapp.repository;

import com.example.myapp.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long>{
    @Query("SELECT DISTINCT m.user2.userId FROM Match m WHERE m.user1.userId = :userId")
    List<Long> findAllMatchedUsers(@Param("userId") Long userId);
}
