package com.btds.app.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.btds.app.Utils.Fecha;

import java.util.List;

public class Llamada  implements Parcelable {

    public String idLlamada;
    public Usuario usuarioOrigen;
    public Usuario usuarioDestino;
    public boolean aceptada;
    public boolean finalizado;
    public boolean videollamada;
    public Fecha fecha;
    public List<Integer> tiempoTranscurrido ;

    public Llamada(){
    }

    public Llamada(String idLlamada, Usuario usuarioOrigen,Usuario usuarioDestino,Boolean videollamada){
        this.idLlamada = idLlamada;
        this.usuarioOrigen = usuarioOrigen;
        this.usuarioDestino = usuarioDestino;
        this.videollamada = videollamada;
        this.finalizado = false;
        this.aceptada = false;
        this.fecha = new Fecha();
    }

    public String getIdLlamada() {
        return idLlamada;
    }

    public void setIdLlamada(String idLlamada) {
        this.idLlamada = idLlamada;
    }

    public Usuario getUsuarioOrigen() {
        return usuarioOrigen;
    }

    public void setUsuarioOrigen(Usuario usuarioOrigen) {
        this.usuarioOrigen = usuarioOrigen;
    }

    public Usuario getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(Usuario usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public void setAceptada(boolean aceptada) {
        this.aceptada = aceptada;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public boolean isVideollamada() {
        return videollamada;
    }

    public void setVideollamada(boolean videollamada) {
        this.videollamada = videollamada;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public List<Integer> getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public void setTiempoTranscurrido(List<Integer> tiempoTranscurrido) {
        this.tiempoTranscurrido = tiempoTranscurrido;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idLlamada);
        dest.writeParcelable(usuarioOrigen, flags);
        dest.writeParcelable(usuarioDestino, flags);
        dest.writeByte((byte) (aceptada ? 1 : 0));
        dest.writeByte((byte) (finalizado ? 1 : 0));
        dest.writeByte((byte) (videollamada ? 1 : 0));
        dest.writeParcelable(fecha, flags);
    }

    protected Llamada(Parcel in) {
        idLlamada = in.readString();
        usuarioOrigen = in.readParcelable(Usuario.class.getClassLoader());
        usuarioDestino = in.readParcelable(Usuario.class.getClassLoader());
        aceptada = in.readByte() != 0;
        finalizado = in.readByte() != 0;
        videollamada = in.readByte() != 0;
        fecha = in.readParcelable(Fecha.class.getClassLoader());
    }

    public static final Creator<Llamada> CREATOR = new Creator<Llamada>() {
        @Override
        public Llamada createFromParcel(Parcel in) {
            return new Llamada(in);
        }

        @Override
        public Llamada[] newArray(int size) {
            return new Llamada[size];
        }
    };
}
