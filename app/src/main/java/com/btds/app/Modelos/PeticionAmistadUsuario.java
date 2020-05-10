package com.btds.app.Modelos;


/**
 * @author Alejandro Molina Louchnikov
 */

public class PeticionAmistadUsuario {

    private String key;
    private String usuarioAccionPeticion;
    private String usuarioEnviadoPeticion;

    public PeticionAmistadUsuario() {

    }

    public PeticionAmistadUsuario(String key, String usuarioAccionPeticion, String usuarioEnviadoPeticion) {
        this.key = key;
        this.usuarioAccionPeticion = usuarioAccionPeticion;
        this.usuarioEnviadoPeticion = usuarioEnviadoPeticion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsuarioAccionPeticion() {
        return usuarioAccionPeticion;
    }

    public void setUsuarioAccionPeticion(String usuarioAccionPeticion) {
        this.usuarioAccionPeticion = usuarioAccionPeticion;
    }

    public String getUsuarioEnviadoPeticion() {
        return usuarioEnviadoPeticion;
    }

    public void setUsuarioEnviadoPeticion(String usuarioEnviadoPeticion) {
        this.usuarioEnviadoPeticion = usuarioEnviadoPeticion;
    }
}
