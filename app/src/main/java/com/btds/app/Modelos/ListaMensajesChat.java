package com.btds.app.Modelos;

import com.btds.app.Utils.Funciones;

public class ListaMensajesChat implements Comparable<ListaMensajesChat>{

    private Usuario usuario;
    private Mensaje mensaje;

    public ListaMensajesChat() {
    }

    public ListaMensajesChat(Usuario usuario, Mensaje mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }

    public Usuario getUsuarios() {
        return usuario;
    }

    public void setUsuarios(Usuario usuarios) {
        usuario = usuarios;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }


    @Override
    public int compareTo(ListaMensajesChat listaMensajes) {
        int segundos = Funciones.obtenerTiempoPasadosMensajes(this.getMensaje(),listaMensajes.getMensaje());

        if(segundos < 0){
            return -1;
        }else if (segundos == 0){
            return 0;
        }else{
            return 1;
        }
    }
}
