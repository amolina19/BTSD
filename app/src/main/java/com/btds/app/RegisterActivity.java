package com.btds.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.btds.app.Modelos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String userID;
    MaterialEditText username, email, password;
    Button button_registrar;


    //Auntetificacion Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        reference = FirebaseDatabase.getInstance().getReference();
        //Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.registrarse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlanco));

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_registrar = findViewById(R.id.button_registrar);


        //Instancio el autentificador
        mAuth = FirebaseAuth.getInstance();

        button_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text_usuario = username.getText().toString();
                String text_email = email.getText().toString();
                String text_password = password.getText().toString();

                if(text_usuario.isEmpty() || text_email.isEmpty() || text_password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, R.string.completar, Toast.LENGTH_SHORT).show();
                }else if(text_password.length()<8){
                    Toast.makeText(RegisterActivity.this, R.string.contraseñaPequeña, Toast.LENGTH_SHORT).show();
                }else {
                    registrarse(text_usuario,text_email,text_password);
                }
            }
        });
    }

    private void registrarse(final String username, String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            userID = firebaseUser.getUid();
                            //Donde se generan los campos en la base de datos
                            Usuario usuarioRegistrandose = new Usuario();


                            String saveCurrentDate, saveCurrentTime;
                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
                            saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm A");
                            saveCurrentTime = currentTime.format(calForTime.getTime());

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
                            usuarioRegistrandose.setHora(saveCurrentTime);
                            usuarioRegistrandose.setFecha(saveCurrentDate);
                            usuarioRegistrandose.setEstado("En Linea");


                            reference.child("Usuarios").child(firebaseUser.getUid()).setValue(usuarioRegistrandose).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, R.string.cuentaCreada, Toast.LENGTH_SHORT).show();
                                        Intent intentRegister = new Intent(RegisterActivity.this,MainActivity.class);
                                        intentRegister.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intentRegister);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
    }


}
