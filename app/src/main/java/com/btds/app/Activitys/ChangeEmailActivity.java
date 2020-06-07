package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

/**
 * @author Alejandro Molina Louchnikov
 */

public class ChangeEmailActivity extends AppCompatActivity {


    Button aplicarCambios;
    MaterialEditText email,password;
    ProgressBar progressBar;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        aplicarCambios = findViewById(R.id.button_aplicar);
        progressBar = findViewById(R.id.progressbar);


        aplicarCambios.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if(Objects.requireNonNull(email.getText()).toString().length() == 0 || Objects.requireNonNull(password.getText()).toString().length() == 0){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.camposVacios), Toast.LENGTH_SHORT).show();
            }else{

                if(!email.getText().toString().contains("@")){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.noPareceEmail), Toast.LENGTH_SHORT).show();

                }else{
                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()),password.getText().toString());

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseUser.updateEmail(email.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.cambiadoCorreoExito), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intentPerfil = new Intent(ChangeEmailActivity.this, PerfilActivity.class);
                                    startActivity(intentPerfil);
                                    finish();
                                } else {
                                    Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.cambiadoCorreoFail), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(ChangeEmailActivity.this, getResources().getString(R.string.noSePuedeReauntenticar), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }


}