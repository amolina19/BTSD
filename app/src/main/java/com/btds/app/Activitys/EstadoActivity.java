package com.btds.app.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.btds.app.Modelos.Estados;
import com.btds.app.R;
import com.btds.app.Utils.Constantes;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

/**
 * @author Alejandro Molina Louchnikov
 */

public class EstadoActivity extends BasicActivity implements StoriesProgressView.StoriesListener, View.OnTouchListener {

    private StoriesProgressView storiesProgressView;
    ImageView storiesImageView;
    TextView usuarioTextView;
    TextView fechaSubida;
    private Context context;
    private int storiesCount = 0;
    //private Usuario usuarioEstado;
    CircleImageView circleImageView;
    List<Estados> listaEstadosUsuario = new ArrayList<>();
    String usuarioIDFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado);
        Log.d("DEBUG ","EstadoActivity Created");

        context = getApplicationContext();
        Intent intent = getIntent();
        usuarioIDFirebase = intent.getStringExtra("estadoUsuarioIDFirebase");



        //listImagesUrl = new ArrayList<>();
        //listImagesUrl.add("https://cde.laprensa.e3.pe/ima/0/0/2/3/7/237000.jpg");
        //listImagesUrl.add("https://www.muylinux.com/wp-content/uploads/2019/01/windows7.jpg");
        //listImagesUrl.add("https://www.milcomos.com/arch/2014/01/ejecutar-aplicaciones-en-Windows-sin-instalarlas.jpg");
        //listImagesUrl.add("https://d24jx5gocr6em0.cloudfront.net/wp-content/uploads/2020/04/03160041/negros-con-ataud-520x350.jpg");
        //listImagesUrl.add("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Spain.svg/1200px-Flag_of_Spain.svg.png");

        usuarioTextView = findViewById(R.id.stories_nombre_usuario);
        fechaSubida = findViewById(R.id.stories_fecha_subida_usuario);
        circleImageView = findViewById(R.id.stories_imagen_perfil_usuario);
        storiesImageView = findViewById(R.id.stories_imageview);
        storiesProgressView = findViewById(R.id.stories);

        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEstadosUsuario.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Estados estado = data.getValue(Estados.class);

                    assert estado != null;
                    if(estado.getUsuario().getId().contentEquals(usuarioIDFirebase)){
                        listaEstadosUsuario.add(estado);
                    }

                }

                Collections.sort(listaEstadosUsuario);

                storiesProgressView.setStoriesCount(listaEstadosUsuario.size()); // <- set stories
                storiesProgressView.setStoryDuration(5000L); // <- set a story duration
                storiesProgressView.setStoriesListener(EstadoActivity.this); // <- set listener
                assert listaEstadosUsuario != null;
                storiesImageView.setOnTouchListener(EstadoActivity.this::onTouch);

                usuarioTextView.setText(listaEstadosUsuario.get(0).getUsuario().getUsuario());
                //fechaSubida.setText(listaEstadosUsuario.get(0).getFecha().hora+""+listaEstadosUsuario.get(0).getFecha().minutos);
                Estados estado = listaEstadosUsuario.get(0);
                Log.d("FECHA EstadoActivity ESTADO",estado.getFecha().toString());

                int minutosTranscurridos = Funciones.obtenerMinutosSubida(estado);
                String fechaSubidaEstado;
                if(minutosTranscurridos<60){
                    fechaSubidaEstado = minutosTranscurridos+" "+getResources().getString(R.string.m);
                }else{
                    fechaSubidaEstado = minutosTranscurridos / 60 +" "+getResources().getString(R.string.h);
                }
                fechaSubida.setText(fechaSubidaEstado);


                //Picasso.with(context).load(listaEstadosUsuario.get(storiesCount).getEstadoURL()).into(storiesImageView);
                String userPictureURL = listaEstadosUsuario.get(0).getUsuario().getImagenURL();
                if(userPictureURL.contentEquals("default")){
                    Glide.with(EstadoActivity.this).load(Constantes.default_image_profile).into(circleImageView);
                }

                if(!EstadoActivity.this.isFinishing()){
                    Glide.with(EstadoActivity.this).load(userPictureURL).into(circleImageView);
                }
                //Glide.with(EstadoActivity.this).load(userPictureURL).into(circleImageView);

                Log.d("DEBUG EstadosActivity","Valor contador "+storiesCount);
                Picasso.with(context).load(listaEstadosUsuario.get(0).getEstadoURL()).into(storiesImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        storiesProgressView.startStories(storiesCount);
                    }

                    @Override
                    public void onError() {
                        //storiesProgressView.resume();
                    }
                });

                 // <- start progress
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void onNext() {
        //Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
        //
        storiesCount++;
        Log.d("DEBUG EstadosActivity","Valor contador "+storiesCount);
        Picasso.with(context).load(listaEstadosUsuario.get(storiesCount).getEstadoURL()).into(storiesImageView);

        Estados estado = listaEstadosUsuario.get(storiesCount);
        int minutosTranscurridos = Funciones.obtenerMinutosSubida(estado);
        String fechaSubidaEstado;
        if(minutosTranscurridos<60){
            fechaSubidaEstado = minutosTranscurridos+" "+getResources().getString(R.string.m);
        }else{
            fechaSubidaEstado = minutosTranscurridos / 60 +" "+getResources().getString(R.string.h);
        }
        fechaSubida.setText(fechaSubidaEstado);



        //storiesProgressView.setBackground(getResources().getDrawable(R.drawable.buscar_amigos_style));
        //Glide.with(getApplicationContext()).load("https://cde.laprensa.e3.pe/ima/0/0/2/3/7/237000.jpg").into(storiesImageView);
    }


    public void onPrev() {
        // Call when finished revserse animation.
        //Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
        storiesCount--;
        if(storiesCount<0){
            storiesCount=0;
        }
        Picasso.with(context).load(listaEstadosUsuario.get(storiesCount).getEstadoURL()).into(storiesImageView);

        // Picasso.with(context).load(listaEstadosUsuario.get(storiesCount).getEstadoURL()).into(storiesImageView);

        Estados estado = listaEstadosUsuario.get(storiesCount);

        int minutosTranscurridos = Funciones.obtenerMinutosSubida(estado);
        String fechaSubidaEstado;
        if(minutosTranscurridos<60){
            fechaSubidaEstado = minutosTranscurridos+" "+getResources().getString(R.string.m);
        }else{
            fechaSubidaEstado = minutosTranscurridos / 60 +" "+getResources().getString(R.string.h);
        }
        fechaSubida.setText(fechaSubidaEstado);
    }


    public void onComplete() {
        //Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Intent backToMainActivity = new Intent(EstadoActivity.this,MainActivity.class);
        //startActivity(backToMainActivity);
        finish();
    }

    @Override
    public boolean onTouch(View v, @org.jetbrains.annotations.NotNull MotionEvent event) {
        int X = (int) event.getX();
        //int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(this, "ACTION_DOWN AT COORDS "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                //boolean isTouch = true;
                storiesProgressView.pause();
                break;

            case MotionEvent.ACTION_MOVE:
                //Toast.makeText(this, "MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_UP:
                //Toast.makeText(this, "ACTION_UP "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                storiesProgressView.resume();
                if(X<(this.getWindow().getDecorView().getWidth()/3.5)){
                    storiesProgressView.reverse();
                }

                if(X>(this.getWindow().getDecorView().getWidth()-200)){
                    storiesProgressView.skip();
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Funciones.setBackPressed();
        super.onBackPressed();
        Intent backToMainActivity = new Intent(EstadoActivity.this,MainActivity.class);
        startActivity(backToMainActivity);
        finish();
    }
}
