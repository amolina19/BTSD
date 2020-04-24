/**
 * @author Alejandro Molina Louchnikov
 */

package com.btds.app.Utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Funciones {
    private static Usuario usuario;
    private static HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private static Fecha fecha;

    public Funciones(){

    }


    /**
     * Función estática para actualizar la última conexión y el estado del usuario. Recorro la base de datos hasta encontrar la referencia de nuestro usuario y asignarselo a un objeto, asignamos los datos y actualizamos.
     * Recorro la base de datos de todos lso objetos ya que no estoy creado ni borrando un valor, sino actualizandolo.
     * @param estado Se le inserta un String, en este caso valores de strings en values/strings.xml
     * @param firebaseUser El Objeto FirebaseUser para obtener el id del usuario local.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void actualizarConexion(@NonNull final String estado, final FirebaseUser firebaseUser) {

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
                                usuario.setFecha(fecha.obtenerDia()+" "+fecha.obtenerMes()+" "+fecha.obtenerAnno());
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

    /**
     * Se obtiene su últ conexión y cálcular cuantos días han transcurrido devolviendo un número entero.
     * @param usuarioChat objeto Usuario del usuario que estamos chateando
     * @return devuelve el número de dias en un int.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static float obtenerDiasPasados(Usuario usuarioChat){

        fecha = new Fecha();
        String fechaUsuario = usuarioChat.getFecha();

        int userChatDateDay =  Integer.parseInt(fechaUsuario.replace(" ","").substring(0,2));
        int userChatDateMonth = Integer.parseInt(fechaUsuario.replace(" ","").substring(2,4));
        int userChatDateYear = Integer.parseInt(fechaUsuario.replace(" ","").substring(4,8));

        LocalDate dateBefore = LocalDate.of(userChatDateYear, userChatDateMonth, userChatDateDay);
        LocalDate dateAfter = LocalDate.of(Integer.parseInt(fecha.obtenerAnno()), Integer.parseInt(fecha.obtenerMes()), Integer.parseInt(fecha.obtenerDia()));
        long nDias = ChronoUnit.DAYS.between(dateBefore, dateAfter);

        Log.d("Debugging Dias transcurridos","Dias transcurridos ult Conexion Usuario: "+nDias);
        return nDias;
    }

    /**
     *
     * @param n Parámetro la función que le insertamos para que nos devuelva una cadena aleatoria de String con esa longitud.
     * @return Devuelve la cadena aleatoria de String generada.
     */

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

    /**
     * Parámetro donde se le introduce un objeto Usuario dentro de la función a través de cualquier actividad que se use para borrarlo de la lista de bloqueados del usuario.
     * @param usuarioChat Objeto Usuario con el que estamos teniendo la conversación.
     */

    public static void desbloquearUsuario(Usuario usuarioChat){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        assert firebaseUser != null;
        refernceBloquedUsers.child(firebaseUser.getUid()+""+usuarioChat.getId()).removeValue();

        getListaUsuariosBloqueados().remove(usuarioChat.getId());
    }

    /**
     * Parámetro donde se le introduce un objeto Usuario dentro de la función a través de cualquier actividad que se use para añadirlo a la lista de bloqueados del usuario.
     * Al bloquear el usuario se útiliza el ID del usuario que bloquea + ID del usuario bloqueado para que no exista duplicidad.
     * @param usuarioChat objeto Usuario con el que estamos teniendo la conversación.
     */

    public static void bloquearUsuario(Usuario usuarioChat){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        UsuarioBloqueado usuarioBloqueadoObject = new UsuarioBloqueado();

        assert firebaseUser != null;
        usuarioBloqueadoObject.setKey(firebaseUser.getUid()+""+usuarioChat.getId());
        usuarioBloqueadoObject.setUsuarioAccionBloquear(firebaseUser.getUid());
        usuarioBloqueadoObject.setUsuarioBloqueado(usuarioChat.getId());

        refernceBloquedUsers.child(usuarioBloqueadoObject.getKey()).setValue(usuarioBloqueadoObject).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("Debugging","Se ha bloqueado al usuario");
            }
        });

    }

    /**
     * Devuelve un HashMap donde el String es el id del usuario y su objeto Usuario para que sean únicos. Cada vez que se introduce o elimine un registro devolverá una lista nueva.
     * @return devuelve el HashMap con los valores recorridos y obtenidos de la base de datos.
     */

    public static HashMap<String,UsuarioBloqueado> obtenerUsuariosBloqueados(FirebaseUser firebaseUser){

        DatabaseReference refernceBloquedUsers = getInstance().getReference("Bloqueados");
        listaUsuariosBloqueados = new HashMap<>();

        refernceBloquedUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuariosBloqueados.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UsuarioBloqueado usuarioBloqueado = snapshot.getValue(UsuarioBloqueado.class);
                    if(usuarioBloqueado != null){

                        if(usuarioBloqueado.getUsuarioAccionBloquear().contentEquals(firebaseUser.getUid())){
                            listaUsuariosBloqueados.put(usuarioBloqueado.getUsuarioBloqueado(),usuarioBloqueado);
                        }

                        for(Map.Entry<String, UsuarioBloqueado> entry : listaUsuariosBloqueados.entrySet()) {
                            //System.out.println(entry.getKey());
                            UsuarioBloqueado value = entry.getValue();
                            Log.d("DEBUG Funciones, VALOR USUARIO BLOQUEADO",value.getUsuarioBloqueado());
                        }
                    }
                }
                Log.d("DEBUG USUARIOS BLOQUEADOS", String.valueOf(listaUsuariosBloqueados.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listaUsuariosBloqueados;
    }

    /**
     *
     * No esta sujeto a cambios en tiempo real sobre la base de datos, devuelve la que contiene en la memoria actual.
     * @return Devuelve un HashMap, el String corresponde el id del usuario y su objeto Usuario para que no existan duplicidad.
     */

    public static HashMap<String,UsuarioBloqueado> getListaUsuariosBloqueados(){
        return listaUsuariosBloqueados;
    }


    /*
    @Deprecated
    public ArrayList<String> ObtenerListaUsuariosStringID(){
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

     */



}
