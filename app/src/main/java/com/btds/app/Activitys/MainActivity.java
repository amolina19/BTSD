package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.btds.app.BuildConfig;
import com.btds.app.Fragmentos.Amigos;
import com.btds.app.Fragmentos.BuscarAmigos;
import com.btds.app.Fragmentos.Chats;
import com.btds.app.Fragmentos.Llamadas;
import com.btds.app.Fragmentos.Peticiones;
import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.Llamada;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
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
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
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

    private static final String APP_KEY = "b464b750-1044-4c6c-91f0-d35dde526b58";
    private static final String APP_SECRET = "go21mH/XrUSnK12FmjNtdA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    LinearLayout linearLayout, linearLayoutMainActivity;
    CircleImageView imagen_perfil;
    Button imageProfileButton;
    TextView usuario;
    Usuario usuarioActual;
    Fecha fecha;
    BottomNavigationView bottomNav;
    List<Estados> listaEstados;
    private static HashMap<String,String> listaAmigos = new HashMap<>();
    private static HashMap<String, Usuario> usuariosConEstados = new HashMap<>();
    RecyclerView recyclerView;

    final private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceUserDataBase, mainDatabasePath;
    private Boolean intentTime = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (firebaseUser == null) {
            Intent backToStartActivity = new Intent(MainActivity.this, StartActivity.class);
            startActivity(backToStartActivity);
            finish();
        }
        //new Constantes("81165","kH6Jv3mOn4htJ78","8sfBc3Rr-PaR4Wf","BWMGDip2NKWtdp3Hevc9",getApplicationContext());

        bottomNav = findViewById(R.id.navigation_view_bottom_home);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_amigos);

        linearLayoutMainActivity = findViewById(R.id.linearLayoutMainActivity);

        linearLayout = findViewById(R.id.linearLayout_estados);
        fecha = new Fecha();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        imageProfileButton = findViewById(R.id.imagenProfileButton);
        imageProfileButton.setBackgroundColor(Color.TRANSPARENT);
        usuario = findViewById(R.id.usuario);

        if (BuildConfig.DEBUG && !(MainActivity.this.isFinishing() || firebaseUser != null)) {
            throw new AssertionError("Assertion failed");
        }

        imageProfileButton.setOnClickListener(v -> {
            Intent intentToProfile = new Intent(MainActivity.this, PerfilActivity.class);
            intentTime = true;
            startActivity(intentToProfile);
        });

        referenceUserDataBase = Funciones.getUsersDatabaseReference();
        mainDatabasePath = Funciones.getDatabaseReference();


        assert firebaseUser != null;
        referenceUserDataBase.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioActual = dataSnapshot.getValue(Usuario.class);

                if (usuarioActual != null) {

                    if(usuarioActual.getVisibilidad() == null){
                        Funciones.VisibilidadUsuarioPublica(usuarioActual);
                    }else{
                        usuarioActual.setVisibilidad(Funciones.corregirVisibilidadPerfilIniciarSesion(MainActivity.this, usuarioActual));
                    }
                    usuario.setText(usuarioActual.getUsuario());

                    if (usuarioActual.getImagenURL().equals("default")) {
                        //String URLdefault = Constantes.default_image_profile;
                        Glide.with(getApplicationContext()).load(R.drawable.default_user_picture).into(imagen_perfil);
                    } else {
                        //https://stackoverflow.com/questions/39093730/you-cannot-start-a-load-for-a-destroyed-activity-in-relativelayout-image-using-g
                        //getApplicationContext() FOR FIX
                        if (!MainActivity.this.isFinishing()) {
                            Glide.with(getApplicationContext()).load(usuarioActual.getImagenURL()).into(imagen_perfil);
                        }
                    }

                    Funciones.mostrarDatosUsuario(usuarioActual);
                    Funciones.actualizarConexion(getResources().getString(R.string.online), usuarioActual);

                    if(usuarioActual.getT2Aintroduced() == null){
                        Funciones.actualizarT2A(false, usuarioActual);
                    }
                    if (usuarioActual.getTwoAunthenticatorFactor() == null || (usuarioActual.getTwoAunthenticatorFactor() && !usuarioActual.getT2Aintroduced())) {
                        Intent intentCheckPhone = new Intent(MainActivity.this, PhoneCheckActivity.class);
                        intentCheckPhone.putExtra("usuario", usuarioActual);
                        startActivity(intentCheckPhone);
                        finish();
                    }

                    String userTest = "usertest";
                    SinchClient sinchClient = Sinch.getSinchClientBuilder()
                            .context(MainActivity.this)
                            .userId(userTest)
                            .applicationKey(APP_KEY)
                            .applicationSecret(APP_SECRET)
                            .environmentHost(ENVIRONMENT)
                            .build();

                    sinchClient.setSupportCalling(true);
                    sinchClient.startListeningOnActiveConnection();
                    sinchClient.start();

                    DatabaseReference llamadasRecibidas = Funciones.getLlamadasReference().child(usuarioActual.getId());
                    List<Llamada> listaLlamadas = new ArrayList<>();
                    llamadasRecibidas.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaLlamadas.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Llamada llamada = data.getValue(Llamada.class);
                                assert llamada != null;
                                if(!llamada.isFinalizado()){
                                    listaLlamadas.add(llamada);
                                }

                            }
                            if (listaLlamadas.size() > 0) {
                                Intent intentCall = new Intent(MainActivity.this, CallActivity.class);
                                intentCall.putExtra("llamada", listaLlamadas.get(listaLlamadas.size() - 1));
                                intentCall.putExtra("usuarioActual", usuarioActual);
                                startActivity(intentCall);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    Funciones.getAuthenticationInstance().signOut();
                    Toast.makeText(MainActivity.this, R.string.errorSesion, Toast.LENGTH_SHORT).show();
                    Intent backToLogin = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(backToLogin);
                    finish();
                }


                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView = findViewById(R.id.recycler_view_estados_main);
                recyclerView.setLayoutManager(layoutManager);

                listaEstados = new ArrayList<>();
                obtenerAmigos();


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerAmigos(){

        Funciones.getAmigosReference().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAmigos.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String valorClaveAmigo = data.getValue(String.class);
                    listaAmigos.put(valorClaveAmigo,valorClaveAmigo);
                }
                obtenerEstados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerEstados(){
        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEstados.clear();
                usuariosConEstados.clear();

                listaEstados.add(new Estados("default", "default", usuarioActual, new Fecha()));
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Estados estado = data.getValue(Estados.class);
                    assert estado != null;
                    if (!estado.usuario.getUsuario().contentEquals(usuarioActual.getUsuario())) {
                        if (!usuariosConEstados.containsKey(estado.getUsuario().getId()) && listaAmigos.containsKey(estado.usuario.getId())) {
                            usuariosConEstados.put(estado.usuario.getId(), estado.getUsuario());
                            if(listaAmigos.containsKey(estado.usuario.getId()) && usuariosConEstados.containsKey(estado.getUsuario().getId())){
                                listaEstados.add(estado);
                            }

                        }

                    }
                }

                EstadosAdapter estadosAdapter = new EstadosAdapter(MainActivity.this, listaEstados);
                recyclerView.setAdapter(estadosAdapter);
                estadosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {

            case R.id.nav_chats:
                selectedFragment = new Chats();
                break;

            case R.id.nav_amigos:
                selectedFragment = new Amigos();
                break;
            case R.id.nav_peticiones:
                selectedFragment = new Peticiones();
                break;

            case R.id.nav_buscar:
                selectedFragment = new BuscarAmigos();
                break;
            case R.id.nav_llamadas:
                selectedFragment = new Llamadas();
        }

        if (selectedFragment != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Chats()).commit();
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
                intentTime = true;
                startActivity(intent);
                return true;
            case R.id.salir:
                usuarioActual.setVisibilidad(Funciones.corregirVisibilidad(this));
                Funciones.actualizarConexion(getResources().getString(R.string.offline), usuarioActual);
                Funciones.actualizarT2A(false, usuarioActual);
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                DesignerToast.Success(MainActivity.this, getResources().getString(R.string.cerrarSesionCorrectamente), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                finish();
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseUser != null && !intentTime) {
            Funciones.actualizarConexion(getApplicationContext().getResources().getString(R.string.offline), usuarioActual);
        }
        finish();
    }

}



