package com.btds.app.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Visibilidad implements Parcelable {

    private Boolean usuario;
    private Boolean telefono;
    private Boolean descripcion;
    private Boolean foto;
    private Boolean enLinea;

    public Visibilidad() {
    }

    public Visibilidad(Boolean usuario, Boolean telefono, Boolean descripcion, Boolean foto, Boolean enLinea) {
        this.usuario = true;
        this.telefono = true;
        this.descripcion = true;
        this.foto = true;
        this.enLinea = true;
    }

    public Boolean getUsuario() {
        return usuario;
    }

    public void setUsuario(Boolean usuario) {
        this.usuario = usuario;
    }

    public Boolean getTelefono() {
        return telefono;
    }

    public void setTelefono(Boolean telefono) {
        this.telefono = telefono;
    }

    public Boolean getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(Boolean descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getFoto() {
        return foto;
    }

    public void setFoto(Boolean foto) {
        this.foto = foto;
    }

    public Boolean getEnLinea() {
        return enLinea;
    }

    public void setEnLinea(Boolean enLinea) {
        this.enLinea = enLinea;
    }

    protected Visibilidad(Parcel in) {
        byte tmpUsuario = in.readByte();
        usuario = tmpUsuario == 0 ? null : tmpUsuario == 1;
        byte tmpTelefono = in.readByte();
        telefono = tmpTelefono == 0 ? null : tmpTelefono == 1;
        byte tmpDescripcion = in.readByte();
        descripcion = tmpDescripcion == 0 ? null : tmpDescripcion == 1;
        byte tmpFoto = in.readByte();
        foto = tmpFoto == 0 ? null : tmpFoto == 1;
        byte tmpEnLinea = in.readByte();
        enLinea = tmpEnLinea == 0 ? null : tmpEnLinea == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (usuario == null ? 0 : usuario ? 1 : 2));
        dest.writeByte((byte) (telefono == null ? 0 : telefono ? 1 : 2));
        dest.writeByte((byte) (descripcion == null ? 0 : descripcion ? 1 : 2));
        dest.writeByte((byte) (foto == null ? 0 : foto ? 1 : 2));
        dest.writeByte((byte) (enLinea == null ? 0 : enLinea ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Visibilidad> CREATOR = new Creator<Visibilidad>() {
        @Override
        public Visibilidad createFromParcel(Parcel in) {
            return new Visibilidad(in);
        }

        @Override
        public Visibilidad[] newArray(int size) {
            return new Visibilidad[size];
        }
    };
}
