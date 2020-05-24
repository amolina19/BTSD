package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        TextView image_view_error;
        PhotoView photoView;

        image_view_error = findViewById(R.id.image_view_error);
        photoView = findViewById(R.id.photo_view);
        Intent intent = getIntent();
        String fotoURL = intent.getStringExtra("FotoURL");

        if(Funciones.conectividadDisponible(this)){
            Log.d("DEBUG ViewImageActivity","FOTOURL "+fotoURL);
            Picasso.with(this).load(fotoURL).into(photoView);
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
