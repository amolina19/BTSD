package com.btds.app.Modelos;

public class Audio {

    String pathLocal;
    String URLfirebase;

    public Audio() {

    }

    public Audio(String pathLocal,String URLfirebase ){
        this.pathLocal = pathLocal;
        this.URLfirebase = URLfirebase;
    }

    public String getPathLocal() {
        return pathLocal;
    }

    public void setPathLocal(String pathLocal) {
        this.pathLocal = pathLocal;
    }

    public String getURLirebase() {
        return URLfirebase;
    }

    public void setURLirebase(String URLirebase) {
        this.URLfirebase = URLirebase;
    }
}
