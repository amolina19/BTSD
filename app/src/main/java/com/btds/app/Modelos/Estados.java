package com.btds.app.Modelos;

import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Estados implements Comparable<Estados>{

    public String key;
    public String estadoURL;
    public Usuario usuario;
    public Fecha fecha;

    public Estados(){
    }

    public Estados(String key,String estadoURL, Usuario usuario, Fecha fecha) {
        this.key = key;
        this.estadoURL = estadoURL;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @NotNull
    public String toString(){
        return "USUARIO "+this.usuario+" URL "+this.estadoURL+" Fecha "+fecha.toString();
    }


    public int compareTo(@NotNull Estados estado){

        if(Funciones.obtenerMinutosSubida(estado) > Funciones.obtenerMinutosSubida(this)){
            return 0;
        }else{
            return 1;
        }
    }

}