package com.btds.app.Activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class EstadoActivity extends BasicActivity implements StoriesProgressView.StoriesListener, View.OnTouchListener {

    private StoriesProgressView storiesProgressView;
    ImageView storiesImageView;
    private boolean isTouch = false;
    private Context context;
    private int storiesCount = 0;
    private Intent intent;
    private Usuario usuarioEstado;
    private String usuarioID;
    CircleImageView circleImageView;
    List<String> listImagesUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado);

        context = getApplicationContext();
        intent = getIntent();
        usuarioID = intent.getStringExtra("userID");

        listImagesUrl = new ArrayList<>();
        listImagesUrl.add("https://cde.laprensa.e3.pe/ima/0/0/2/3/7/237000.jpg");
        listImagesUrl.add("https://www.muylinux.com/wp-content/uploads/2019/01/windows7.jpg");
        listImagesUrl.add("https://www.milcomos.com/arch/2014/01/ejecutar-aplicaciones-en-Windows-sin-instalarlas.jpg");
        listImagesUrl.add("https://d24jx5gocr6em0.cloudfront.net/wp-content/uploads/2020/04/03160041/negros-con-ataud-520x350.jpg");

        circleImageView = findViewById(R.id.stories_imagen_perfil_usuario);
        storiesImageView = findViewById(R.id.stories_imageview);
        storiesProgressView = findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(listImagesUrl.size()); // <- set stories
        storiesProgressView.setStoryDuration(3200L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
        storiesImageView.setOnTouchListener(this);


        Glide.with(getApplicationContext()).load("https://scontent-fml2-1.cdninstagram.com/v/t51.2885-19/s150x150/90512088_153085949275903_8205891114746511360_n.jpg?_nc_ht=scontent-fml2-1.cdninstagram.com&_nc_ohc=MOW6b_JNbMAAX-zaI4X&oh=f512453213f4e63a2d45cbc99d87df43&oe=5EBF7726").into(circleImageView);

        Picasso.with(context).load(listImagesUrl.get(0)).into(storiesImageView);
    }



    public void onNext() {
        //Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
        storiesCount++;
        Picasso.with(context).load(listImagesUrl.get(storiesCount)).into(storiesImageView);

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
        Picasso.with(context).load(listImagesUrl.get(storiesCount)).into(storiesImageView);
    }


    public void onComplete() {
        //Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                Toast.makeText(this, "ACTION_DOWN AT COORDS "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                isTouch = true;
                storiesProgressView.pause();
                break;

            case MotionEvent.ACTION_MOVE:
                Toast.makeText(this, "MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_UP:
                Toast.makeText(this, "ACTION_UP "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
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
}
