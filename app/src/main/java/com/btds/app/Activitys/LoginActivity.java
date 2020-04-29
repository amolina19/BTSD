package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vdx.designertoast.DesignerToast;

import java.util.Objects;

/**
 * @author Alejandro Molina Louchnikov
 */

public class LoginActivity extends BasicActivity {


    /*
    class TaskProgressBar extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            for(int i=0;i<100;i++){
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


    MaterialEditText email,password;
    Button button_iniciarSesion;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    //private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //DEPRECATED API 23 getResources.getColor()
        toolbar.setTitleTextColor(this.getColor(R.color.colorBlanco));


        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_iniciarSesion = findViewById(R.id.button_iniciarSesion);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        button_iniciarSesion.setOnClickListener(v -> {
            String text_email = Objects.requireNonNull(email.getText()).toString();
            String text_password = Objects.requireNonNull(password.getText()).toString();

            //new TaskProgressBar().execute();

            if(text_email.isEmpty() || text_password.isEmpty()){
                Toast.makeText(LoginActivity.this, R.string.completar, Toast.LENGTH_SHORT).show();
            }else{
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(text_email,text_password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentLogin);
                                //Toast.makeText(LoginActivity.this, R.string.iniciarSesionCorrectamente, Toast.LENGTH_SHORT).show();
                                DesignerToast.Success(LoginActivity.this, getResources().getString(R.string.iniciarSesionCorrectamente), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                                finish();
                            }else{
                                //Toast.makeText(LoginActivity.this, R.string.autentificacionFallida, Toast.LENGTH_SHORT).show();
                                DesignerToast.Error(LoginActivity.this, getResources().getString(R.string.autentificacionFallida), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                            }
                        });
            }
        });
    }
}
