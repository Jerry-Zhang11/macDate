package com.example.myapp.repository;

import com.example.myapp.model.Genders;
import com.example.myapp.model.Swipes;
import com.example.myapp.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SwipesRepository extends JpaRepository<Swipes, Long> {

    @Query("SELECT DISTINCT s.swipee.userId FROM Swipes s WHERE s.swiper.userId = :swiperId")
    List<Long> findSwipedUserIdsBySwiper(@Param("swiperId") Long swiperId);

    @Query("""
        SELECT ui FROM UserInfo ui 
        WHERE ui.userId != :currentUserId 
        AND ui.userId NOT IN :swipedUserIds 
        """)
    List<UserInfo> findEligibleUsers(
            @Param("currentUserId") Long currentUserId,
            @Param("swipedUserIds") List<Long> swipedUserIds
    );

    @Query("SELECT s.liked FROM Swipes s WHERE s.swiper.userId = :swiperId AND s.swipee.userId = :swipeeId")
    Boolean findLikedStatus(@Param("swiperId") Long swiperId, @Param("swipeeId") Long swipeeId);
}
