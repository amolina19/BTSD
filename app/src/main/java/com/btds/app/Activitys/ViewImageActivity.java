package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alejandro Molina Louchnikov
 */

public class ViewImageActivity extends AppCompatActivity {

    TextView image_view_error;
    PhotoView photoView;
    CircleImageView imagen_perfil;
    TextView usuarioTextView, fechaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imagen_perfil = findViewById(R.id.imagen_usuario_foto);
        usuarioTextView = findViewById(R.id.usuario);
        fechaTextView = findViewById(R.id.fecha);

        image_view_error = findViewById(R.id.image_view_error);
        photoView = findViewById(R.id.photo_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        assert bundle != null;
        Usuario usuario = bundle.getParcelable("Usuario");
        Mensaje mensaje = bundle.getParcelable("Mensaje");
        Fecha fecha = bundle.getParcelable("Fecha");

        assert usuario != null;
        Glide.with(this).load(usuario.getImagenURL()).into(imagen_perfil);
        usuarioTextView.setText(usuario.getUsuario());
        assert mensaje != null;
        assert fecha != null;
        String fechaStr = getResources().getString(R.string.enviado)+" "+fecha.hora+":"+fecha.minutos+" "+getResources().getString(R.string.deldia)+" "+fecha.dia+"/"+fecha.mes+"/"+fecha.anno;
        fechaTextView.setText(fechaStr);

        if(Funciones.conectividadDisponible(this)){
            Log.d("DEBUG ViewImageActivity","FOTOURL "+mensaje.getMensaje());
            Picasso.with(this).load(mensaje.getMensaje()).into(photoView);
        }else{
            image_view_error.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void onBackPressed() {
        //Funciones.setBackPressed();
        super.onBackPressed();
        //Intent backToChats = new Intent(MessageActivity.this,MainActivity.class);
        //startActivity(backToChats);
        finish();
    }
}
