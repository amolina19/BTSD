package com.btds.app.Activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdx.designertoast.DesignerToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Alejandro Molina Louchnikov
 */

public class EstadoActivity extends BasicActivity implements StoriesProgressView.StoriesListener, View.OnTouchListener {

    private StoriesProgressView storiesProgressView;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView storiesImageView;
    TextView usuarioTextView;
    TextView fechaSubida;
    private Context context;
    private int storiesCount = 0;
    //private Usuario usuarioEstado;
    CircleImageView circleImageView;
    List<Estados> listaEstadosUsuario = new ArrayList<>();
    String usuarioIDFirebase;
    Button addHistoria;
    Button borrarHistoria;
    Button addHistoriaV2;
    Usuario usuarioActual;

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

        addHistoriaV2 = findViewById(R.id.addv2Historia);
        addHistoriaV2.setVisibility(View.GONE);
        addHistoriaV2.setClickable(false);
        addHistoria = findViewById(R.id.addHistoria);
        addHistoria.setVisibility(View.GONE);
        addHistoria.setClickable(false);
        borrarHistoria = findViewById(R.id.eliminarHistoria);
        borrarHistoria.setVisibility(View.GONE);
        borrarHistoria.setClickable(false);
        usuarioTextView = findViewById(R.id.stories_nombre_usuario);
        fechaSubida = findViewById(R.id.stories_fecha_subida_usuario);
        circleImageView = findViewById(R.id.stories_imagen_perfil_usuario);
        storiesImageView = findViewById(R.id.stories_imageView);
        storiesProgressView = findViewById(R.id.stories);

        addHistoria.setOnClickListener(v -> {
            abrirCamara();
            if(listaEstadosUsuario.size() > 0){
                storiesProgressView.pause();
            }

        });

        addHistoriaV2.setOnClickListener(v -> {
            abrirCamara();
            if(listaEstadosUsuario.size() > 0){
                storiesProgressView.pause();
            }
        });

        borrarHistoria.setOnClickListener(v -> {

            storiesProgressView.pause();
            AlertDialog.Builder builder = new AlertDialog.Builder(EstadoActivity.this);
            builder.setTitle(getResources().getString(R.string.borrarHistoria));
            builder.setMessage(getResources().getString(R.string.borrarHistoriaPregunta));
            builder.setNegativeButton(getResources().getString(R.string.Cancelar),null);
            builder.setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, which) -> {
                Funciones.borrarHistoria(listaEstadosUsuario.get(storiesCount));
                storiesCount = 0;
                storiesProgressView.startStories(storiesCount);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });


        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obtenerUsuarioActual();
                listaEstadosUsuario.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Estados estado = data.getValue(Estados.class);

                    assert estado != null;
                    if(estado.getUsuario().getId().contentEquals(usuarioIDFirebase)){
                        listaEstadosUsuario.add(estado);
                    }

                }

                Collections.sort(listaEstadosUsuario);

                if(usuarioIDFirebase.contentEquals(firebaseUser.getUid())) {
                    if (listaEstadosUsuario.size() == 0) {
                        addHistoria.setVisibility(View.VISIBLE);
                        addHistoria.setClickable(true);
                        usuarioTextView.setVisibility(View.GONE);
                        fechaSubida.setVisibility(View.GONE);
                        circleImageView.setVisibility(View.GONE);
                        storiesImageView.setVisibility(View.GONE);
                        storiesProgressView.setVisibility(View.GONE);
                    } else {
                        addHistoriaV2.setVisibility(View.VISIBLE);
                        addHistoriaV2.setClickable(true);
                        borrarHistoria.setVisibility(View.VISIBLE);
                        borrarHistoria.setClickable(true);
                    }
                }


                storiesProgressView.setStoriesCount(listaEstadosUsuario.size()); // <- set stories
                storiesProgressView.setStoryDuration(5000L); // <- set a story duration
                storiesProgressView.setStoriesListener(EstadoActivity.this); // <- set listener
                assert listaEstadosUsuario != null;
                storiesImageView.setOnTouchListener(EstadoActivity.this::onTouch);

                if(listaEstadosUsuario.size() > 0){
                    try{
                    usuarioTextView.setText(listaEstadosUsuario.get(0).getUsuario().getUsuario());
                    }catch (IndexOutOfBoundsException ioobe){
                        onBackPressed();
                    }

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
                    if(!EstadoActivity.this.isFinishing()){
                        if(userPictureURL.contentEquals("default")){
                            Glide.with(EstadoActivity.this).load(R.drawable.default_user_picture).into(circleImageView);
                        }else{
                            Glide.with(EstadoActivity.this).load(userPictureURL).into(circleImageView);
                        }
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
                }




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
        Intent backToMainActivity = new Intent(EstadoActivity.this,MainActivity.class);
        startActivity(backToMainActivity);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Log.d("DEBUG TusEstadosActivity","IMAGEN PATH "+image.getPath());
            //Toast.makeText(this, getResources().getString(R.string.actualizandoImagenPerfil), Toast.LENGTH_SHORT).show();
            subirImagen(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void  obtenerUsuarioActual(){
        DatabaseReference usuario = Funciones.getUsersDatabaseReference().child(firebaseUser.getUid());
        usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioActual = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void subirImagen(Image image){

        Uri file = Uri.fromFile(new File(image.getPath()));

        //FirebaseStorage storage;
        StorageReference storageReference;

        //storage = FirebaseStorage.getInstance();
        storageReference = Funciones.getFirebaseStorageReference();

        //StorageReference storageUserEstadosRef = storageReference.child("Imagenes/Perfil/"+usuarioObject.getId());
        String idEstado = new Fecha().obtenerFechaTotal()+""+Funciones.getAlphaNumericString(8);
        String URLstring = Funciones.getAlphaNumericString(16);
        Log.d("DEBUG TusEstadosActivity","URL "+ URLstring);
        StorageReference storageUserEstadosRef = storageReference.child("Estados/"+firebaseUser.getUid()+"/"+URLstring);
        UploadTask uploadTask = storageUserEstadosRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("DEBUG PerfilActivity","El Estado no se ha subido");
            //Toast.makeText(TusEstadosActivity.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "ERROR al subir el estado", Toast.LENGTH_SHORT).show();
            DesignerToast.Warning(EstadoActivity.this,getResources().getString(R.string.errorSubirEstado), Gravity.CENTER, Toast.LENGTH_SHORT);
        }).addOnSuccessListener(taskSnapshot -> storageUserEstadosRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            // getting image uri and converting into string
            //usuarioObject.setImagenURL(downloadUrl.toString());
            //Fecha fecha = new Fecha();

            Estados estado = new Estados(idEstado,downloadUrl.toString(),usuarioActual,new Fecha());
            Log.d("DEBUG TusEstadosActivity ","FECHA SUBIDA "+estado.fecha.toString());

            assert firebaseUser != null;
            Funciones.getEstadosDatabaseReference().child(idEstado).setValue(estado).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Log.d("DEBUG TusEstadosActivity","Referencia Estado Registrado en la base de datos");
                    if(addHistoria.getVisibility() == View.VISIBLE){
                        finish();
                        startActivity(getIntent());
                    }
                }
            });

        }));
    }

    @AfterPermissionGranted(1)
    public void abrirCamara(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(EasyPermissions.hasPermissions(this,perms)){
            ImagePicker.create(EstadoActivity.this).returnMode(ReturnMode.GALLERY_ONLY).single().start();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoAbrirCamara),1,perms);
        }
        //
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioActual != null){
            Funciones.actualizarConexion(getResources().getString(R.string.online),usuarioActual);
        }
    }
}
