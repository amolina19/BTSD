package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.viewpager.widget.ViewPager;

import com.btds.app.Fragmentos.Amigos;
import com.btds.app.Fragmentos.BuscarAmigos;
import com.btds.app.Fragmentos.Chats;
import com.btds.app.Fragmentos.Estados;
import com.btds.app.Modelos.Constantes;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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


    CircleImageView imagen_perfil;
    TextView usuario;
    Usuario usuarioObject;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;
    Fecha fecha;
    ProgressBar progressBar;
    EditText editText_buscarAmigos;
    Button añadirAmigosButton;
    Constantes constantes;



    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;
    DatabaseReference referenceStatus;


    Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new Constantes("81165","kH6Jv3mOn4htJ78","8sfBc3Rr-PaR4Wf","BWMGDip2NKWtdp3Hevc9",getApplicationContext());

        fecha = new Fecha();
        //System.out.println(fecha.toString());
        //System.out.println(fecha.obtenerFechaTotal());

        bitmap = null;

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuario);
        //editText_buscarAmigos = findViewById(R.id.buscar_amigos);
        //editText_buscarAmigos.setVisibility(View.GONE);

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

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.añadirFragmento(new Chats(), "Chats");
        viewPageAdapter.añadirFragmento(new Amigos(), "Amigos");
        viewPageAdapter.añadirFragmento(new Estados(), "Estados");
        viewPageAdapter.añadirFragmento(new BuscarAmigos(), "Buscar");
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

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
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Funciones.setActividadEnUso(true);
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
                Toast.makeText(this, R.string.cerrarSesionCorrectamente, Toast.LENGTH_SHORT).show();
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

}
    /*
    private void SelectImage() {
        //Intent takeImageIntent = ImagePicker.getPickImageIntent(this);
        if (takeImageIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takeImageIntent, 5);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            //Bitmap bitmap = ImagePicker.getBitmapFromResult(this, resultCode, data);
            //imagen_perfil.setImageBitmap(bitmap);
            if (null != bitmap && resultCode == RESULT_OK) {

                String imagenNAME = "test123";

                //String savedTo = ImagePicker.writeImage(getApplicationContext(), bitmap, imagenNAME);
                System.out.println(savedTo);
                System.out.println("HA ENTRADO");
                imagen_perfil.setImageBitmap(bitmap);

            }else{
                System.out.println("NO HA ENTRADO");
            }



    }


     */



