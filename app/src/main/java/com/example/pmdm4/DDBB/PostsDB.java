package com.example.pmdm4.DDBB;

import androidx.room.Database;

import androidx.room.RoomDatabase;

import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;

@Database(entities = {Post.class, User.class}, version = 1)
public abstract class PostsDB extends RoomDatabase {
    public abstract PostsNUsersDAO getPostNUsersDAO();

}
