package com.example.pmdm4.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddActivity extends AppCompatActivity {
    private EditText tituloEd;
    private Spinner autoresSpinn;
    private EditText cuerpoEd;
    private Button addButtton, cancelButton;
    private ArrayList<User> listaUsers = new ArrayList<>();
    private ArrayList<String> listaAutores = new ArrayList<>();
    private ArrayList<Post> listaPost = new ArrayList<>();
    private PostsLab myLab;
    private int idautor;
    private boolean control = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        myLab = PostsLab.get(this);
        listaUsers = (ArrayList<User>) myLab.getUsers();
        listaPost = (ArrayList<Post>) myLab.getPosts();

        for (User u : listaUsers) {
            listaAutores.add(u.getName());
        }

        tituloEd = findViewById(R.id.titulo_editText);
        cuerpoEd = findViewById(R.id.cuerpo_editText);
        autoresSpinn = findViewById(R.id.spinner_users);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaAutores);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoresSpinn.setAdapter(arrayAdapter);
        autoresSpinn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Guardamos el ID del autor, la position en la lista va de 0 a 4 y los ids de 1 a 5, position+1
                idautor = myLab.getUserId(listaAutores.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Boton para enviar a la main activity
        addButtton = findViewById(R.id.button_aceptar_add);
        addButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enviar lo escrito en el titulo y body
                String titulo = tituloEd.getText().toString();
                String body = cuerpoEd.getText().toString();
                JSONObject object = new JSONObject();
                try {
                    object.put("title", titulo);
                    object.put("body", body);
                    object.put("userId", idautor);
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, SplashActivity.POSTURL, object, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String tituloPost = response.getString("title");
                                String bodyPost = response.getString("body");
                                int userIdPost = response.getInt("userId");
                                Post post = new Post(tituloPost, bodyPost, userIdPost);
                                myLab.addPost(post);
                                MainActivity.listaPosts.add(post);
                                MainActivity.adaptador.notifyDataSetChanged();
                                control = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(AddActivity.this, "No se encuentran los datos de la URL " + SplashActivity.POSTURL, Toast.LENGTH_SHORT).show();
                                control = false;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(AddActivity.this, "No se han podido añadir los datos a la URL " + SplashActivity.POSTURL, Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(objectRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddActivity.this, "No se han podido añadir los datos a la URL " + SplashActivity.POSTURL, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra("control", control);
                setResult(2, intent);
                finish();
            }
        });
        cancelButton = findViewById(R.id.button_cancelar_add);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActivity.super.onBackPressed();
            }
        });
    }
}
