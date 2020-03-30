package com.btds.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.btds.app.Fragmentos.Amigos;
import com.btds.app.Fragmentos.Chats;
import com.btds.app.Fragmentos.Estados;
import com.btds.app.Modelos.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

//

public class MainActivity extends AppCompatActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    Usuario usuarioObject;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;
    Funciones funciones = new Funciones();
    Fecha fecha;

    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fecha = new Fecha();
        System.out.println(fecha.toString());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUserDataBase = getInstance().getReference("Usuarios");
        mainDatabasePath = getInstance().getReference();

        //actualizarBaseDatos();
        funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getApplicationContext());


        referenceUserDataBase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);

                if(usuarioObject !=null){
                    usuario.setText(usuarioObject.getUsuario());

                    if(usuarioObject.getImagenURL().equals("default")){
                        imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        //https://stackoverflow.com/questions/39093730/you-cannot-start-a-load-for-a-destroyed-activity-in-relativelayout-image-using-g
                        //getApplicationContext() FOR FIX
                        Glide.with(getApplicationContext()).load(usuarioObject.getImagenURL()).into(imagen_perfil);
                    }
                }else{
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this, R.string.errorSesion, Toast.LENGTH_SHORT).show();
                    Intent backToLogin = new Intent(MainActivity.this,StartActivity.class);
                    startActivity(backToLogin);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.añadirFragmento(new Chats(),"Chats");
        viewPageAdapter.añadirFragmento(new Amigos(),"Amigos");
        viewPageAdapter.añadirFragmento(new Estados(),"Estados");
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.salir:
                Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser, referenceUserDataBase, getApplicationContext());
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                Toast.makeText(this, R.string.cerrarSesionCorrectamente, Toast.LENGTH_SHORT).show();
                finish();
                return true;
        }
        return false;
    }

    class ViewPageAdapter extends FragmentPagerAdapter{

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

        public void añadirFragmento(Fragment fragmento, String titulo){
            this.fragmentos.add(fragmento);
            this.titulos.add(titulo);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int posicion) {
            return this.titulos.get(posicion);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        viewPageAdapter.notifyDataSetChanged();
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser,referenceUserDataBase, getApplicationContext());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!Funciones.getActividadEnUso()){
            Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser,referenceUserDataBase, getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPageAdapter.notifyDataSetChanged();
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser,referenceUserDataBase, getApplicationContext());
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(!Funciones.getActividadEnUso()){
            Funciones.actualizarConexion(getResources().getString(R.string.offline), firebaseUser,referenceUserDataBase, getApplicationContext());
        }
    }


}
