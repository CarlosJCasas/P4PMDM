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
import java.util.function.UnaryOperator;

public class ModificarActivity  extends AppCompatActivity {
    private EditText tituloEd;
    private Spinner autoresSpinn;
    private EditText cuerpoEd;
    private Button addButtton, cancelButton;
    private ArrayList<User> listaUsers = new ArrayList<>();
    private ArrayList<String> listaAutores = new ArrayList<>();
    private ArrayList<Post> listaPost = new ArrayList<>();
    private PostsLab myLab;
    private int idAutor;

    private String tituloEnviar;
    private String bodyEnviar;
    private int userIdEnviar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        myLab = PostsLab.get(this);

        listaPost = (ArrayList<Post>) myLab.getPosts();
        listaUsers = (ArrayList<User>) myLab.getUsers();
        tituloEd = findViewById(R.id.titulo_editText);
        cuerpoEd = findViewById(R.id.cuerpo_editText);
        autoresSpinn = findViewById(R.id.spinner_users);

        Intent intent = getIntent();
        int idPost = intent.getIntExtra("id",1);
        String titulo = intent.getStringExtra("titulo");
        String cuerpo = intent.getStringExtra("body");
        idAutor = intent.getIntExtra("autor",1);
        tituloEd.setHint(MainActivity.capitalizarPrimeraletra(titulo));
        cuerpoEd.setHint(MainActivity.capitalizarPrimeraletra(cuerpo));

        for (User u : listaUsers) {
            listaAutores.add(u.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaAutores);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoresSpinn.setAdapter(arrayAdapter);
        autoresSpinn.setSelection(idAutor-1);
        autoresSpinn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idAutor = myLab.getUserId(listaAutores.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addButtton = findViewById(R.id.button_aceptar_add);
        addButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recogemos todos los datos de los ED y Spinner
                if(tituloEd.getText().toString().isEmpty()){
                    //Enviar titulo
                    tituloEnviar = titulo;
                }else{
                    tituloEnviar = tituloEd.getText().toString();
                }
                if(cuerpoEd.getText().toString().isEmpty()){
                    bodyEnviar = cuerpo;
                }else {
                    bodyEnviar = cuerpoEd.getText().toString();
                }
                userIdEnviar = idAutor;
                if(idPost>=100){
                    Post post = new Post(idPost, tituloEnviar, bodyEnviar, userIdEnviar);
                    myLab.updatePost(post);
                    Intent intent1 = new Intent();
                    intent1.putExtra("idUpdated", idPost);
                    intent1.putExtra("titleUpdate", tituloEnviar);
                    intent1.putExtra("bodyUpdate", bodyEnviar);
                    intent1.putExtra("userIdUpdated", userIdEnviar);
                    setResult(4,intent1);

                }else{
                    //Mandar la modificacion a la API PUT
                    RequestQueue requestQueue = Volley.newRequestQueue(ModificarActivity.this);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id",idPost);
                        jsonObject.put("title", tituloEnviar);
                        jsonObject.put("body", bodyEnviar);
                        jsonObject.put("userId", userIdEnviar);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                                SplashActivity.POSTURL.concat(String.valueOf(idPost)), jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Recibo los datos de la API y modifico la BD
                                try {
                                    int idUpdate = response.getInt("id");
                                    String tituloUpdate = response.getString("title");
                                    String bodyUpdate = response.getString("body");
                                    int userUpdate = response.getInt("userId");
                                    Post post = new Post(idUpdate, tituloUpdate, bodyUpdate, userUpdate);
                                    //No actualiza esta movida
                                    myLab.updatePost(post);
                                    Intent intent1 = new Intent();
                                    intent1.putExtra("idUpdated", idUpdate);
                                    intent1.putExtra("titleUpdate", tituloUpdate);
                                    intent1.putExtra("bodyUpdate", bodyUpdate);
                                    intent1.putExtra("userIdUpdated", userUpdate);
                                    setResult(4,intent1);
                                    listaPost = (ArrayList<Post>) myLab.getPosts();
                                    MainActivity.listaPosts.clear();
                                    MainActivity.listaPosts.addAll(listaPost);
                                    MainActivity.adaptador.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ModificarActivity.this, getString(R.string.error_modificar) + SplashActivity.POSTURL, Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }

        });
        cancelButton = findViewById(R.id.button_cancelar_add);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModificarActivity.super.onBackPressed();
            }
        });
    }
}
