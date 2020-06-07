package com.btds.app.Modelos;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Usuario implements Parcelable {

    private String id, usuario, imagenURL, estado, hora, fecha, descripcion, telefono;
    private Boolean twoAunthenticatorFactor, phoneVerificationOmited, T2Aintroduced;
    private Visibilidad visibilidad;

    public Usuario(){
    }

    public Usuario(String id, String usuario, String imagenURL) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
        this.T2Aintroduced = false;
        this.visibilidad = new Visibilidad(true,true,true,true,true);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getTwoAunthenticatorFactor() {
        return twoAunthenticatorFactor;
    }

    public void setTwoAunthenticatorFactor(Boolean twoAunthenticatorFactor) {
        this.twoAunthenticatorFactor = twoAunthenticatorFactor;
    }

    public Boolean getT2Aintroduced() {
        return T2Aintroduced;
    }

    public void setT2Aintroduced(Boolean t2Aintroduced) {
        T2Aintroduced = t2Aintroduced;
    }

    public Boolean getPhoneVerificationOmited() {
        return phoneVerificationOmited;
    }

    public void setPhoneVerificationOmited(Boolean phoneVerificationOmited) {
        this.phoneVerificationOmited = phoneVerificationOmited;
    }

    public Visibilidad getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(Visibilidad visibilidad) {
        this.visibilidad = visibilidad;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(usuario);
        dest.writeString(imagenURL);
        dest.writeString(estado);
        dest.writeString(hora);
        dest.writeString(fecha);
        dest.writeString(descripcion);
        dest.writeString(telefono);
        dest.writeByte((byte) (twoAunthenticatorFactor == null ? 0 : twoAunthenticatorFactor ? 1 : 2));
        dest.writeByte((byte) (phoneVerificationOmited == null ? 0 : phoneVerificationOmited ? 1 : 2));
        dest.writeByte((byte) (T2Aintroduced == null ? 0 : T2Aintroduced ? 1 : 2));
        dest.writeParcelable(visibilidad, flags);
    }

    protected Usuario(Parcel in) {
        id = in.readString();
        usuario = in.readString();
        imagenURL = in.readString();
        estado = in.readString();
        hora = in.readString();
        fecha = in.readString();
        descripcion = in.readString();
        telefono = in.readString();
        byte tmpTwoAunthenticatorFactor = in.readByte();
        twoAunthenticatorFactor = tmpTwoAunthenticatorFactor == 0 ? null : tmpTwoAunthenticatorFactor == 1;
        byte tmpPhoneVerificationOmited = in.readByte();
        phoneVerificationOmited = tmpPhoneVerificationOmited == 0 ? null : tmpPhoneVerificationOmited == 1;
        byte tmpT2Aintroduced = in.readByte();
        T2Aintroduced = tmpT2Aintroduced == 0 ? null : tmpT2Aintroduced == 1;
        visibilidad = in.readParcelable(Visibilidad.class.getClassLoader());
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };
}
