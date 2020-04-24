package com.btds.app.Modelos;


/**
 * @author Alejandro Molina Louchnikov
 */

public class UsuarioBloqueado {

    private String key;
    private String usuarioAccionBloquear;
    private String usuarioBloqueado;

    public UsuarioBloqueado() {

    }

    public UsuarioBloqueado(String key, String id, String usuarioAccionBloquear, String usuarioBloqueado) {
        this.key = key;
        this.usuarioAccionBloquear = usuarioAccionBloquear;
        this.usuarioBloqueado = usuarioBloqueado;
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

    public void setUsuarioAccionBloquear(String UsuarioAccionBloquear) {
        this.usuarioAccionBloquear = UsuarioAccionBloquear;
    }

    public String getUsuarioBloqueado() {
        return usuarioBloqueado;
    }

    public void setUsuarioBloqueado(String usuarioBloqueado) {
        this.usuarioBloqueado = usuarioBloqueado;
    }
}
