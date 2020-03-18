package com.btds.app.Modelos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario {

    private String id;
    private String usuario;
    private String imagenURL;
    private String estado;
    private String hora;
    private String fecha;
    //private List<Usuario> amigos;
    //private List<Usuario> bloqueados;
    //private List<Usuario> peticionesAmistad;



    public Usuario(){
    }

    public Usuario(String id, String usuario, String imagenURL) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
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

    /*

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public List<Usuario> getBloqueados() {
        return bloqueados;
    }

    public void setBloqueados(List<Usuario> bloqueados) {
        this.bloqueados = bloqueados;
    }

    public List<Usuario> getPeticionesAmistad() {
        return peticionesAmistad;
    }

    public void setPeticionesAmistad(List<Usuario> peticionesAmistad) {
        this.peticionesAmistad = peticionesAmistad;
    }

    */

}
