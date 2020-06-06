package com.btds.app.Utils;

import android.util.Log;

public class Persistence extends android.app.Application  {



    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        Funciones.persistencia(true);
        Log.d("DEBUG Persistencia", "Activada");
    }

}
