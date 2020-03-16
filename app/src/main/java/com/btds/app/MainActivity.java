package com.btds.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
//

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView imagen_perfil;
    TextView usuario;
    Usuario usuarioObject;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);
                usuario.setText(usuarioObject.getUsuario());

                if(usuarioObject.getImagenURL().equals("default")){
                    imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MainActivity.this).load(usuarioObject.getImagenURL()).into(imagen_perfil);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class));
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
}
