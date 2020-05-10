package com.btds.app.Modelos;


/**
 * @author Alejandro Molina Louchnikov
 */

public class UsuarioSilenciado {

    private String key;
    private String usuarioAccionSilenciar;
    private String usuarioSilenciado;

    public UsuarioSilenciado() {

    }

    public UsuarioSilenciado(String key, String id, String usuarioAccionSilenciar, String usuarioSilenciado) {
        this.key = key;
        this.usuarioAccionSilenciar = usuarioAccionSilenciar;
        this.usuarioSilenciado = usuarioSilenciado;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsuarioAccionSilenciar() {
        return usuarioAccionSilenciar;
    }

    public void setUsuarioAccionSilenciar(String usuarioAccionSilenciar) {
        this.usuarioAccionSilenciar = usuarioAccionSilenciar;
    }

    public String getUsuarioSilenciado() {
        return usuarioSilenciado;
    }

    public void setUsuarioSilenciado(String usuarioSilenciado) {
        this.usuarioSilenciado = usuarioSilenciado;
    }
}
