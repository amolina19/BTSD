package com.btds.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.MensajesAdapter;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    TextView estado;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceUserDataBase = getInstance().getReference("Usuarios");
    DatabaseReference reference;

    ImageButton enviar_button;
    EditText enviar_texto;

    Usuario usuarioChat;
    int diasPasados;
    Intent intent;

    MensajesAdapter mensajesAdapter;
    List<Mensaje> listaMensajes;
    RecyclerView recyclerView;

    Fecha fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Funciones.setActividadEnUso(true);
        Funciones.setBackPressed(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view_mensaje);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        enviar_button = findViewById(R.id.enviar_mensaje_button);
        enviar_texto = findViewById(R.id.enviar_mensaje);

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);
        estado = findViewById(R.id.estado);

        intent = getIntent();
        final String usuarioID = intent.getStringExtra("userID");

        //firebaseUser =
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioID);

        enviar_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String mensaje = enviar_texto.getText().toString();

                if(!mensaje.equals("")){
                    enviarMensaje(firebaseUser.getUid(),usuarioID,mensaje);
                }else{
                    Toast.makeText(MessageActivity.this, "No se puede enviar un mensaje vacio", Toast.LENGTH_SHORT).show();
                }
                enviar_texto.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioChat = dataSnapshot.getValue(Usuario.class);
                usuario.setText(usuarioChat.getUsuario());

                if(usuarioChat.getEstado().contentEquals("Desconectado")) {

                    diasPasados = Integer.valueOf((int) Funciones.obtenerDiasPasados(usuarioChat));

                    if (diasPasados == 0) {
                        estado.setText(getResources().getString(R.string.hoy) +" "+ usuarioChat.getHora());
                    } else {
                        if (diasPasados == 1 ) {
                            estado.setText(getResources().getString(R.string.ayer) +" "+usuarioChat.getHora());
                        } else if (diasPasados > 1) {
                            String fecha = usuarioChat.getFecha().replace(" ", "/");
                            estado.setText(getResources().getString(R.string.ultavez1parte)+" "+fecha+" "+ getResources().getString(R.string.ultavez2parte)+" "+ usuarioChat.getHora());
                        }
                    }
                }else {
                    estado.setText(usuarioChat.getEstado());
                }

                if(usuarioChat.getImagenURL().equals("default")){
                    imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MessageActivity.this).load(usuarioChat.getImagenURL()).into(imagen_perfil);
                }

                leerMensaje(firebaseUser.getUid(),usuarioChat.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarMensaje(String emisor, String receptor, String mensaje){
        fecha = new Fecha();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);
        hashMap.put("hora",fecha.obtenerHora()+":"+fecha.obtenerMinutos());
        hashMap.put("fecha",fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAño());
        hashMap.put("leido","false");

        reference.child("Chats").push().setValue(hashMap);
    }

    private void leerMensaje(final String id, final String userID){

        listaMensajes = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();


                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    if(mensaje.getReceptor().equals(id) && mensaje.getEmisor().equals(userID)
                        || mensaje.getReceptor().equals(userID) && mensaje.getEmisor().equals(id)){
                        listaMensajes.add(mensaje);
                    }

                    mensajesAdapter = new MensajesAdapter(MessageActivity.this,listaMensajes);
                    recyclerView.setAdapter(mensajesAdapter);
                }
                //System.out.println("LISTA MENSAJES "+ listaMensajes.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Funciones.setActividadEnUso(true);
        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getApplicationContext());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Funciones.setActividadEnUso(false);
        if(!Funciones.getBackPressed() && !Funciones.getActividadEnUso()){
            Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser, referenceUserDataBase, getApplicationContext());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Funciones.setActividadEnUso(true);
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getApplicationContext());

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        Funciones.setActividadEnUso(false);
        if(!Funciones.getBackPressed() && !Funciones.getActividadEnUso()){
            Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser, referenceUserDataBase, getApplicationContext());
        }
    }

    @Override
    public void onBackPressed() {
        Funciones.setBackPressed(true);
        super.onBackPressed();
    }
}
