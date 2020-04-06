package com.btds.app.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class Fecha {

    private String milisegundos;
    private String segundos;
    private String minutos;
    private String hora;
    private String dia;
    private String mes;
    private String año;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Fecha(){
        this.milisegundos = this.obtenerMilisegundos();
        this.segundos = this.obtenerSegundos();
        this.minutos = this.obtenerMinutos();
        this.hora = this.obtenerHora();
        this.dia = this.obtenerDia();
        this.mes = this.obtenerMes();
        this.año = this.obtenerAño();
    }

    public String obtenerMilisegundos(){
        int posicion = java.time.LocalDateTime.now().toString().lastIndexOf(".");
        int longitud = java.time.LocalDateTime.now().toString().length();
        this.milisegundos = java.time.LocalDateTime.now().toString().substring(posicion+1,longitud);
        return milisegundos;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerSegundos(){
        int posicion = java.time.LocalDateTime.now().toString().lastIndexOf(":");
        posicion++;
        this.minutos = java.time.LocalDateTime.now().toString().substring(posicion,posicion+2);
        return this.minutos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerMinutos(){
        int posicion = java.time.LocalDateTime.now().toString().lastIndexOf(":");
        posicion = posicion-2;
        this.minutos = java.time.LocalDateTime.now().toString().substring(posicion,posicion+2);
        return this.minutos;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerHora(){
        int posicion = java.time.LocalDateTime.now().toString().lastIndexOf(":");
        posicion = posicion-5;
        this.hora = java.time.LocalDateTime.now().toString().substring(posicion,posicion+2);
        return this.hora;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String  obtenerDia(){
        this.dia = java.time.LocalDateTime.now().toString().substring(8,10);
        return this.dia;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerMes(){
        this.mes = java.time.LocalDateTime.now().toString().substring(5,7);
        return this.mes;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String obtenerAño(){
        //posicion = posicion-3;
        this.año = java.time.LocalDateTime.now().toString().substring(0,4);
        return this.año;
    }

    public String toString(){
        return this.hora+":"+this.minutos+" "+this.segundos+" Sec "+this.dia+"/"+this.mes+"/"+this.año;
    }

    public String obtenerFechaTotal(){
        return this.obtenerAño()+""+this.obtenerMes()+""+this.obtenerDia()+""+this.obtenerHora()+""+this.obtenerMinutos()+""+this.obtenerSegundos()+""+this.obtenerMilisegundos();
    }
}
