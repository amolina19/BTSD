package com.btds.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.btds.app.Modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    TextView estado;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton enviar_button;
    EditText enviar_texto;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

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

        enviar_button = findViewById(R.id.enviar_mensaje_button);
        enviar_texto = findViewById(R.id.enviar_mensaje);

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);
        estado = findViewById(R.id.estado);


        intent = getIntent();
        final String usuarioID = intent.getStringExtra("userID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioID);

        enviar_button.setOnClickListener(new View.OnClickListener() {
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
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario usuarioChat = dataSnapshot.getValue(Usuario.class);
                usuario.setText(usuarioChat.getUsuario());

                if(usuarioChat.getEstado().contentEquals("Desconectado")){
                    estado.setText("Ult vez a las "+usuarioChat.getHora().toUpperCase());
                }else{
                    estado.setText(usuarioChat.getEstado());
                }

                if(usuarioChat.getImagenURL().equals("default")){
                    imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MessageActivity.this).load(usuarioChat.getImagenURL()).into(imagen_perfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarMensaje(String emisor, String receptor, String mensaje){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);

        reference.child("Chats").push().setValue(hashMap);
    }
}
