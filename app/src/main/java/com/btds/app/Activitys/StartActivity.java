package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Alejandro Molina Louchnikov
 */

public class StartActivity extends BasicActivity {

    Button login, registrarse;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("DEBUG ","OnStartActivity Created");
        //Comprobar si hay un usuario logeado
        if(firebaseUser != null){
            Intent intentStart = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intentStart);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);



        login = findViewById(R.id.login);
        registrarse = findViewById(R.id.registrarse);

        login.setOnClickListener(v -> startActivity(new Intent(StartActivity.this,LoginActivity.class)));

        registrarse.setOnClickListener(v -> startActivity(new Intent(StartActivity.this,RegisterActivity.class)));
    }
}