package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.EstadosAdapter;
import com.btds.app.Fragmentos.Amigos;
import com.btds.app.Fragmentos.BuscarAmigos;
import com.btds.app.Fragmentos.Chats;
import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Constantes;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.vdx.designertoast.DesignerToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alejandro Molina Louchnikov
 */


public class MainActivity extends BasicActivity {

    LinearLayout linearLayout;
    CircleImageView imagen_perfil;
    Button imageProfileButton;
    TextView usuario;
    Usuario usuarioObject;
    Fecha fecha;
    BottomNavigationView bottomNav;
    List<Estados> listaEstados;
    LinearLayout linearLayoutMainActivity;

    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG ","MainActivity Created");

        //new Constantes("81165","kH6Jv3mOn4htJ78","8sfBc3Rr-PaR4Wf","BWMGDip2NKWtdp3Hevc9",getApplicationContext());

        bottomNav = findViewById(R.id.navigation_view_bottom_home);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_amigos);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Amigos()).commit();

        linearLayoutMainActivity = findViewById(R.id.linearLayoutMainActivity);

        linearLayout = findViewById(R.id.linearLayout_estados);
        fecha = new Fecha();
        Log.d("DEBUG FECHA",java.time.LocalDateTime.now().toString());
        Log.d("DEBUG MainActivity ","Creacion de la actividad "+fecha.toString());
        //System.out.println(fecha.toString());
        //System.out.println(fecha.obtenerFechaTotal());

        //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        imageProfileButton = findViewById(R.id.imagenProfileButton);
        imageProfileButton.setBackgroundColor(Color.TRANSPARENT);
        usuario = findViewById(R.id.usuario);
        //editText_buscarAmigos = findViewById(R.id.buscar_amigos);
        //editText_buscarAmigos.setVisibility(View.GONE);

        //Log.d("DEBUG MainActivity","##Lista estados "+listaEstados.size());

        imageProfileButton.setOnClickListener(v -> {
            Intent intentToProfile = new Intent(MainActivity.this,PerfilActivity.class);
            startActivity(intentToProfile);
        });

        firebaseUser = Funciones.getFirebaseUser();
        referenceUserDataBase = Funciones.getUsersDatabaseReference();
        mainDatabasePath = Funciones.getDatabaseReference();


        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());


        referenceUserDataBase.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);

                if (usuarioObject != null) {
                    usuario.setText(usuarioObject.getUsuario());

                    if (usuarioObject.getImagenURL().equals("default")) {
                        imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        //https://stackoverflow.com/questions/39093730/you-cannot-start-a-load-for-a-destroyed-activity-in-relativelayout-image-using-g
                        //getApplicationContext() FOR FIX
                        if (!MainActivity.this.isFinishing()) {
                            Glide.with(getApplicationContext()).load(usuarioObject.getImagenURL()).into(imagen_perfil);
                        }
                    }

                    Funciones.mostrarDatosUsuario(usuarioObject);

                    if(usuarioObject.getTelefono().contentEquals("")){
                        Intent intentCheckPhone = new Intent(MainActivity.this,PhoneCheckActivity.class);
                        startActivity(intentCheckPhone);
                        finish();
                    }


                } else {
                    Funciones.getAuthenticationInstance().signOut();
                    Toast.makeText(MainActivity.this, R.string.errorSesion, Toast.LENGTH_SHORT).show();
                    Intent backToLogin = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(backToLogin);
                    finish();
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
                RecyclerView recyclerView = findViewById(R.id.recycler_view_estados_main);
                recyclerView.setLayoutManager(layoutManager);


                listaEstados = new ArrayList<>();
                HashMap<String,Usuario> usuariosConEstados = new HashMap<>();

                Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaEstados.clear();
                        usuariosConEstados.clear();

                        listaEstados.add(new Estados(Constantes.default_history_image,usuarioObject,new Fecha()));
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            //Log.d("DEBUG MainActivity obtenerEstados","ESTADO ITERADO");
                            Estados estado = data.getValue(Estados.class);
                            assert estado != null;
                            //Log.d("DEBUG MainActivity INFORMACION ESTADO",estado.toString());

                            if(!estado.usuario.getUsuario().contentEquals(usuarioObject.getUsuario())) {
                                if(!usuariosConEstados.containsKey(estado.getUsuario().getId())){
                                    usuariosConEstados.put(estado.usuario.getId(),estado.getUsuario());
                                    listaEstados.add(estado);
                                }

                            }
                        }


                        Log.d("DEBUG MainActivity","Lista de estados "+listaEstados.size());
                        EstadosAdapter estadosAdapter = new EstadosAdapter(MainActivity.this,listaEstados);
                        recyclerView.setAdapter(estadosAdapter);
                        estadosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        //viewPageAdapter.añadirFragmento(new Chats(), "Chats");
        //viewPageAdapter.añadirFragmento(new Amigos(), "Amigos");
        //viewPageAdapter.añadirFragmento(new Estados(), "Estados");
        //viewPageAdapter.añadirFragmento(new BuscarAmigos(), "Buscar");
        //viewPager.setAdapter(viewPageAdapter);
        //tabLayout.setupWithViewPager(viewPager);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()){
            case R.id.nav_chats:
                selectedFragment = new Chats();
                break;
            case R.id.nav_amigos:
                selectedFragment = new Amigos();
                break;
            case R.id.nav_buscar:
                selectedFragment = new BuscarAmigos();
                break;
        }

        if(selectedFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
        }
        return true;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.perfil:
                intent = new Intent(this, PerfilActivity.class);
                //Me ha solucionado un error, Ejemplo abro la camara o galeria en una actividad en el PerfilActivity y al realizar la captura o seleccionar me devuelve a la MainActivity
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //Funciones.setActividadEnUso(true);
                startActivity(intent);
                //Borrar o comentar el comando finish por que sino al volver para atras no realizara la accion ya que la actividad principal esta destruida.
                //finish();
                return true;
            case R.id.salir:
                Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser);
                intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                //Toast.makeText(this, R.string.cerrarSesionCorrectamente, Toast.LENGTH_SHORT).show();
                DesignerToast.Success(MainActivity.this,getResources().getString(R.string.cerrarSesionCorrectamente), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                finish();
                FirebaseAuth.getInstance().signOut();
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}



