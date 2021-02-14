package com.example.pmdm4.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomAdapter.ItemClickListener {
    public static ArrayList<Post> listaPosts = new ArrayList<>();
    private ArrayList<User> listaUsers = new ArrayList<>();
    private ArrayList<Post> listaPostOriginal = new ArrayList<>();
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
        adaptador.setClickListener(this);
        registerForContextMenu(recyclerView);
        recyclerView.setAdapter(adaptador);

        FloatingActionButton addButton = findViewById(R.id.bottonAdd);
        addButton.setOnClickListener(this::launchAddActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adaptador.notifyDataSetChanged();
    }

    //Borra todos los datos y los recupera
    public void resetearDatos() {
        // TODO Añadir un cuadro de texto de confirmacion y en el onClick eliminar todos los datos llamar a la Splash y que vuelva a empezar, reiniciar la app.
        //Elimina todos los datos de la base de datos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmar_reset);
        builder.setPositiveButton(R.string.aceptar, (dialog, which) -> {
            //Deberia cambiar la lista por la antigua y la base de datos
            myLab.deleteAllPosts();
            for(Post post : listaPostOriginal){
                myLab.addPost(post);
            }
            listaPosts = listaPostOriginal;
            adaptador.notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.create().show();
    }

    //Poner la primera letra en mayuscula
    public static String capitalizarPrimeraletra(String string) {
        string = string.substring(0, 1).toUpperCase() + string.substring(1);
        return string;
    }


    //Llama a la actividad añadir
    public void launchAddActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(intent, 2);
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
            case 4:
                data.getIntExtra("idUpdated", 1);
                listaPosts.clear();
                listaPosts = (ArrayList<Post>) myLab.getPosts();
                adaptador.notifyDataSetChanged();
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

                break;
            case (R.id.anhadir):
                launchAddActivity(item.getActionView());
                break;
        }
        adaptador.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }


    //Menu contextual
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (111):
                //Abrir la activity de autor
                Post postAutor = listaPosts.get(item.getGroupId());
                int idAutor = postAutor.getUserId();
                Intent intentAutor = new Intent(MainActivity.this, DatosAutor.class);
                intentAutor.putExtra("idAutor", idAutor);
                startActivity(intentAutor);
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
                startActivityForResult(intent, 4);
                adaptador.notifyDataSetChanged();
                return true;

            case (113):
                //Eliminar el item concreto
                int idPosts = item.getGroupId();
                Post postEliminar = listaPosts.get(idPosts);
                int idBd = postEliminar.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Eliminar?");
                builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                                SplashActivity.POSTURL.concat(String.valueOf(idBd)),
                                null,
                                new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(MainActivity.this, "Post "+ SplashActivity.POSTURL.concat(String.valueOf(idBd))+ " ha sido eliminado", Toast.LENGTH_SHORT).show();
                                //Eliminar POST
                                myLab.deletePost(postEliminar);
                                listaPosts.remove(idPosts);
                                adaptador.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(MainActivity.this, "Eliminar no funciona", Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    }
                });
                builder.setNegativeButton(R.string.cancelar, null);
                builder.create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //OnItemClick muestra los detalles de la posición donde hace click
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