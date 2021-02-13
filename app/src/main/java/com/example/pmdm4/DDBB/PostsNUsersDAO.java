package com.example.pmdm4.DDBB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;
import com.example.pmdm4.core.UsersAndPosts;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PostsNUsersDAO {

    //Posts querys
    @Insert
    void addPost(Post post);

    @Update
    void updatePost(Post post);

    @Delete
    void deletePost(Post post);

    @Query("SELECT * FROM post WHERE id LIKE :id")
    Post getPost(int id);

    @Query("SELECT * FROM post")
    List<Post> getPosts();

    @Query("DELETE  FROM post")
    void deleteAllPosts();


    //Users querys
    @Insert
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM user WHERE id LIKE :id")
    User getUser(int id);

    @Query("SELECT * FROM user")
    List<User> getUsers();

    @Query("DELETE  FROM user")
    void deleteAllUers();

    @Query("SELECT id FROM user WHERE name LIKE :nombre")
    int getUserId(String nombre);

    @Query("SELECT name FROM user WHERE id LIKE :id")
    String getUserName(int id);


    //User y Posts
    @Transaction
    @Query("SELECT * FROM User")
    List<UsersAndPosts> getUsersWithPosts();
}
