package com.example.myapp.service;

import com.example.myapp.dto.SwipeDto;
import com.example.myapp.model.*;
import com.example.myapp.repository.GendersRepository;
import com.example.myapp.repository.SwipesRepository;
import com.example.myapp.repository.UserInfoRepository;
import com.example.myapp.response.HomeResponse;
import com.example.myapp.response.LoadMessagesResponse;
import org.springframework.stereotype.Service;
import com.example.myapp.repository.MatchRepository;


import java.util.*;

@Service
public class HomeService {

    private final SwipesRepository swipesRepository;
    private final UserInfoRepository userInfoRepository;
    private final MatchRepository matchRepository;

    public HomeService(SwipesRepository swipesRepository, UserInfoRepository userInfoRepository, MatchRepository matchRepository) {
        this.swipesRepository = swipesRepository;
        this.userInfoRepository = userInfoRepository;
        this.matchRepository = matchRepository;
    }

    public List<HomeResponse> loadUsers(String userEmail) {
        Optional<UserInfo> maybeUserInfo = userInfoRepository.findByUserEmail(userEmail);

        if (maybeUserInfo.isPresent()) {
            UserInfo userInfo = maybeUserInfo.get();
            long userId = userInfo.getUserId();
            Set<Genders> orientations = userInfo.getOrientations();

            // Handle empty orientations

            List<Long> swipedUsers = swipesRepository.findSwipedUserIdsBySwiper(userId);


            // Handle empty swipedUsers list for query compatibility
            if (swipedUsers.isEmpty()) {
                swipedUsers = List.of(-1L); // dummy ID that won't match any real user
            }

            List<UserInfo> usersToLoad = swipesRepository.findEligibleUsers(userId, swipedUsers);

            List<HomeResponse> homeResponses = new ArrayList<>();

            // Load up to 5 users, but don't exceed available users
            int maxUsers = Math.min(5, usersToLoad.size());

            for(int i = 0; i < maxUsers; i++){
                UserInfo userToLoad = usersToLoad.get(i);

                // Null check
                if (userToLoad.getUser() == null) {
                    continue; // Skip this user if User entity is null
                }

                User user = userToLoad.getUser();
                String username = user.getFirstName();
                String email = user.getUsername();

                Set<String> userPhotos = new HashSet<>();

                for(UserPics userPics : userToLoad.getUserPics()){
                    userPhotos.add(userPics.getPhotoUrl());
                }

                HomeResponse homeResponse = new HomeResponse(email, username, userToLoad.getAge(), userToLoad.getGender(), userPhotos, null);
                homeResponses.add(homeResponse);
            }

            return homeResponses;


        } else {
            throw new RuntimeException("User Info not found");
        }
    }

    public String saveSwipes(SwipeDto input){

        String swiperEmail = input.getSwiperEmail();
        String swipeeEmail = input.getSwipeeEmail();
        Boolean liked = input.getLiked();

        Optional<UserInfo> maybeSwiper = userInfoRepository.findByUserEmail(swiperEmail);
        Optional<UserInfo> maybeSwipee = userInfoRepository.findByUserEmail(swipeeEmail);

        if(maybeSwiper.isPresent() && maybeSwipee.isPresent()){
            UserInfo swiper = maybeSwiper.get();
            UserInfo swipee = maybeSwipee.get();

            Swipes swipes = new Swipes();
            swipes.setSwiper(swiper);
            swipes.setSwipee(swipee);
            swipes.setLiked(liked);

            swipesRepository.save(swipes);

            if(liked){

                List<Long> swipedByswipee = swipesRepository.findSwipedUserIdsBySwiper(swipee.getUserId());

                for (Long swipedUserId : swipedByswipee) {
                    if (swipedUserId == swiper.getUserId()) {

                        Boolean beingLiked = swipesRepository.findLikedStatus(swipee.getUserId(), swipedUserId);

                        if(beingLiked) {
                            Match match = new Match();
                            match.setUser1(swiper);
                            match.setUser2(swipee);
                            matchRepository.save(match);

                            Match match2 = new Match();
                            match2.setUser1(swipee);
                            match2.setUser2(swiper);
                            matchRepository.save(match2);
                        }

                        return "Matched";
                    }
                }

                return "Not matched";
            }

            return "Swiped";
        }
        else{
            throw new RuntimeException("User Info not found");
        }
    }


}
