package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class RecoverActivity extends AppCompatActivity {

    MaterialEditText email;
    ProgressBar progressBar;
    Button recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        email = findViewById(R.id.usuarioemail);
        progressBar = findViewById(R.id.progressbar);
        recuperar = findViewById(R.id.button_recuperar);

        recuperar.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if(Objects.requireNonNull(email.getText()).toString().length() == 0){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, getResources().getString(R.string.campoVacio), Toast.LENGTH_SHORT).show();
            }else{
                if(!email.getText().toString().contains("@")){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, getResources().getString(R.string.noPareceEmail), Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("DEBUG RecoverActivity", "Email enviado");
                                    Intent intentLogin = new Intent(RecoverActivity.this,LoginActivity.class);
                                    startActivity(intentLogin);
                                    Toast.makeText(RecoverActivity.this, getResources().getString(R.string.emailEnviado), Toast.LENGTH_SHORT).show();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RecoverActivity.this, getResources().getString(R.string.emailEnviadoFail), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}