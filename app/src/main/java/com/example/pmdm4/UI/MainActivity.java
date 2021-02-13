package com.example.pmdm4.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pmdm4.DDBB.PostsLab;
import com.example.pmdm4.R;
import com.example.pmdm4.core.CustomAdapter;
import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;
import com.example.pmdm4.core.UsersAndPosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener {
    public static ArrayList<Post> listaPosts = new ArrayList<>();
    private ArrayList<User> listaUsers = new ArrayList<>();
    private final ArrayList<UsersAndPosts> listaUserPosts = new ArrayList<>();
    private RequestQueue requestQueue;
    private PostsLab myLab;
    public static CustomAdapter adaptador;
    private RecyclerView recyclerView;
    boolean control = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myLab = PostsLab.get(this);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        /*
        ¿Siempre entra aqui porque cuando se inicia siempre estan vacias, cual debe ser la condicion?
        Debe recoger siempre los datos de la base de datos pero no de las URL
         */

        //Rellenamos las listas con los datos obtenidos de la base de datos

        listaPosts = (ArrayList<Post>) myLab.getPosts();
        listaUsers = (ArrayList<User>) myLab.getUsers();

        adaptador = new CustomAdapter(MainActivity.this, listaPosts);
        adaptador.setClickListener(this::onItemCLick);
        registerForContextMenu(recyclerView);
        recyclerView.setAdapter(adaptador);

        FloatingActionButton addButton = findViewById(R.id.bottonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddActivity(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (control) {
            adaptador.notifyDataSetChanged();
        }
    }

    //Recibe los datos de los USERS
    public void recibirDatosUsers() {
        //Crear las request para meter los datos en las listas
        for (int numUser = 1; numUser < 6; numUser++) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SplashActivity.USERURL.concat(String.valueOf(numUser)), null, new Response.Listener<JSONObject>() {
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
                        Toast.makeText(MainActivity.this, "ERROR AL RECIBIR DATOS DE LA URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, "No se han podido obtener los datos de la Url " + SplashActivity.USERURL, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    //Recibe los datos de los POSTS
    public void recibirDatosPosts() {
        //Crear las request para meter los datos en las listas

        for (int numPost = 1; numPost < 51; numPost++) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SplashActivity.POSTURL.concat(String.valueOf(numPost)), null, new Response.Listener<JSONObject>() {
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
                        Toast.makeText(MainActivity.this, "ERROR AL RECIBIR DATOS DE LA URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, "No se han podido obtener los datos de la Url " + SplashActivity.POSTURL, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    //Borra todos los datos y los recupera
    public void resetearDatos() {
        // TODO Añadir un cuadro de texto de confirmacion y en el onClick eliminar todos los datos llamar a la Splash y que vuelva a empezar, reiniciar la app.
        //Elimina todos los datos de la base de datos
        myLab.deleteAllPosts();
        myLab.deleteAllUers();

        //Volver a añadir los datos a las listas
        listaPosts = (ArrayList<Post>) myLab.getPosts();
        listaUsers = (ArrayList<User>) myLab.getUsers();

    }

    //Poner la primera letra en mayuscula
    public static String capitalizarPrimeraletra(String string) {
        string = string.substring(0, 1).toUpperCase() + string.substring(1);
        return string;
    }


    //Llama a la actividad añadir
    public void launchAddActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    //Recibir los datos de las activities segun el codigo resultado
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 2:
                control = data.getBooleanExtra("control", false);
                if (control) {
                    adaptador.notifyDataSetChanged();
                }
                break;
            case 3:
                //Recibir los datos modificar

                break;
        }
    }

    //Toolbars
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.search):
                break;
            case (R.id.resetear):
                resetearDatos();
                adaptador.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Menu contextual
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (111):
                //Abrir la activity de autor
                return true;
            case (112):
                //Abrir la activity de modificar

                int position = item.getGroupId();
                Post post = listaPosts.get(position);
                Intent intent = new Intent(MainActivity.this, ModificarActivity.class);
                intent.putExtra("id", post.getId());
                intent.putExtra("titulo", post.getTitle());
                intent.putExtra("body", post.getBody());
                intent.putExtra("autor", post.getUserId());
                startActivity(intent);

                return true;

            case (113):
                //Eliminar el item concreto
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemCLick(View view, int position) {
        //TODO tiene que abrir la descripcion
        Post post = listaPosts.get(position);
        String titulo = post.getTitle();
        String cuerpo = post.getBody();
        User user = listaUsers.get(post.getUserId() - 1);
        String nombreAutor = user.getName();
        Intent intent = new Intent(MainActivity.this, DetallesActivity.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("cuerpo", cuerpo);
        intent.putExtra("autor", nombreAutor);
        startActivityForResult(intent, 3);

    }


}