package com.btds.app.Activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vdx.designertoast.DesignerToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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
    private DatabaseReference chatsReference;

    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fabOpen, fabClose, backRotateForward, rotateBackWard;
    private Boolean fabIsOpen = false;

    private EditText enviar_texto;
    //private String cadenaTeclado;

    private Usuario usuarioChat;
    private String usuarioID;
    private int diasPasados;

    private Boolean ubicacion = false;

    private MensajesAdapter mensajesAdapter;
    private List<Mensaje> listaMensajes;
    private HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados;
    private RecyclerView recyclerView;

    //Obtener el contexto de la actividad
    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Log.d("DEBUG ","MessageActivity Created");
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
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        backRotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackWard = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);
        fab.setOnClickListener(v -> animarFab());
        fab1.setOnClickListener(v -> {
            abrirCamara();
            Toast.makeText(contexto, "ABRIENDO CAMARA", Toast.LENGTH_SHORT).show();
        });
        fab2.setOnClickListener(v -> Toast.makeText(contexto, "ABRIENDO MICRO", Toast.LENGTH_SHORT).show());
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ubicacion == false){
                    preguntarPermisosLocalizacion();
                }else{
                    animarFab();

                    Intent intentMap = new Intent(MessageActivity.this,MapsActivity.class);
                    intentMap.putExtra("userID",usuarioChat.getId());
                    startActivity(intentMap);
                }
            }
        });

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
        assert usuarioID != null;
        chatsReference = Funciones.getUsersDatabaseReference().child(usuarioID);

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

        chatsReference.addValueEventListener(new ValueEventListener() {
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
                    if(!MessageActivity.this.isFinishing()){
                        Glide.with(MessageActivity.this).load(R.drawable.default_user_picture).into(imagen_perfil);
                    }
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

    private void animarFab(){

        if(fabIsOpen){
            fab.startAnimation(rotateBackWard);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab3.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab1.setVisibility(View.INVISIBLE);
            fab2.setVisibility(View.INVISIBLE);
            fab3.setVisibility(View.INVISIBLE);
            fabIsOpen = false;
        }else{
            fab.startAnimation(backRotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab3.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab1.setVisibility(View.VISIBLE);
            fab2.setVisibility(View.VISIBLE);
            fab3.setVisibility(View.VISIBLE);
            fabIsOpen = true;
        }
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
        //Fecha fecha = new Fecha();
        String id = new Fecha().obtenerFechaTotal();
        Mensaje.Tipo tipoMensaje = Mensaje.Tipo.TEXTO;
        Mensaje mensajeObject = new Mensaje(id,emisor,receptor,mensaje,tipoMensaje,false,new Fecha());
        chatsReference = Funciones.getChatsDatabaseReference();

        chatsReference.child(mensajeObject.getId()).setValue(mensajeObject).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("DEBUG Mensaje","Se ha enviado el mensaje");
            }
        });
    }

    private void enviarFoto(String emisor, String receptor, String URLFoto){

        //Luego instanciar la fecha actual del dispositivo.
        //Fecha fecha = new Fecha();
        String id = new Fecha().obtenerFechaTotal();
        Mensaje.Tipo tipoMensaje = Mensaje.Tipo.FOTO;
        Mensaje mensajeObject = new Mensaje(id,emisor,receptor,URLFoto,tipoMensaje,false,new Fecha());
        chatsReference = Funciones.getChatsDatabaseReference();

        chatsReference.child(mensajeObject.getId()).setValue(mensajeObject).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("DEBUG Mensaje","Se ha enviado La foto");
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

        chatsReference = Funciones.getChatsDatabaseReference();
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    assert mensaje != null;
                    if(mensaje.getReceptor().equals(id) && mensaje.getEmisor().equals(userID) || mensaje.getReceptor().equals(userID) && mensaje.getEmisor().equals(id)){
                        //mensaje.setLeido("true");

                        if(!mensaje.getEmisor().contentEquals(id)) {
                            mensaje.setLeido(true);
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
        Intent backToChats = new Intent(MessageActivity.this,MainActivity.class);
        startActivity(backToChats);
        finish();
    }

    @AfterPermissionGranted(1)
    public void abrirCamara(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(EasyPermissions.hasPermissions(this,perms)){
            ImagePicker.create(MessageActivity.this).returnMode(ReturnMode.GALLERY_ONLY).single().start();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoAbrirCamara),1,perms);
        }
        //
    }

    @AfterPermissionGranted(2)
    public void preguntarPermisosLocalizacion() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (EasyPermissions.hasPermissions(this, perms)) {
            //Si tiene permisos
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ubicacion = true;
            fab3.performClick();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoLocalizacion),2,perms);
        }
        //
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            //Log.d("DEBUG PerfilActivity","IMAGEN PATH "+image.getPath());
            //Toast.makeText(this, getResources().getString(R.string.actualizandoImagenPerfil), Toast.LENGTH_SHORT).show();
            subirImagen(image);
        }
        animarFab();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void subirImagen(Image image){

        Uri file = Uri.fromFile(new File(image.getPath()));

        //FirebaseStorage storage;
        //StorageReference storageReference;

        //storage = FirebaseStorage.getInstance();
        StorageReference storageReference = Funciones.getFirebaseStorageReference();
        String generated = Funciones.getAlphaNumericString(16);
        StorageReference storageUserProfileRef = storageReference.child("Chats/"+firebaseUser.getUid()+"/"+generated);
        UploadTask uploadTask = storageUserProfileRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            //Log.d("DEBUG PerfilActivity","LA IMAGEN NO SE HA SUBIDO");
            //Toast.makeText(M.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            storageUserProfileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                // getting image uri and converting into string
                //usuarioObject.setImagenURL(downloadUrl.toString());
                enviarFoto(firebaseUser.getUid(),usuarioID,downloadUrl.toString());
            });
            //Log.d("DEBUG PerfilActivity","La imagen se ha subido al perfil de "+usuarioObject.getUsuario());
        });

    }

}