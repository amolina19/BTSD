package com.btds.app.Modelos;


import android.os.Parcel;
import android.os.Parcelable;

import com.btds.app.Utils.Fecha;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Mensaje implements Parcelable {

    private String key, id, emisor, receptor, mensaje;
    private boolean leido;
    private Tipo tipoMensaje;
    public Fecha fecha;
    private LatLng ubicacion;
    private Audio audio;


    //Class com.btds.app.Modelos.Mensaje does not define a no-argument constructor
    //ES NECESARIO CREAR UN BEAN CON CUN CONSTRUCTOR VACIO
    public Mensaje(){
    }

    //Imagenes y Texto, En el mensaje ira la URL de la imagen para ser cargada o el texto dependiendo del Tipo de mensaje
    public Mensaje(String id, String emisor, String receptor, String mensaje,Tipo tipoMensaje, boolean leido,Fecha fecha) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.leido = leido;
        this.tipoMensaje = tipoMensaje;
        this.fecha = fecha;
    }

    //Ubicacion
    public Mensaje(String id, String emisor, String receptor, LatLng ubicacion, boolean leido,Fecha fecha) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.ubicacion = ubicacion;
        this.leido = leido;
        this.tipoMensaje = Tipo.LOCALIZACION;
        this.fecha = fecha;
    }

    //Audio
    public Mensaje(String id, String emisor, String receptor, Audio audio, boolean leido,Fecha fecha) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.audio = audio;
        this.leido = leido;
        this.tipoMensaje = Tipo.AUDIO;
        this.fecha = fecha;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Tipo getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(Tipo tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public boolean getLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(id);
        dest.writeString(emisor);
        dest.writeString(receptor);
        dest.writeString(mensaje);
        dest.writeByte((byte) (leido ? 1 : 0));
        dest.writeParcelable(ubicacion, flags);
    }


    protected Mensaje(Parcel in) {
        key = in.readString();
        id = in.readString();
        emisor = in.readString();
        receptor = in.readString();
        mensaje = in.readString();
        leido = in.readByte() != 0;
        ubicacion = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Mensaje> CREATOR = new Creator<Mensaje>() {
        @Override
        public Mensaje createFromParcel(Parcel in) {
            return new Mensaje(in);
        }

        @Override
        public Mensaje[] newArray(int size) {
            return new Mensaje[size];
        }
    };


    public enum Tipo{
        TEXTO,
        AUDIO,
        FOTO,
        LOCALIZACION
    }

}
