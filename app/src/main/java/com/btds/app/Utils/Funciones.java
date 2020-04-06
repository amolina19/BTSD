package com.btds.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class Funciones extends Activity {
    private static Usuario usuario;
    private static Boolean actividadEnUso = false;
    private static  Boolean backPressed = false;
    private static HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    static Fecha fecha;
    private static DatabaseReference referenceUserDataBase;

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
    public static void actualizarConexion(@NonNull final String estado, final FirebaseUser firebaseUser, final Context contexto) {

        fecha = new Fecha();
        usuario = new Usuario();

        final DatabaseReference referenceUserDataBase = getInstance().getReference("Usuarios");

        //addListenerForSingleValueEvent() me ha solucionado un problema grandisimo
        //Antes utilizaba el addValueEventListener() y al salir de la aplicacion aunque estuviese cerrada, la base de datos entraba en bucles sobrescibiendo los valores de Linea a Desconectado sin parar.
        referenceUserDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        if(firebaseUser != null){
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

    public static String getAlphaNumericString(int n){


        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static void desbloquearUsuario(Usuario usuarioChat){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        refernceBloquedUsers.child(firebaseUser.getUid()+""+usuarioChat.getId()).removeValue();
    }

    public static void bloquearUsuario(Usuario usuarioChat){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        UsuarioBloqueado usuarioBloqueadoObject = new UsuarioBloqueado();

        usuarioBloqueadoObject.setKey(firebaseUser.getUid()+""+usuarioChat.getId());
        usuarioBloqueadoObject.setUsuarioAccionBloquear(firebaseUser.getUid());
        usuarioBloqueadoObject.setUsuarioBloqueado(usuarioChat.getId());

        refernceBloquedUsers.child(usuarioBloqueadoObject.getKey()).setValue(usuarioBloqueadoObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.println("Se ha bloqueado al usuario");
                }
            }
        });

    }

    public static HashMap<String,UsuarioBloqueado> obtenerUsuariosBloqueados(){

        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        listaUsuariosBloqueados = new HashMap<String, UsuarioBloqueado>();

        refernceBloquedUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuariosBloqueados.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UsuarioBloqueado usuarioBloqueado = snapshot.getValue(UsuarioBloqueado.class);
                    if(usuarioBloqueado != null){
                        listaUsuariosBloqueados.put(usuarioBloqueado.getUsuarioBloqueado(),usuarioBloqueado);

                        for(Map.Entry<String, UsuarioBloqueado> entry : listaUsuariosBloqueados.entrySet()) {
                            System.out.println(entry.getKey());
                            UsuarioBloqueado value = entry.getValue();
                            System.out.println(value.getUsuarioBloqueado());

                            // do what you have to do here
                            // In your case, another loop.
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listaUsuariosBloqueados;
    }

    public static HashMap<String,UsuarioBloqueado> getListaUsuariosBloqueados(){
        return listaUsuariosBloqueados;
    }

 /*
    public static void escribiendo(final FirebaseUser firebaseUser, final Context contexto){
        final DatabaseReference referenceUserDataBase = getInstance().getReference("Usuarios");

        referenceUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Usuario usuarioObj = snapshot.getValue(Usuario.class);
                        if (usuarioObj != null) {

                            if (firebaseUser.getUid().contentEquals(usuarioObj.getId())) {
                                usuarioObj.setEscribiendo(contexto.getResources().getString(R.string.escribiendo));
                                referenceUserDataBase.child(firebaseUser.getUid()).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    */


    public ArrayList<String> ObtenerListaUsuariosStringID(String firebaseID){
       final  ArrayList<String> arrayList = new ArrayList<>();

       DatabaseReference referenceUserDataBase = getInstance().getReference("Usuarios");

        referenceUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Usuario usuarioObj = snapshot.getValue(Usuario.class);
                    if(usuarioObj != null){
                        arrayList.add(usuarioObj.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return arrayList;
    }


}
