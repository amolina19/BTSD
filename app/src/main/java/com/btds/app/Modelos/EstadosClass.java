package com.btds.app.Modelos;

public class EstadosClass {

    private String estadoURL;
    private String usuario;
    //private Usuario usuarioEstado;

    public EstadosClass() {
    }

    public EstadosClass(String estadoURL, String usuario) {
        this.estadoURL = estadoURL;
        this.usuario = usuario;
    }

    public String getestadoURL() {
        return estadoURL;
    }

    public void setImagenesURL(String estadoURL) {
        this.estadoURL = estadoURL;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /*
    public Usuario getUsuarioEstado() {
        return usuarioEstado;
    }

    public void setUsuarioEstado(Usuario usuarioEstado) {
        this.usuarioEstado = usuarioEstado;
    }
     */



}
