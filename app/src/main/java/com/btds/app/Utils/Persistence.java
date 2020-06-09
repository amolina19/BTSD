package com.btds.app.Utils;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Persistence extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        Funciones.persistencia(true);
    }

}
