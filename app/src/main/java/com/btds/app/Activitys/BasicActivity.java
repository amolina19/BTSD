package com.btds.app.Activitys;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Alejandro Molina Louchnikov
 */

public class BasicActivity extends AppCompatActivity {

    //private int listaMensajesLeidosGlobal = 0;
    //private int listaMensajesNoLeidosGlobal = 0;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    /*
    class TaskConnectionChecker extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            while(true){

                try {
                    Thread.sleep(60000);
                    URL url = new URL("https://btsd-andstudio.firebaseio.com/");
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    System.out.println("Internet is connected");
                    return null;
                } catch (MalformedURLException e) {
                    Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());
                } catch (IOException | InterruptedException ioexc) {
                }
            }
        }
    }
    */


    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        //Funciones.setActividadEnUso(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        //Funciones.setActividadEnUso(true);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        //finish(); AQUI DA MUCHOS ERRORES
    }



    @Override
    protected void onStop() {
        super.onStop();
        //finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }




}
