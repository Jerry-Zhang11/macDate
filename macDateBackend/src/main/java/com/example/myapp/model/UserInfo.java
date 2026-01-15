package com.example.myapp.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_info")
@ToString(exclude = {"userPics", "swipesMade", "swipesAt", "matchMade", "matchWith", "user", "orientations"})
@EqualsAndHashCode(exclude = {"userPics", "swipesMade", "swipesAt", "matchMade", "matchWith", "user", "orientations"})
public class UserInfo {
    @Id
    @Column(name="user_id")
    private long userId;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @JsonIgnore
    @MapsId
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name="user_orientation",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "gender_id")
    )
    private Set<Genders> orientations = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="userInfo", fetch = FetchType.LAZY)
    private Set<UserPics> userPics = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="swiper")
    private Set<Swipes> swipesMade = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="swipee")
    private Set<Swipes> swipesAt = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="user1")
    private Set<Match> matchMade = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy="user2")
    private Set<Match> matchWith = new HashSet<>();

    public UserInfo(long userId, int age, Gender gender) {
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.orientations = new HashSet<>();
        this.userPics = new HashSet<>();
    }

    public UserInfo(long userId, Gender gender) {
        this.userId = userId;
        this.gender = gender;
        this.orientations = new HashSet<>();
        this.userPics = new HashSet<>();
    }

    public void addOrientations(Genders gender) {
        orientations.add(gender);
    }

    public void removeAllOrientations() {
        orientations.clear();
    }
}