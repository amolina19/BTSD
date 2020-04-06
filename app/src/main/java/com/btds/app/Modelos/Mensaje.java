package com.btds.app.Modelos;

public class Mensaje {

    private String key;
    private String id;
    private String emisor;
    private String receptor;
    private String mensaje;
    private String hora;
    private String fecha;
    private String leido;


    //Class com.btds.app.Modelos.Mensaje does not define a no-argument constructor
    //ES NECESARIO CREAR UN BEAN CON CUN CONSTRUCTOR VACIO
    public Mensaje(){
    }

    public Mensaje(String id, String emisor, String receptor, String mensaje, String hora, String fecha,String leido) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.hora = hora;
        this.fecha = fecha;
        this.leido = leido;
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

    public String getLeido() {
        return leido;
    }

    public void setLeido(String leido) {
        this.leido = leido;
    }
}
