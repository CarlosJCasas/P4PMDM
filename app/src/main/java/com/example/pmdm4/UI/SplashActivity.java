package com.example.pmdm4.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pmdm4.DDBB.PostsLab;
import com.example.pmdm4.R;
import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    public static final String USERURL = "https://jsonplaceholder.typicode.com/users/";
    public static final String POSTURL = "https://jsonplaceholder.typicode.com/posts/";
    public PostsLab myLab;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        myLab = PostsLab.get(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myLab.getPosts().isEmpty() && myLab.getUsers().isEmpty()) {
                    recibirDatosUsers();
                    recibirDatosPosts();
                }
            }
        }, 1000);

        //Poner un contador de 50 para cuando se libera el splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
    }

    //Recibes los datos de de los USERS de la URL
    public void recibirDatosUsers() {
        //Crear las request para meter los datos en las listas

        for (int numUser = 1; numUser < 6; numUser++) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    USERURL.concat(String.valueOf(numUser)),
                    null,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Aqui recibir todos los datos del response
                    try {
                        int id = response.getInt("id");
                        String name = response.getString("name");
                        String username = response.getString("username");
                        String email = response.getString("email");
                        String phone = response.getString("phone");
                        JSONObject company = response.getJSONObject("company");
                        String companyName = company.getString("name");
                        User user = new User(id, name, username, email, companyName, phone);
                        //Añadir a la base de datos
                        myLab.addUser(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, "ERROR AL RECIBIR DATOS DE LA URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(SplashActivity.this, "No se han podido obtener los datos de la Url " + USERURL, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);

        }
    }

    //Recibes los datos de de los POSTS de la URL
    public void recibirDatosPosts() {
        //Crear las request para meter los datos en las listas
        for (int numPost = 1; numPost < 51; numPost++) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, POSTURL.concat(String.valueOf(numPost)), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Aqui recibir todos los datos del response
                    try {
                        String title = response.getString("title");
                        String body = response.getString("body");
                        int userId = response.getInt("userId");
                        Post post = new Post(title, body, userId);
                        //Add a la base de datos
                        myLab.addPost(post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashActivity.this, "ERROR AL RECIBIR DATOS DE LA URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(SplashActivity.this, "No se han podido obtener los datos de la Url " + POSTURL, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);

        }
    }
}
