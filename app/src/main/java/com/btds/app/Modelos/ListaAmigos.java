package com.btds.app.Modelos;


/**
 * @author Alejandro Molina Louchnikov
 */

public class ListaAmigos {

    private String key;
    private String usuarioAccionBloquear;
    private String usuarioEnviadoPeticion;

    public ListaAmigos() {

    }

    public ListaAmigos(String key, String id, String usuarioAccionBloquear, String usuarioBloqueado) {
        this.key = key;
        this.usuarioAccionBloquear = usuarioAccionBloquear;
        this.usuarioEnviadoPeticion = usuarioBloqueado;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsuarioAccionBloquear() {
        return usuarioAccionBloquear;
    }

    public void setUsuarioAccionBloquear(String usuarioAccionBloquear) {
        this.usuarioAccionBloquear = usuarioAccionBloquear;
    }

    public String getUsuarioEnviadoPeticion() {
        return usuarioEnviadoPeticion;
    }

    public void setUsuarioEnviadoPeticion(String usuarioEnviadoPeticion) {
        this.usuarioEnviadoPeticion = usuarioEnviadoPeticion;
    }
}
