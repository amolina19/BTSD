package com.btds.app.Modelos;


import com.btds.app.Utils.Fecha;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Mensaje {

    private String key;
    private String id;
    private String emisor;
    private String receptor;
    private String mensaje;
    private boolean leido;
    private Tipo tipoMensaje;
    public Fecha fecha;
    private LatLng ubicacion;


    //Class com.btds.app.Modelos.Mensaje does not define a no-argument constructor
    //ES NECESARIO CREAR UN BEAN CON CUN CONSTRUCTOR VACIO
    public Mensaje(){
    }

    //Imagenes y Texto, En el mensaje ira la URL de la imagen para ser cargada o el texto dependiendo del Tipo de mensaje
    public Mensaje(String id, String emisor, String receptor, String mensaje,Tipo tipoMensaje, boolean leido,Fecha fecha) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.leido = leido;
        this.tipoMensaje = tipoMensaje;
        this.fecha = fecha;
    }

    //Ubicacion
    public Mensaje(String id, String emisor, String receptor, LatLng ubicacion, boolean leido,Fecha fecha) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.ubicacion = ubicacion;
        this.leido = leido;
        this.tipoMensaje = Tipo.LOCALIZACION;
        this.fecha = fecha;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Tipo getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(Tipo tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public boolean getLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public enum Tipo{
        TEXTO,
        AUDIO,
        FOTO,
        LOCALIZACION
    }
}
