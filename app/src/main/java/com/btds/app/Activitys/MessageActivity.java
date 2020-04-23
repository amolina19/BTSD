package com.btds.app.Activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.MensajesAdapter;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdx.designertoast.DesignerToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MessageActivity extends BasicActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    TextView estado;


    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference reference;


    ImageButton enviar_button;
    EditText enviar_texto;
    private String cadenaTeclado;

    Usuario usuarioChat;
    String usuarioID;
    int diasPasados;
    Intent intent;

    Menu menuUserFunction;

    MensajesAdapter mensajesAdapter;
    List<Mensaje> listaMensajes;
    HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados;
    RecyclerView recyclerView;
    private String usuario_profile_default= "https://res.cloudinary.com/teepublic/image/private/s--6vDtUIZ---/t_Resized%20Artwork/c_fit,g_north_west,h_1054,w_1054/co_ffffff,e_outline:53/co_ffffff,e_outline:inner_fill:53/co_bbbbbb,e_outline:3:1000/c_mpad,g_center,h_1260,w_1260/b_rgb:eeeeee/c_limit,f_jpg,h_630,q_90,w_630/v1570281377/production/designs/6215195_0.jpg";

    private Context contexto;

    Fecha fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Funciones.setActividadEnUso(true);
        Funciones.setBackPressed(false);
        contexto = getApplicationContext();
        invalidateOptionsMenu();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUserDataBase = getInstance().getReference("Usuarios");
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());
        listaUsuariosBloqueados = Funciones.obtenerUsuariosBloqueados();

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

        mensajesAdapter = new MensajesAdapter(MessageActivity.this,listaMensajes);
        recyclerView.setAdapter(mensajesAdapter);

        enviar_button = findViewById(R.id.enviar_mensaje_button);
        enviar_texto = findViewById(R.id.enviar_mensaje);
        //setOnFocusChangeListener(enviar_texto, "teclado");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);
        estado = findViewById(R.id.estado);

        intent = getIntent();
        usuarioID = intent.getStringExtra("userID");

        //firebaseUser =
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioID);

        enviar_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String mensaje = enviar_texto.getText().toString();

                if(!listaUsuariosBloqueados.containsKey(usuarioChat.getId())){
                    if(!mensaje.equals("")){
                        enviarMensaje(firebaseUser.getUid(),usuarioID,mensaje);
                    }else{
                        Toast.makeText(MessageActivity.this, "No se puede enviar un mensaje vacio", Toast.LENGTH_SHORT).show();
                    }
                    enviar_texto.setText("");
                }else{
                    Toast.makeText(contexto, "Desbloquea al usuario para enviar mensajes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioChat = dataSnapshot.getValue(Usuario.class);
                usuario.setText(usuarioChat.getUsuario());

                if(Funciones.getListaUsuariosBloqueados().containsKey(usuarioChat.getId())){
                    estado.setText(R.string.bloqueado);
                }else if(usuarioChat.getEstado().contentEquals("Desconectado")) {

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
                    Glide.with(MessageActivity.this).load(usuario_profile_default).into(imagen_perfil);
                }else{
                    if (!MessageActivity.this.isFinishing()) {
                        Glide.with(MessageActivity.this).load(usuarioChat.getImagenURL()).into(imagen_perfil);
                    }
                }

                System.out.println("USUARIOS BLOQUEADOS "+listaUsuariosBloqueados.size());

                if(!listaUsuariosBloqueados.containsKey(usuarioChat.getId())){
                    leerMensaje(firebaseUser.getUid(),usuarioChat.getId());
                    System.out.println("HA LEIDO LOS MENSAJES");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarMensaje(String emisor, String receptor, String mensaje){

        fecha = new Fecha();

        Mensaje mensajeObject = new Mensaje();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");

        /*
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",Funciones.getAlphaNumericString(24));
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);
        hashMap.put("hora",fecha.obtenerHora()+":"+fecha.obtenerMinutos());
        hashMap.put("fecha",fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAño());
        hashMap.put("leido","false");

        reference.child("Chats").push().setValue(hashMap);

        */
        //mensajeObject.setKey("");
        mensajeObject.setId(this.fecha.obtenerFechaTotal()+""+Funciones.getAlphaNumericString(8));
        mensajeObject.setEmisor(emisor);
        mensajeObject.setReceptor(receptor);
        mensajeObject.setMensaje(mensaje);
        mensajeObject.setHora(fecha.obtenerHora()+":"+fecha.obtenerMinutos());
        mensajeObject.setHora(fecha.obtenerHora()+":"+fecha.obtenerMinutos());
        mensajeObject.setFecha(fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAño());
        mensajeObject.setLeido("false");

        reference.child(mensajeObject.getId()).setValue(mensajeObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Se ha enviado el mensaje");
                }
            }
        });
    }

    private void leerMensaje(final String id, final String userID){

        listaMensajes = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    if(mensaje.getReceptor().equals(id) && mensaje.getEmisor().equals(userID)
                        || mensaje.getReceptor().equals(userID) && mensaje.getEmisor().equals(id)){
                        //mensaje.setLeido("true");

                        if(!mensaje.getEmisor().contentEquals(id)) {
                            mensaje.setLeido("true");
                        }
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


    /*
    private void setOnFocusChangeListener(TextView textView, String name){

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Toast.makeText(MessageActivity.this, "ESTAS EN EL TECLADO", Toast.LENGTH_SHORT).show();
                    enviar_texto.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            if(!s.equals("") ) {
                               // System.out.println("ESTAS ESCRIBIENDO");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            /*
                            //System.out.println("ESTAS ESCRIBIENDO");
                            if((cadenaTeclado = enviar_texto.getText().toString()).length() > 0){

                                //Funciones.escribiendo(firebaseUser,contexto);
                            }else{

                            }


                        }
                    });
                }
            }
        });
    }  */



    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menuUserFunction = menu;
        try {

            if(listaUsuariosBloqueados.containsKey(usuarioChat.getId())){
                getMenuInflater().inflate(R.menu.context_menu_amigo_2,menu);
            }else{
                getMenuInflater().inflate(R.menu.context_menu_amigo,menu);
            }


        } catch (Exception e) {
            e.printStackTrace();
            //Log.i(TAG, "onCreateOptionsMenu: error: "+e.getMessage());
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.silenciar:

                return true;
            case R.id.bloquear:

                if(listaUsuariosBloqueados.containsKey(usuarioChat.getId())){

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.unblockUser));
                    builder.setMessage(getResources().getString(R.string.confirmUnlock)+" "+usuarioChat.getUsuario());
                    builder.setNegativeButton(getResources().getString(R.string.Cancelar),null);
                    builder.setPositiveButton(getResources().getString(R.string.Aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Funciones.desbloquearUsuario(usuarioChat);
                            //Toast.makeText(contexto, "Se ha desbloqueado el usuario"+usuarioChat.getUsuario(), Toast.LENGTH_SHORT).show();
                            DesignerToast.Success(MessageActivity.this,getResources().getString(R.string.unblocked)+" "+ usuarioChat.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.blockUser));
                    builder.setMessage(getResources().getString(R.string.confirmBlock)+" "+usuarioChat.getUsuario());
                    builder.setNegativeButton(getResources().getString(R.string.Cancelar),null);
                    builder.setPositiveButton(getResources().getString(R.string.Aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Funciones.bloquearUsuario(usuarioChat);
                            //Toast.makeText(contexto, "Has bloqueado a "+usuarioChat.getUsuario(), Toast.LENGTH_SHORT).show();
                            DesignerToast.Warning(MessageActivity.this,getResources().getString(R.string.blockedUser)+" "+ usuarioChat.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                //listaUsuariosBloqueados.put(usuarioChat.getId(),usuarioChat);
                return true;
            case R.id.eliminar:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Funciones.setBackPressed(true);
        super.onBackPressed();
        Intent backToChats = new Intent(MessageActivity.this,StartActivity.class);
        startActivity(backToChats);
    }

}
