package com.example.pmdm4.core;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UsersAndPosts {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    public List<Post> posts;
}
