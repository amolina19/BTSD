package com.btds.app.Utils;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * @author Alejandro Molina Louchnikov
 */
public class Fecha implements Parcelable {

    public String milisegundos;
    public String segundos;
    public String minutos;
    public String hora;
    public String dia;
    public String mes;
    public String anno;

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

    public int getMilisegundosInteger() {
        return Integer.parseInt(milisegundos);
    }

    public void setMilisegundosInteger(int milisegundos) {
        this.milisegundos = String.valueOf(milisegundos);
    }

    public int getSegundosInteger() {
        return Integer.parseInt(segundos);
    }

    public void setSegundosInteger(int segundos) {
        this.segundos = String.valueOf(segundos);
    }

    public int getMinutosInteger() {
        return Integer.parseInt(minutos);
    }

    public void setMinutosInteger(int minutos) {
        this.minutos = String.valueOf(minutos);
    }

    public int getHoraInteger() {
        return Integer.parseInt(hora);
    }

    public void setHoraInteger(int hora) {
        this.hora = String.valueOf(hora);
    }

    public int getDiaInteger() {
        return Integer.parseInt(dia);
    }

    public void setDiaInteger(int dia) {
        this.dia = String.valueOf(dia);
    }

    public int getMesInteger() {
        return Integer.parseInt(mes);
    }

    public void setMesInteger(int mes) {
        this.mes = String.valueOf(mes);
    }

    public int getAnnoInteger() {
        return Integer.parseInt(anno);
    }

    public void setAnnoInteger(int anno) {
        this.anno = String.valueOf(anno);
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve milisengudos en String
     */

    private String obtenerMilisegundos(){

            String fecha = java.time.LocalDateTime.now().toString();
            int longitudPunto = fecha.lastIndexOf(".");
            try{
                this.milisegundos = java.time.LocalDateTime.now().toString().substring((longitudPunto+1),fecha.length());
            }catch (StringIndexOutOfBoundsException sioobe){
                this.milisegundos = "000";
            }

        return milisegundos;
    }

    /**
     * Devuelve la fecha actual del sistema concatenada
     * @return Devuelve segundos en String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String obtenerSegundos(){
        try{
            this.minutos = java.time.LocalDateTime.now().toString().substring(17,19);
        }catch (StringIndexOutOfBoundsException sioobe){
            this.segundos = "00";
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(milisegundos);
        dest.writeString(segundos);
        dest.writeString(minutos);
        dest.writeString(hora);
        dest.writeString(dia);
        dest.writeString(mes);
        dest.writeString(anno);
    }

    protected Fecha(Parcel in) {
        milisegundos = in.readString();
        segundos = in.readString();
        minutos = in.readString();
        hora = in.readString();
        dia = in.readString();
        mes = in.readString();
        anno = in.readString();
    }

    public static final Creator<Fecha> CREATOR = new Creator<Fecha>() {
        @Override
        public Fecha createFromParcel(Parcel in) {
            return new Fecha(in);
        }

        @Override
        public Fecha[] newArray(int size) {
            return new Fecha[size];
        }
    };
}
