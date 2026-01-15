package com.example.myapp.service;

import java.util.Optional;

import com.example.myapp.dto.UserAgeDto;
import com.example.myapp.dto.UserGenderDto;
import com.example.myapp.dto.UserOrientationsDto;
import com.example.myapp.model.Gender;
import com.example.myapp.model.Genders;
import com.example.myapp.model.User;
import com.example.myapp.model.UserInfo;
import com.example.myapp.model.UserPics;
import com.example.myapp.repository.GendersRepository;
import com.example.myapp.repository.UserInfoRepository;
import com.example.myapp.repository.UserPicsRepository;
import com.example.myapp.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.UUID;
import java.io.IOException;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class SignupService{
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;
    private final GendersRepository gendersRepository;
    private final UserPicsRepository userPicsRepository;
    private final S3Client s3Client;

    public SignupService(UserInfoRepository userInfoRepository, UserRepository userRepository, GendersRepository gendersRepository, UserPicsRepository userPicsRepository, S3Client s3Client){
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
        this.gendersRepository = gendersRepository;
        this.userPicsRepository = userPicsRepository;
        this.s3Client = s3Client;
    }

    @Transactional
    public void saveUserGender(UserGenderDto input){
        Optional<User> maybeUser = userRepository.findByEmail(input.getEmail());
        if(maybeUser.isPresent()){
            User user = maybeUser.get();
            Optional<UserInfo> maybeUserInfo = userInfoRepository.findByUserEmail(input.getEmail());
            if(maybeUserInfo.isPresent()){
                UserInfo userInfo = maybeUserInfo.get();
                userInfo.setGender(input.getGender());
                userInfoRepository.save(userInfo);
            }
            else{
                UserInfo userInfo = new UserInfo();
                userInfo.setGender(input.getGender());
                userInfo.setUser(user);

                userInfoRepository.save(userInfo);
            }

        }
        else{
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void saveUserAge(UserAgeDto input){
        Optional<UserInfo> maybeUserInfo = userInfoRepository.findByUserEmail(input.getEmail());
        if(maybeUserInfo.isPresent()){
            UserInfo userInfo = maybeUserInfo.get();

            userInfo.setAge(input.getAge());
            userInfoRepository.save(userInfo);
        }
        else{
            throw new RuntimeException("User Info not found");
        }
    }

    @Transactional
    public void saveUserOrientations(UserOrientationsDto input){
        Optional<UserInfo> maybeUserInfo = userInfoRepository.findByUserEmail(input.getEmail());
        if(maybeUserInfo.isPresent()){
            UserInfo userInfo = maybeUserInfo.get();
            userInfo.removeAllOrientations();
            for (Gender gender : input.getOrientations()){
                Optional<Genders> maybeGender = gendersRepository.findByGender(gender);
                if(maybeGender.isPresent()){
                    Genders orientation = maybeGender.get();
                    userInfo.addOrientations(orientation);
                }
                else{
                    throw new RuntimeException("Enter correct Gender"); }
            }
            userInfoRepository.save(userInfo);
        }
        else{
            throw new RuntimeException("User Info not found");
        }
    }


    @Transactional
    public void saveUserPics(String email, MultipartFile[] photos) throws IOException{
        Optional<UserInfo> maybeUserInfo = userInfoRepository.findByUserEmail(email);
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if(maybeUserInfo.isPresent() && maybeUser.isPresent()){
            UserInfo userInfo = maybeUserInfo.get();
            for (MultipartFile photo : photos){
                String filename = saveFile(photo);
                String originalFilename = photo.getOriginalFilename();
                UserPics userPics = new UserPics();
                String photoUrl = "https://macdate-user-photos.s3.amazonaws.com/" + filename;

                userPics.setPhotoUrl(photoUrl);
                userPics.setOriginalFilename(originalFilename);
                userPics.setUserInfo(userInfo);
                userPicsRepository.save(userPics);

            }
            User user = maybeUser.get();
            user.setIsNewUser(false);
            userRepository.save(user);
        }
        else{
            throw new RuntimeException("User Info not found");
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        s3Client.putObject(
            PutObjectRequest.builder()
                    .bucket("macdate-user-photos")
                    .key(uniqueFilename)
                    .contentType(file.getContentType())
                    .build(),
            RequestBody.fromBytes(file.getBytes())
        );

        return uniqueFilename;
    }



}
