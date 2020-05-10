package com.btds.app.Modelos;

import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;

import java.util.HashMap;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Estados implements Comparable<Estados>{

    public String estadoURL;
    public Usuario usuario;
    public Fecha fecha;
    public HashMap<String,Usuario> usuariosVistos;

    public Estados(){
    }

    public Estados(String estadoURL, Usuario usuario) {
        this.estadoURL = estadoURL;
        this.usuario = usuario;
    }

    public Estados(String estadoURL, Usuario usuario, Fecha fecha) {
        this.estadoURL = estadoURL;
        this.usuario = usuario;
        this.fecha = fecha;
    }


    public String getEstadoURL() {
        return this.estadoURL;
    }

    public void setEstadoURL(String estadoURL) {
        this.estadoURL = estadoURL;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Fecha getFecha() {
        return this.fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public String toString(){
        return "USUARIO "+this.usuario+" URL "+this.estadoURL+" Fecha "+fecha.toString();
    }

    public HashMap<String, Usuario> getUsuariosVistos() {
        return usuariosVistos;
    }

    public void setUsuariosVistos(HashMap<String, Usuario> usuariosVistos) {
        this.usuariosVistos = usuariosVistos;
    }

    public int compareTo(Estados estado){

        if(Funciones.obtenerMinutosSubida(estado) > Funciones.obtenerMinutosSubida(this)){
            return 0;
        }else{
            return 1;
        }
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