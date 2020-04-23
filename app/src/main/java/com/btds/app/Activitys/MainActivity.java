package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.btds.app.Adaptadores.EstadosAdapter;
import com.btds.app.Fragmentos.Amigos;
import com.btds.app.Fragmentos.BuscarAmigos;
import com.btds.app.Fragmentos.Chats;
import com.btds.app.Modelos.EstadosClass;
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
import com.vdx.designertoast.DesignerToast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

//

public class MainActivity extends BasicActivity {


    class TaskProgressBar extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(i);
            }
            return null;
        }
    }

    LinearLayout linearLayout;
    CircleImageView imagen_perfil;
    Button imageProfileButton;
    TextView usuario;
    Usuario usuarioObject;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;
    Fecha fecha;
    ProgressBar progressBar;
    BottomNavigationView bottomNav;

    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;
    DatabaseReference referenceStatus;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new Constantes("81165","kH6Jv3mOn4htJ78","8sfBc3Rr-PaR4Wf","BWMGDip2NKWtdp3Hevc9",getApplicationContext());

        linearLayout = findViewById(R.id.linearLayout_estados);
        fecha = new Fecha();
        //System.out.println(fecha.toString());
        //System.out.println(fecha.obtenerFechaTotal());

        //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);

        bottomNav = findViewById(R.id.navigation_view_bottom_home);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Amigos()).commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        imageProfileButton = findViewById(R.id.imagenProfileButton);
        imageProfileButton.setBackgroundColor(Color.TRANSPARENT);
        usuario = findViewById(R.id.usuario);
        //editText_buscarAmigos = findViewById(R.id.buscar_amigos);
        //editText_buscarAmigos.setVisibility(View.GONE);

        iniciarEstadosLayout();

        imageProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToProfile = new Intent(MainActivity.this,PerfilActivity.class);
                startActivity(intentToProfile);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUserDataBase = getInstance().getReference("Usuarios");
        mainDatabasePath = getInstance().getReference();


        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());


        referenceUserDataBase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this, R.string.errorSesion, Toast.LENGTH_SHORT).show();
                    Intent backToLogin = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(backToLogin);
                    finish();
                }
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


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        }
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
                intent = new Intent(MainActivity.this, PerfilActivity.class);
                //Me ha solucionado un error, Ejemplo abro la camara o galeria en una actividad en el PerfilActivity y al realizar la captura o seleccionar me devuelve a la MainActivity
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //Funciones.setActividadEnUso(true);
                startActivity(intent);
                //Borrar o comentar el comando finish por que sino al volver para atras no realizara la accion ya que la actividad principal esta destruida.
                //finish();
                return true;
            case R.id.salir:
                Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser, getApplicationContext());
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                //Toast.makeText(this, R.string.cerrarSesionCorrectamente, Toast.LENGTH_SHORT).show();
                DesignerToast.Success(MainActivity.this,getResources().getString(R.string.cerrarSesionCorrectamente), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                finish();
                return true;
        }
        return false;
    }

    class ViewPageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentos;
        private ArrayList<String> titulos;

        ViewPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            this.fragmentos = new ArrayList<Fragment>();
            this.titulos = new ArrayList<String>();
        }

        @NonNull
        @Override
        public Fragment getItem(int posicion) {
            return this.fragmentos.get(posicion);
        }

        @Override
        public int getCount() {
            return this.fragmentos.size();
        }

        public void añadirFragmento(Fragment fragmento, String titulo) {
            this.fragmentos.add(fragmento);
            this.titulos.add(titulo);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int posicion) {
            return this.titulos.get(posicion);
        }
    }

    private void iniciarEstadosLayout(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_estados_main);
        recyclerView.setLayoutManager(layoutManager);

        List<EstadosClass> listaEstados = new ArrayList<>();
        listaEstados.add(new EstadosClass("https://www.staffcreativa.pe/blog/wp-content/uploads/logos9.gif","Tus Historias"));
        listaEstados.add(new EstadosClass("https://i.pinimg.com/originals/33/b8/69/33b869f90619e81763dbf1fccc896d8d.jpg","españita"));
        listaEstados.add(new EstadosClass("https://d24jx5gocr6em0.cloudfront.net/wp-content/uploads/2020/04/03160041/negros-con-ataud-520x350.jpg","testUserName"));
        listaEstados.add(new EstadosClass("https://i.pinimg.com/originals/33/b8/69/33b869f90619e81763dbf1fccc896d8d.jpg","españita"));
        listaEstados.add(new EstadosClass("https://cdn.motor1.com/images/mgl/GwZbJ/s3/logo-story-volkswagen.jpg","testUserName"));
        listaEstados.add(new EstadosClass("https://i.pinimg.com/originals/33/b8/69/33b869f90619e81763dbf1fccc896d8d.jpg","españita"));
        listaEstados.add(new EstadosClass("https://cdn.motor1.com/images/mgl/GwZbJ/s3/logo-story-volkswagen.jpg","testUserName"));
        EstadosAdapter adapter = new EstadosAdapter(this,listaEstados);
        recyclerView.setAdapter(adapter);
    }

}




