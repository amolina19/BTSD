package com.btds.app.Activitys;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Alejandro Molina Louchnikov
 */

public class BasicActivity extends AppCompatActivity {

    //private int listaMensajesLeidosGlobal = 0;
    //private int listaMensajesNoLeidosGlobal = 0;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser != null){
            //Funciones.habilitarPersistencia();
            //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
           // Funciones.actualizarConexion(getApplicationContext().getResources().getString(R.string.online),firebaseUser);
        }
        //Funciones.setActividadEnUso(true);
    }

}
