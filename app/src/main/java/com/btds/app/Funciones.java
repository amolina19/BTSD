package com.btds.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.btds.app.Modelos.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Funciones extends Activity {
    private static Usuario usuario;
    private static Boolean actividadEnUso = false;
    private static  Boolean backPressed = false;
    static Fecha fecha;

    public Funciones(){

    }

    public static Boolean getActividadEnUso() {
        return actividadEnUso;
    }

    public static void setActividadEnUso(Boolean actividadEnUso) {
        Funciones.actividadEnUso = actividadEnUso;
    }

    public static Boolean getBackPressed() {
        return backPressed;
    }

    public static void setBackPressed(Boolean backPressed) {
        Funciones.backPressed = backPressed;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void actualizarConexion(@NonNull final String estado, final FirebaseUser firebaseUser, final DatabaseReference referenceUserDataBase, final Context contexto) {

        fecha = new Fecha();
        usuario = new Usuario();

        Map currentTimeMap = new HashMap<>();
        currentTimeMap.put("hora", fecha.obtenerHora()+":"+fecha.obtenerMinutos());
        currentTimeMap.put("fecha", fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAño());
        currentTimeMap.put("estado", estado);

        //addListenerForSingleValueEvent() me ha solucionado un problema grandisimo
        //Antes utilizaba el addValueEventListener() y al salir de la aplicacion aunque estuviese cerrada, la base de datos entraba en bucles sobrescibiendo los valores de Linea a Desconectado sin parar.
        referenceUserDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        if (usuario.getId().equals(firebaseUser.getUid())) {
                            usuario.setEstado(estado);
                            usuario.setHora(fecha.obtenerHora()+":"+fecha.obtenerMinutos());
                            usuario.setFecha(fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAño());
                            referenceUserDataBase.child(firebaseUser.getUid()).setValue(usuario);
                            //System.out.println("CONEXION: "+estado);
                            //System.out.println("RECURSO "+contexto.getResources().getString(R.string.offline));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static float obtenerDiasPasados(Usuario usuarioChat){

        fecha = new Fecha();
        String fechaUsuario = usuarioChat.getFecha();

        int userChatDateDay =  Integer.valueOf(fechaUsuario.replace(" ","").substring(0,2));
        int userChatDateMonth = Integer.valueOf(fechaUsuario.replace(" ","").substring(2,4));
        int userChatDateYear = Integer.valueOf(fechaUsuario.replace(" ","").substring(4,8));

        LocalDate dateBefore = LocalDate.of(userChatDateYear, userChatDateMonth, userChatDateDay);
        LocalDate dateAfter = LocalDate.of(Integer.valueOf(fecha.obtenerAño()), Integer.valueOf(fecha.obtenerMes()), Integer.valueOf(fecha.obtenerDia()));
        long nDias = ChronoUnit.DAYS.between(dateBefore, dateAfter);

        System.out.println("Dias transcurridos ult Conexion Usuario: "+nDias);
        return nDias;
    }


}
