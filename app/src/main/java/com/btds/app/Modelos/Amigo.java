package com.btds.app.Modelos;

import java.util.HashMap;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Amigo {

    private HashMap<String,String> amigosFirebaseID;

    public Amigo(){

    }

    public Amigo(HashMap<String, String> amigosFirebaseID) {
        this.amigosFirebaseID = amigosFirebaseID;
    }

    public HashMap<String, String> getAmigosFirebaseID() {
        return amigosFirebaseID;
    }

    public void setAmigosFirebaseID(HashMap<String, String> amigosFirebaseID) {
        this.amigosFirebaseID = amigosFirebaseID;
    }
}
