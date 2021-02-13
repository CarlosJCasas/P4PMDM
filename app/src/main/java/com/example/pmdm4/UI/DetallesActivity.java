package com.example.pmdm4.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmdm4.DDBB.PostsLab;
import com.example.pmdm4.R;
import com.example.pmdm4.core.Post;

import java.util.ArrayList;

public class DetallesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_activity);

        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String body = intent.getStringExtra("cuerpo");
        String autor = intent.getStringExtra("autor");

        TextView tituloPost = findViewById(R.id.titulo_post_detalles);
        TextView nombreAutor = findViewById(R.id.nombre_autor_tv_detalles);
        TextView cuerpoPost = findViewById(R.id.cuerpo_act_detalles);

        tituloPost.setText(MainActivity.capitalizarPrimeraletra(titulo));
        nombreAutor.setText(autor);
        cuerpoPost.setText(body);

    }
}
