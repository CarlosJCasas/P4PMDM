package com.example.pmdm4.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmdm4.DDBB.PostsLab;
import com.example.pmdm4.R;
import com.example.pmdm4.core.Post;
import com.example.pmdm4.core.User;

import java.util.ArrayList;

public class DatosAutor extends AppCompatActivity {
    PostsLab myLab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_autor);
        TextView nombre, nickname, company, email, telefono;
        Intent intent = getIntent();
        int idUser = intent.getIntExtra("idAutor", 1);
        myLab = PostsLab.get(this);
        nombre = findViewById(R.id.nombreUser);
        nickname = findViewById(R.id.nicknameUser);
        company = findViewById(R.id.companhiaUser);
        email = findViewById(R.id.emailUser);
        telefono = findViewById(R.id.telefonoUser);

        User user = myLab.getUser(idUser);
        nombre.setText(user.getName());
        nickname.setText(user.getUsername());
        company.setText(user.getCompanyName());
        email.setText(user.getEmail());
        telefono.setText(user.getPhone());

    }
}
