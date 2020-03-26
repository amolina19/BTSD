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

public class MessageActivity extends AppCompatActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    TextView estado;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton enviar_button;
    EditText enviar_texto;

    Usuario usuarioChat;
    Integer[] diasPasados;
    Intent intent;

    MensajesAdapter mensajesAdapter;
    List<Mensaje> listaMensajes;
    RecyclerView recyclerView;

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
        diasPasados = new Integer[2];


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

                usuarioChat = dataSnapshot.getValue(Usuario.class);
                usuario.setText(usuarioChat.getUsuario());

                if(usuarioChat.getEstado().contentEquals("Desconectado")) {

                    diasPasados = obtenerDiasPasados();

                    if (diasPasados[0] == 0) {
                        estado.setText("Ult vez hoy a las " + usuarioChat.getHora().toUpperCase());
                    } else {
                        if (diasPasados[0] == 1 && diasPasados[1] == 0) {
                            estado.setText("Ayer a las " + usuarioChat.getHora().toUpperCase());
                        } else if (diasPasados[1] == 1) {
                            String fecha = usuarioChat.getFecha().replace(" ", "/");
                            estado.setText("Visto ult vez el " + fecha + " a las " + usuarioChat.getHora().toUpperCase());
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

    private void enviarMensaje(String emisor, String receptor, String mensaje){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String[] fechaActual = obtenerHoraYFechaActual();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);
        hashMap.put("hora",fechaActual[0]);
        hashMap.put("fecha",fechaActual[1]);
        hashMap.put("leido","false");
        System.out.println(fechaActual[0]);

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
                System.out.println("LISTA MENSAJES"+ listaMensajes.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Integer[] obtenerDiasPasados(){
        String fecha = usuarioChat.getFecha();

        String saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        int userChatDateDay = Integer.valueOf(fecha.replace(" ","").substring(1,2));
        int actualDateDay = Integer.valueOf(saveCurrentDate.replace(" ","").substring(1,2));


        int userChatDateMonth = Integer.valueOf(fecha.replace(" ","").substring(3,4));
        int actualDateMonth = Integer.valueOf(saveCurrentDate.replace(" ","").substring(3,4));

        //System.out.println("DAY "+userChatDateDay+" MES "+userChatDateMonth);

        Integer[] valores = new Integer[2];
        valores[0] = 0; //Saber cuantos dias han pasado
        valores[1] = 0; // 1 Si el mes del usuario es anterior , 0 si se conecto en el mismo mes

        if(userChatDateDay < actualDateDay ){

            valores[0] = actualDateDay - userChatDateDay;

            if(userChatDateMonth < actualDateMonth){
                valores[1] = 1;
            }else{
                valores[1] = 0;
            }
        }
        System.out.println(valores[0]+" "+valores[1]);

        return valores;
    }

    private String[] obtenerHoraYFechaActual(){

        String[] fecha = new String[2];
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        //Posicion Hora
        fecha[0] = saveCurrentTime;
        //Posicion Fecha
        fecha[1] = saveCurrentDate;
        
        return fecha;
    }
}
