package com.btds.app.Modelos;


/**
 * @author Alejandro Molina Louchnikov
 */

public class Mensaje {

    private String key;
    private String id;
    private String emisor;
    private String receptor;
    private String mensaje;
    private String anno;
    private String mes;
    private String dia;
    private String hora;
    private String fecha;
    private String leido;


    //Class com.btds.app.Modelos.Mensaje does not define a no-argument constructor
    //ES NECESARIO CREAR UN BEAN CON CUN CONSTRUCTOR VACIO
    public Mensaje(){
    }

    public Mensaje(String id, String emisor, String receptor, String mensaje, String anno, String mes, String dia, String hora, String fecha,String leido) {
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.anno = anno;
        this.mes = mes;
        this.dia = dia;
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

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
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
