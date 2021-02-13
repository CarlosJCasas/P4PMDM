package com.example.pmdm4.DDBB;

import android.content.Context;

import androidx.room.Room;

import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;
import com.example.pmdm4.core.UsersAndPosts;

import java.util.List;

public class PostsLab implements PostsNUsersDAO{
    private static PostsLab myPostsLab;
    private PostsNUsersDAO myDAO;


    public PostsLab(Context context) {
        Context appContext = context.getApplicationContext();
        PostsDB database = Room.databaseBuilder(appContext, PostsDB.class,
                "postDatabase").allowMainThreadQueries().build();

        myDAO = database.getPostNUsersDAO();

    }
    public static PostsLab get(Context context){
        if(myPostsLab == null){
            myPostsLab = new PostsLab(context);
        }
        return myPostsLab;
    }


    //Post things
    @Override
    public void addPost(Post post) {
        myDAO.addPost(post);
    }

    @Override
    public void updatePost(Post post) {
        myDAO.updatePost(post);
    }

    @Override
    public void deletePost(Post post) {
        myDAO.deletePost(post);
    }

    @Override
    public Post getPost(int id) {
        return myDAO.getPost(id);
    }

    @Override
    public List<Post> getPosts() {
        return myDAO.getPosts();
    }

    @Override
    public void deleteAllPosts() {
        myDAO.deleteAllPosts();
    }



    //Users things
    @Override
    public void addUser(User user) {
        myDAO.addUser(user);
    }

    @Override
    public void updateUser(User user) {
        myDAO.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        myDAO.deleteUser(user);
    }

    @Override
    public User getUser(int id) {
        return myDAO.getUser(id);
    }

    @Override
    public List<User> getUsers() {
        return myDAO.getUsers();
    }

    @Override
    public void deleteAllUers() {
        myDAO.deleteAllUers();
    }

    @Override
    public int getUserId(String nombre) {
        return myDAO.getUserId(nombre);
    }

    @Override
    public String getUserName(int id) {
        return myDAO.getUserName(id);
    }

    @Override
    public List<UsersAndPosts> getUsersWithPosts() {
        return myDAO.getUsersWithPosts();
    }
}
