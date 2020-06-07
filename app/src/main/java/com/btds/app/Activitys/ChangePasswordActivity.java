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

public class ChangePasswordActivity extends AppCompatActivity {


    MaterialEditText password, repeatpassword, oldpassword;
    ProgressBar progressBar;
    Button aplicar_button;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldpassword = findViewById(R.id.oldpassword);
        password = findViewById(R.id.password);
        repeatpassword = findViewById(R.id.repeatpassword);
        aplicar_button = findViewById(R.id.button_aplicar);
        progressBar = findViewById(R.id.progressbar);
        //String userEmail = firebaseUser.getEmail();
        //AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.,oldpass);

        aplicar_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(repeatpassword.getText()).toString().isEmpty()) {
                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.camposVacios), Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(password.getText()).toString().isEmpty()) {
                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.camposVacios), Toast.LENGTH_SHORT).show();
            } else {
                if (!password.getText().toString().contentEquals(repeatpassword.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.contrasenaNoCoincide), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {

                    if (password.getText().toString().length() < 8) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.contraseñaPequeña), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), Objects.requireNonNull(oldpassword.getText()).toString());
                        firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(password.getText().toString()).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.contrasenaCambidaExitosa), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        Intent intentPerfil = new Intent(ChangePasswordActivity.this, PerfilActivity.class);
                                        startActivity(intentPerfil);
                                        finish();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.contrasenaCambiadaFail), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.noSePuedeReauntenticar), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        });
    }
}