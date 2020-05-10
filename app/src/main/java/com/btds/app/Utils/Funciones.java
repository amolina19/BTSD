

package com.btds.app.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.PeticionAmistadUsuario;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

/**
 * @author Alejandro Molina Louchnikov
 */


public class Funciones {
    private static Usuario usuario;
    private static HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private static Fecha fecha;

    private static Usuario usuarioOjectReturn;
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
        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
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
        FirebaseUser firebaseUser = Funciones.getFirebaseUser();
        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
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

        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
        listaUsuariosBloqueados = new HashMap<>();

        refernceBloquedUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuariosBloqueados.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    UsuarioBloqueado usuarioBloqueado = snapshot.getValue(UsuarioBloqueado.class);
                    if(usuarioBloqueado != null){
                        if(firebaseUser != null){
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


    public static String obtenerEstadoUsuario(Context contexto, int diasPasados,Usuario usuarioChat){
        String text = null;
        if (diasPasados == 0) {
            text = contexto.getResources().getString(R.string.hoy) +" "+ usuarioChat.getHora();
        } else {
            if (diasPasados == 1 ) {
                text = contexto.getResources().getString(R.string.ayer) +" "+usuarioChat.getHora();
            } else if (diasPasados > 1) {
                String fecha = usuarioChat.getFecha().replace(" ", "/");
                text = contexto.getResources().getString(R.string.ultavez1parte)+" "+fecha+" "+ contexto.getResources().getString(R.string.ultavez2parte)+" "+ usuarioChat.getHora();
            }
        }

        if(text == null){
            return contexto.getResources().getString(R.string.errorAlCargar);
        }else{
            return text;
        }
    }

    public static int obtenerMinutosSubida(Estados estado){

        Log.d("DEBUG obtenerHorasSubida ","Fecha SUBIDA "+estado.getFecha().toString());
        String userEstadoDateMinute = estado.fecha.minutos;
        String userEstadoDateHour = estado.fecha.hora;
        String userEstadoDateDay =  estado.fecha.dia;
        String userEstadoDateMonth = estado.fecha.mes;
        String userEstadoDateYear = estado.fecha.anno;

        Log.d("DEBUG FECHA ESTADO OBJECT"," "+estado.getFecha().toString());

        fecha = new Fecha();
        LocalDateTime dateBefore = LocalDateTime.of(Integer.valueOf(userEstadoDateYear),Integer.valueOf(userEstadoDateMonth),Integer.valueOf(userEstadoDateDay),Integer.valueOf(userEstadoDateHour),Integer.valueOf(userEstadoDateMinute));
        LocalDateTime dateAfter = LocalDateTime.of(Integer.parseInt(fecha.obtenerAnno()),Integer.parseInt(fecha.obtenerMes()),Integer.parseInt(fecha.obtenerDia()),Integer.parseInt(fecha.obtenerHora()),Integer.parseInt(fecha.obtenerMinutos()));
        long minutos = ChronoUnit.MINUTES.between(dateBefore, dateAfter);
        int minutosTranscurridos = (int) minutos;
        Log.d("Debugging Minutos transcurridos", String.valueOf(+minutos));

        return minutosTranscurridos;
    }

    public static HashMap<String,Usuario> usuariosConEstados(){

        HashMap<String,Usuario> usuariosConEstados = new HashMap<>();

        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    //Log.d("DEBUG FUNCIONES obtenerEstados","ESTADO ITERADO");
                    Estados estado = data.getValue(Estados.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return  usuariosConEstados;
    }

    public static List<Estados> obtenerEstados(){
        ArrayList<Estados> listaEstados = new ArrayList<>();

        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaEstados.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Log.d("DEBUG FUNCIONES obtenerEstados","ESTADO ITERADO");
                    Estados estado = data.getValue(Estados.class);
                    listaEstados.add(estado);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listaEstados;
    }

    public static HashMap<String,PeticionAmistadUsuario> obtenerPeticionesAmistadEnviadas(FirebaseUser firebaseUser){

        HashMap<String, PeticionAmistadUsuario> peticionesEnviadas = new HashMap<>();



        return peticionesEnviadas;
    }


    public static String getSystemLanguage(){
        return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
    }

    public static CountryCodePicker.Language obtainLanguageContryPicker(){

        CountryCodePicker.Language language;

        String idioma = getSystemLanguage();

        switch (idioma){
            case "ar":
                language = CountryCodePicker.Language.ARABIC;
                break;
            case "es":
                language = CountryCodePicker.Language.SPANISH;
                break;
            case "fr":
                language = CountryCodePicker.Language.FRENCH;
                break;
            case "cn":
                language = CountryCodePicker.Language.CHINESE;
                break;
            case "pt":
                language = CountryCodePicker.Language.PORTUGUESE;
                break;
            case "de":
                language = CountryCodePicker.Language.GERMAN;
                break;
            case "ru":
                language = CountryCodePicker.Language.RUSSIAN;
                break;
            case "jp":
                language = CountryCodePicker.Language.JAPANESE;
                break;
            default:
                language = CountryCodePicker.Language.ENGLISH;
                break;
        }
        return language;
    }

    public static int getCountryCode(){
        int countryCode;

        switch (Funciones.getSystemLanguage()){
            case "es":
                countryCode = +34;
                break;
            case "fr":
                countryCode = +33;
                break;
            case "us":
                countryCode = +1;
                break;
            case "uk":
                countryCode = +44;
                break;
            case "cn":
                countryCode = +86;
                break;
            case "pt":
                countryCode = +351;
                break;
            case "jp":
                countryCode = +81;
                break;
            case "de":
                countryCode = +49;
                break;
            case "ru":
                countryCode = +7;
                break;
            default:
                countryCode = -1;
                break;
        }
        return countryCode;
    }

    public static void mostrarDatosUsuario(Usuario usuario){

        if(usuario.getId()!=null){
            Log.d("Funcion MostrarDatosUsuario, UID",usuario.getId());
        }
        if(usuario.getUsuario()!=null){
            Log.d("Funcion MostrarDatosUsuario, Usuario",usuario.getUsuario());
        }
        if(usuario.getDescripcion()!=null){
            Log.d("Funcion MostrarDatosUsuario, Descripcion",usuario.getDescripcion());
        }
        if(usuario.getEstado()!=null){
            Log.d("Funcion MostrarDatosUsuario, Estado",usuario.getEstado());
        }
        if(usuario.getFecha()!=null){
            Log.d("Funcion MostrarDatosUsuario, Fecha",usuario.getFecha());
        }
        if(usuario.getHora()!=null){
            Log.d("Funcion MostrarDatosUsuario, Hora",usuario.getHora());
        }
        if(usuario.getImagenURL()!=null){
            Log.d("Funcion MostrarDatosUsuario, IMAGEN URL",usuario.getImagenURL());
        }
        if(usuario.getTelefono()!=null){
            Log.d("Funcion MostrarDatosUsuario, Telefono",usuario.getTelefono());
        }
    }


    //Metodos para recoger instancias de Firebase.
    public static FirebaseAuth getAuthenticationInstance(){
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getDatabaseReference(){
        return getInstance().getReference();
    }

    public static DatabaseReference getUsersDatabaseReference(){
        return getInstance().getReference("Usuarios");
    }

    public static DatabaseReference getActualUserDatabaseReference(FirebaseUser firebaseUser){
        return getInstance().getReference("Usuarios").child(firebaseUser.getUid());
    }

    public static DatabaseReference getBlockUsersListDatabaseReference(){
        return getInstance().getReference("Bloqueados");
    }

    public static DatabaseReference getFriendsPetitionListDatabaseReference(){
        return getInstance().getReference("PeticionesAmigos");
    }

    public static DatabaseReference getFriendsListDatabaseReference(){
        return getInstance().getReference("ListaAmigos");
    }

    public static DatabaseReference getChatsDatabaseReference(){
        return getInstance().getReference("Chats");
    }

    public static DatabaseReference getEstadosDatabaseReference(){
        return getInstance().getReference("Estados");
    }

    public static DatabaseReference getEstadosUserDatabaseReference(FirebaseUser firebaseUser){
        return getInstance().getReference("Estados").child(firebaseUser.getUid());
    }

    public static FirebaseStorage getFirebaseStorage(){
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getFirebaseStorageReference(){
        return getFirebaseStorage().getReference();
    }

    public static StorageReference getUserEstadosFirebaseStorageReference(FirebaseUser firebaseUser){
        return getFirebaseStorage().getReference().child("Estados").child(firebaseUser.getUid());
    }

    public static DatabaseReference getPeticionesAmistadReference(){
        return getInstance().getReference().child("PeticionesAmistad");
    }

    public static PhoneAuthProvider getPhoneAuthProvider(){
        return PhoneAuthProvider.getInstance();
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