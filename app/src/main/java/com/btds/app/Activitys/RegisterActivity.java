package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vdx.designertoast.DesignerToast;

import java.util.Objects;

/**
 * @author Alejandro Molina Louchnikov
 */

public class RegisterActivity extends BasicActivity {


    /*
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
    */



    String userID;
    MaterialEditText username, email, password;
    Button button_registrar;


    //Auntetificacion Firebase
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Usuario usuarioRegistrandose;

    Fecha fecha;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        reference = FirebaseDatabase.getInstance().getReference();
        //Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.registrarse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //DEPRECATED API 23 getResources().getColor(
        toolbar.setTitleTextColor(this.getColor(R.color.colorBlanco));

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_registrar = findViewById(R.id.button_registrar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        //Instancio el autentificador
        mAuth = FirebaseAuth.getInstance();

        button_registrar.setOnClickListener(v -> {

            String text_usuario = Objects.requireNonNull(username.getText()).toString();
            String text_email = Objects.requireNonNull(email.getText()).toString();
            String text_password = Objects.requireNonNull(password.getText()).toString();

            if(text_usuario.isEmpty() || text_email.isEmpty() || text_password.isEmpty()){
                Toast.makeText(RegisterActivity.this, R.string.completar, Toast.LENGTH_SHORT).show();
            }else if(text_password.length()<8){
                Toast.makeText(RegisterActivity.this, R.string.contraseñaPequeña, Toast.LENGTH_SHORT).show();
            }else {
                registrarse(text_usuario,text_email,text_password);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registrarse(final String username, String email, String password){

        fecha = new Fecha();
        progressBar.setVisibility(View.VISIBLE);
        //new TaskProgressBar().execute();


        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        userID = firebaseUser.getUid();
                        //Donde se generan los campos en la base de datos
                        usuarioRegistrandose = new Usuario();

                        /*
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("id",userID);
                        hashMap.put("usuario",username);
                        hashMap.put("imagenURL","default");
                        hashMap.put("hora",saveCurrentTime);
                        hashMap.put("fecha",saveCurrentDate);
                        hashMap.put("estado","En Linea");
                        */

                        usuarioRegistrandose.setId(userID);
                        usuarioRegistrandose.setUsuario(username);
                        usuarioRegistrandose.setImagenURL("default");
                        usuarioRegistrandose.setHora(fecha.obtenerHora()+":"+fecha.obtenerMinutos());
                        usuarioRegistrandose.setFecha(fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAnno());
                        usuarioRegistrandose.setEstado("En Linea");
                        Toast.makeText(RegisterActivity.this, "Cuenta creada", Toast.LENGTH_SHORT).show();


                        reference.child("Usuarios").child(firebaseUser.getUid()).setValue(usuarioRegistrandose).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                //Toast.makeText(RegisterActivity.this, R.string.cuentaCreada, Toast.LENGTH_SHORT).show();

                                DesignerToast.Success(RegisterActivity.this, getResources().getString(R.string.cuentaCreada), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                                Intent intentRegister = new Intent(RegisterActivity.this,MainActivity.class);
                                intentRegister.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentRegister);
                                finish();
                            }
                        });
                    }
                });
    }

}


