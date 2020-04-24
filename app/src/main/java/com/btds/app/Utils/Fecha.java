package com.btds.app.Utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * @author Alejandro Molina Louchnikov
 */
public class Fecha {

    private String milisegundos;
    private String segundos;
    private String minutos;
    private String hora;
    private String dia;
    private String mes;
    private String anno;

    /**
     * Constructor sin parámetros, al instanciarse llamada los métodos para que devuelvan la fecha individual y se los asigna a los atributos.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Fecha(){
        this.milisegundos = this.obtenerMilisegundos();
        this.segundos = this.obtenerSegundos();
        this.minutos = this.obtenerMinutos();
        this.hora = this.obtenerHora();
        this.dia = this.obtenerDia();
        this.mes = this.obtenerMes();
        this.anno = this.obtenerAnno();
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve milisengudos en String
     */

    private String obtenerMilisegundos(){
        this.milisegundos = java.time.LocalDateTime.now().toString().substring(20,23);
        return milisegundos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve segundos en String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String obtenerSegundos(){
        this.minutos = java.time.LocalDateTime.now().toString().substring(17,19);
        return this.minutos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve minutos en String
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerMinutos(){
        this.minutos = java.time.LocalDateTime.now().toString().substring(14,16);
        return this.minutos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve la hora en String
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerHora(){
        this.hora = java.time.LocalDateTime.now().toString().substring(11,13);
        return this.hora;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el día en String
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String  obtenerDia(){
        this.dia = java.time.LocalDateTime.now().toString().substring(8,10);
        return this.dia;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el mes en String
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerMes(){
        this.mes = java.time.LocalDateTime.now().toString().substring(5,7);
        return this.mes;

    }
    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve el año en String
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerAnno(){
        //posicion = posicion-3;
        this.anno = java.time.LocalDateTime.now().toString().substring(0,4);
        return this.anno;
    }

    /**
     * Devuelve la fecha entera actual del sistema concatenada
     * @return Devuelve toda la fecha concatenada en String mediante los atributos.
     */

    @NonNull
    public String toString(){
        return this.hora+":"+this.minutos+" "+this.segundos+" Sec "+this.dia+"/"+this.mes+"/"+this.anno;
    }

    /**
     * Devuelve la fecha entera actual del sistema concatenada
     * @return Devuelve toda la fecha empezando por el año concatenada en String mediante los metodos.
     */

    public String obtenerFechaTotal(){
        return this.obtenerAnno()+""+this.obtenerMes()+""+this.obtenerDia()+""+this.obtenerHora()+""+this.obtenerMinutos()+""+this.obtenerSegundos()+""+this.obtenerMilisegundos();
    }
}
