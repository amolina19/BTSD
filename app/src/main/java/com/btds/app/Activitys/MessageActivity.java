package com.btds.app.Activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

/**
 * @author Alejandro Molina Louchnikov
 */
public class MessageActivity extends BasicActivity {

    private CircleImageView imagen_perfil;
    private TextView usuario;
    private TextView estado;

    //Referencias necesarias con la base de datos.
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    private EditText enviar_texto;
    //private String cadenaTeclado;

    private Usuario usuarioChat;
    private String usuarioID;
    private int diasPasados;

    private MensajesAdapter mensajesAdapter;
    private List<Mensaje> listaMensajes;
    private HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados;
    private RecyclerView recyclerView;
    private String usuario_profile_default= "https://res.cloudinary.com/teepublic/image/private/s--6vDtUIZ---/t_Resized%20Artwork/c_fit,g_north_west,h_1054,w_1054/co_ffffff,e_outline:53/co_ffffff,e_outline:inner_fill:53/co_bbbbbb,e_outline:3:1000/c_mpad,g_center,h_1260,w_1260/b_rgb:eeeeee/c_limit,f_jpg,h_630,q_90,w_630/v1570281377/production/designs/6215195_0.jpg";

    //Obtener el contexto de la actividad
    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //Funciones.setActividadEnUso(true);
        //Funciones.setBackPressed(false);
        contexto = getApplicationContext();
        invalidateOptionsMenu();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getInstance().getReference("Usuarios");
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        listaUsuariosBloqueados = Funciones.obtenerUsuariosBloqueados(firebaseUser);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view_mensaje);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mensajesAdapter = new MensajesAdapter(MessageActivity.this,listaMensajes);
        recyclerView.setAdapter(mensajesAdapter);

        ImageButton enviar_button = findViewById(R.id.enviar_mensaje_button);
        enviar_texto = findViewById(R.id.enviar_mensaje);
        //setOnFocusChangeListener(enviar_texto, "teclado");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);
        estado = findViewById(R.id.estado);

        Intent intent = getIntent();
        usuarioID = intent.getStringExtra("userID");
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioID);

        enviar_button.setOnClickListener(v -> {
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
        });

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioChat = dataSnapshot.getValue(Usuario.class);
                assert usuarioChat != null;
                usuario.setText(usuarioChat.getUsuario());


                //Comprobar si esta bloqueado y así descartar el calculo de días de su última conexión.
                if(Funciones.getListaUsuariosBloqueados().containsKey(usuarioChat.getId())){
                    estado.setText(R.string.bloqueado);


                    //Si esta desconectado calculamos su última conexión.
                }else if(usuarioChat.getEstado().contentEquals(getResources().getString(R.string.desconectado))) {

                    diasPasados = (int) Funciones.obtenerDiasPasados(usuarioChat);
                    estado.setText(Funciones.obtenerEstadoUsuario(contexto,diasPasados,usuarioChat));
                    //Si el usuario esta en línea se descarta el cálculo de su última conexión y el TextView sera En Línea.
                }else {
                    estado.setText(usuarioChat.getEstado());
                }
                    //Si su perfil contiene en su atributo de ImagenURL default se cargará una imagen de pérfil.
                if(usuarioChat.getImagenURL().equals("default")){
                    Glide.with(MessageActivity.this).load(usuario_profile_default).into(imagen_perfil);
                }else{
                    //Este if comprueba que la actividad no esta destruida o en pausa, ya que si se intenta cargar una imagén antes o después de iniciar la actividad, crasheará.
                    if (!MessageActivity.this.isFinishing()) {
                        Glide.with(MessageActivity.this).load(usuarioChat.getImagenURL()).into(imagen_perfil);
                    }
                }
                //Esta línea está para comprobar en modo depuración si los datos visuales se corresponden a los de este log.
                Log.d("DEBUG Usuarios Bloqueados","USUARIOS BLOQUEADOS " + listaUsuariosBloqueados.size());

                if(!listaUsuariosBloqueados.containsKey(usuarioChat.getId())){
                    leerMensaje(firebaseUser.getUid(),usuarioChat.getId());
                    //De todos los mensajes recibidos por parte nuestra conversación han sido leídos.
                    Log.d("DEBUG Mensajes Leidos","HA LEIDO LOS MENSAJES");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Función para enviar mensajes a un determinado usuario.
     * @param emisor es el ID del objeto Usuario (Nuestro usuario).
     * @param receptor es el ID del objeto Usuario con el que estamos en conversación.
     * @param mensaje es el mensaje a enviar.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarMensaje(String emisor, String receptor, String mensaje){

        //Luego instanciar la fecha actual del dispositivo.
        Fecha fecha = new Fecha();

        Mensaje mensajeObject = new Mensaje();
        reference = Funciones.getChatsDatabaseReference();

        mensajeObject.setId(fecha.obtenerFechaTotal()+""+Funciones.getAlphaNumericString(8));
        mensajeObject.setEmisor(emisor);
        mensajeObject.setReceptor(receptor);
        mensajeObject.setMensaje(mensaje);
        mensajeObject.setHora(fecha.obtenerHora()+":"+ fecha.obtenerMinutos());
        mensajeObject.setHora(fecha.obtenerHora()+":"+ fecha.obtenerMinutos());
        mensajeObject.setFecha(fecha.obtenerDia()+" "+ fecha.obtenerMes()+" "+ fecha.obtenerAnno());
        mensajeObject.setLeido("false");

        reference.child(mensajeObject.getId()).setValue(mensajeObject).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("DEBUG Mensaje","Se ha enviado el mensaje");
            }
        });
    }

    /**
     * Función para leer mensajes con un determinado usuario.
     * @param id es el ID del objeto Usuario (Nuestro usuario).
     * @param userID es el ID del objeto Usuario con el que estamos en conversación.
     */

    private void leerMensaje(final String id, final String userID){

        listaMensajes = new ArrayList<>();

        reference = Funciones.getChatsDatabaseReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    assert mensaje != null;
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
                Log.d("DEBUG Lista mensajes totales recibidos","LISTA MENSAJES "+ listaMensajes.size());
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

    /**
     * Parámetro donde al iniciar la actividad se inflara el menú según si esta bloqueado o no.
     * @param menu parámetro que se introducirá al generar la actividad.
     * @return devuelve true indicando de que esta correcto.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {

            if(listaUsuariosBloqueados.containsKey(usuarioChat.getId())){
                getMenuInflater().inflate(R.menu.context_menu_amigo_2,menu);
            }else{
                getMenuInflater().inflate(R.menu.context_menu_amigo,menu);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("DEBUG Menu inflating error","onCreateOptionsMenu: error: "+e.getMessage());
        }
        return true;
    }

    /**
     * Menú contextual para las funcionalidades del usuario.
     * @param item es el objeto que se introducirá según la opción que el usuario eliga.
     * @return devuelve true indicando que está ok.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.silenciar:

                //Se desactivara las notifcaciones del usuario en cuestión.
                return true;

                //El id no cambia, ya que esto está definido en el layout de menu/context_menu_amigo de manera estática
            case R.id.bloquear:

                //Si el usuario está bloqueado, en el menú inflado aparecerá el String desbloquear donde nos mostrará un diálogo de confirmación por parte del usuario.
                if(listaUsuariosBloqueados.containsKey(usuarioChat.getId())){

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.unblockUser));
                    builder.setMessage(getResources().getString(R.string.confirmUnlock)+" "+usuarioChat.getUsuario());
                    builder.setNegativeButton(getResources().getString(R.string.Cancelar),null);
                    builder.setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, which) -> {
                        Funciones.desbloquearUsuario(usuarioChat);
                        //Toast.makeText(contexto, "Se ha desbloqueado el usuario"+usuarioChat.getUsuario(), Toast.LENGTH_SHORT).show();
                        DesignerToast.Success(MessageActivity.this,getResources().getString(R.string.unblocked)+" "+ usuarioChat.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                        //Refresca la actividad para mostrar los cambios.
                        finish();
                        startActivity(getIntent());
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.blockUser));
                    builder.setMessage(getResources().getString(R.string.confirmBlock)+" "+usuarioChat.getUsuario());
                    builder.setNegativeButton(getResources().getString(R.string.Cancelar),null);
                    builder.setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, which) -> {
                        Funciones.bloquearUsuario(usuarioChat);
                        //Toast.makeText(contexto, "Has bloqueado a "+usuarioChat.getUsuario(), Toast.LENGTH_SHORT).show();
                        DesignerToast.Warning(MessageActivity.this,getResources().getString(R.string.blockedUser)+" "+ usuarioChat.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                        //Refresca la actividad para mostrar los cambios.
                        finish();
                        startActivity(getIntent());
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return true;
            case R.id.eliminar:
                    //Eliminar a un usuario de nuestra lista de amigos.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //Funciones.setBackPressed();
        super.onBackPressed();
        Intent backToChats = new Intent(MessageActivity.this,StartActivity.class);
        startActivity(backToChats);
        finish();
    }

}
