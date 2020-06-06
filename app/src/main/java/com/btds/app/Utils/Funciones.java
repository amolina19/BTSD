package com.btds.app.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.ListaMensajesChat;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.Modelos.Visibilidad;
import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.shrinathbhosale.preffy.Preffy;

/**
 * @author Alejandro Molina Louchnikov
 */


public class Funciones {
    private static HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private static HashMap<String,String> listaAmigos;
    private static Fecha fecha;

    public Funciones(){

    }


    /**
     * Función estática para actualizar la última conexión y el estado del usuario. Recorro la base de datos hasta encontrar la referencia de nuestro usuario y asignarselo a un objeto, asignamos los datos y actualizamos.
     * Recorro la base de datos de todos lso objetos ya que no estoy creado ni borrando un valor, sino actualizandolo.
     * @param estado Se le inserta un String, en este caso valores de strings en values/strings.xml
     * @param usuario El Objeto usuario para actualizar sus datos y obtener el id del usuario local.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void actualizarConexion(@NonNull final String estado, Usuario usuario) {

        fecha = new Fecha();

        usuario.setEstado(estado);
        usuario.setHora(new Fecha().hora+":"+new Fecha().minutos);
        usuario.setFecha(new Fecha().dia+" "+new Fecha().mes+" "+new Fecha().anno);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);

    }

    public static void actualizarT2A(boolean value,Usuario usuario){

        usuario.setT2Aintroduced(value);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
    }

    public static void VisibilidadUsuarioPublica(Usuario usuario){
        Visibilidad  visibilidad= new Visibilidad(true, true, true, true, true);
        usuario.setVisibilidad(visibilidad);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
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

    public static HashMap<String,String> obtenerListaAmigos(FirebaseUser firebaseUser){

        listaAmigos = new HashMap<>();
        DatabaseReference refernceAmigos = Funciones.getAmigosReference();

        refernceAmigos.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAmigos.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String valorClaveAmigo = data.getValue(String.class);
                    listaAmigos.put(valorClaveAmigo,valorClaveAmigo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listaAmigos;
    }

    /**
     *
     * No esta sujeto a cambios en tiempo real sobre la base de datos, devuelve la que contiene en la memoria actual.
     * @return Devuelve un HashMap, el String corresponde el id del usuario y su objeto Usuario para que no existan duplicidad.
     */

    public static HashMap<String,UsuarioBloqueado> getListaUsuariosBloqueados(){
        return listaUsuariosBloqueados;
    }

    public static Boolean obtenerEstadoUsuario(Context contexto, Usuario usuario){

        return usuario.getEstado().contentEquals(contexto.getResources().getString(R.string.online));

    }


    public static String obtenerEstadoUsuario(Context contexto, int diasPasados,Usuario usuarioChat){
        String text = null;
        if (diasPasados == 0) {
            text = contexto.getResources().getString(R.string.hoy) +" "+ usuarioChat.getHora();
        } else {
            if (diasPasados == 1 ) {
                text = contexto.getResources().getString(R.string.ayerAlas) +" "+usuarioChat.getHora();
            } else if (diasPasados > 1) {
                String fecha = usuarioChat.getFecha().replace(" ", "/");
                text = contexto.getResources().getString(R.string.ultavez1parte)+" "+fecha+" "+ usuarioChat.getHora();
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
        //String userEstadoDateMinute = estado.fecha.minutos;
        //String userEstadoDateHour = estado.fecha.hora;
        //String userEstadoDateDay =  estado.fecha.dia;
        //String userEstadoDateMonth = estado.fecha.mes;
        //String userEstadoDateYear = estado.fecha.anno;

        Log.d("DEBUG FECHA ESTADO OBJECT"," "+estado.getFecha().toString());

        Fecha fecha = new Fecha();
        //LocalDateTime dateBefore = LocalDateTime.of(Integer.valueOf(userEstadoDateYear),Integer.valueOf(userEstadoDateMonth),Integer.valueOf(userEstadoDateDay),Integer.valueOf(userEstadoDateHour),Integer.valueOf(userEstadoDateMinute));
        //LocalDateTime dateAfter = LocalDateTime.of(Integer.parseInt(fecha.obtenerAnno()),Integer.parseInt(fecha.obtenerMes()),Integer.parseInt(fecha.obtenerDia()),Integer.parseInt(fecha.obtenerHora()),Integer.parseInt(fecha.obtenerMinutos()));

        LocalDateTime dateBefore = LocalDateTime.of(estado.fecha.getAnnoInteger(),estado.fecha.getMesInteger(),estado.fecha.getDiaInteger(),estado.fecha.getHoraInteger(),estado.fecha.getMinutosInteger());
        LocalDateTime dateAfter = LocalDateTime.of(fecha.getAnnoInteger(),fecha.getMesInteger(),fecha.getDiaInteger(),fecha.getHoraInteger(),fecha.getMinutosInteger());
        long minutos = ChronoUnit.MINUTES.between(dateBefore, dateAfter);
        int minutosTranscurridos = (int) minutos;
        Log.d("Debugging Minutos transcurridos", String.valueOf(+minutos));

        return minutosTranscurridos;
    }

    public static String calcularTiempo(int milisegundos){
        String tiempo,hrsStr,minStr,segStr;
        int segundos = milisegundos/1000;
        if(segundos < 60){
            segStr = String.valueOf(segundos);
            tiempo = segStr+"s";
        }else if(segundos < 3600){
            int min = segundos/60;
            int seg = segundos - (min*60);
            segStr = String.valueOf(seg);
            minStr = String.valueOf(min);
            tiempo = minStr+"m "+segStr+"s";
        }else{
            int hrs = segundos/3600;
            int min = (segundos - (hrs/3600))/60;
            int seg = segundos - (min*60)+(hrs*3600);

            segStr = String.valueOf(seg);
            minStr = String.valueOf(min);
            hrsStr = String.valueOf(hrs);
            tiempo = hrsStr+"h "+minStr+"m "+segStr+"s";
        }
        return tiempo;
    }

    public static List<Integer> obtenerTiempoLlamada(int milisegundos){
        List<Integer> tiempoTranscurrido = new ArrayList<>();
        int segundos = milisegundos/1000;
        if(segundos < 60){
            tiempoTranscurrido.add(segundos);
        }else if(segundos < 3600){
            int min = segundos/60;
            int seg = segundos - (min*60);
            tiempoTranscurrido.add(seg);
            tiempoTranscurrido.add(min);
        }else{
            int hrs = segundos/3600;
            int min = (segundos - (hrs/3600))/60;
            int seg = segundos - (min*60)+(hrs*3600);

            tiempoTranscurrido.add(seg);
            tiempoTranscurrido.add(min);
            tiempoTranscurrido.add(hrs);
        }

        return tiempoTranscurrido;
    }

    public static int obtenerTiempoPasadosMensajes(Mensaje mensaje1, Mensaje mensaje2){

        LocalDateTime dateMensaje1 = LocalDateTime.of(mensaje1.getFecha().getAnnoInteger(),mensaje1.getFecha().getMesInteger(),mensaje1.getFecha().getDiaInteger(),mensaje1.getFecha().getHoraInteger(),mensaje1.getFecha().getMinutosInteger(),mensaje1.getFecha().getSegundosInteger());
        LocalDateTime dateMensaje2 = LocalDateTime.of(mensaje2.getFecha().getAnnoInteger(),mensaje2.getFecha().getMesInteger(),mensaje2.getFecha().getDiaInteger(),mensaje2.getFecha().getHoraInteger(),mensaje2.getFecha().getMinutosInteger(),mensaje2.getFecha().getSegundosInteger());

        long segundos = ChronoUnit.SECONDS.between(dateMensaje1, dateMensaje2);
        return (int) segundos;
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
                countryCode = 34;
                break;
            case "fr":
                countryCode = 33;
                break;
            case "us":
                countryCode = 1;
                break;
            case "uk":
                countryCode = 44;
                break;
            case "cn":
                countryCode = 86;
                break;
            case "pt":
                countryCode = 351;
                break;
            case "jp":
                countryCode = 81;
                break;
            case "de":
                countryCode = 49;
                break;
            case "ru":
                countryCode = 7;
                break;
            default:
                countryCode = 1;
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

    public static boolean conectividadDisponible(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void aceptarAmigo(FirebaseUser firebaseUser, Usuario usuarioAceptar){

        if(usuarioAceptar !=null && firebaseUser != null){
            String valorAmigoID = usuarioAceptar.getId();
            String valorUsuarioActual = firebaseUser.getUid();

            Funciones.getAmigosReference().child(firebaseUser.getUid()).child(valorAmigoID).setValue(valorAmigoID);
            Funciones.getAmigosReference().child(usuarioAceptar.getId()).child(valorUsuarioActual).setValue(valorUsuarioActual);
            Funciones.eliminarPeticion(firebaseUser,usuarioAceptar);
        }
    }

    public static Visibilidad corregirVisibilidad(Context context){

        Visibilidad visibilidad = new Visibilidad();
        Preffy preffy = Preffy.getInstance(context);

        String keyUsuario  = preffy.getString("VisibilidadUsuario");
        if(keyUsuario.contentEquals("true")){
            visibilidad.setUsuario(true);
        }else{
            visibilidad.setUsuario(false);
        }

        String keyDescripcion  = preffy.getString("VisibilidadDescripcion");
        if(keyDescripcion.contentEquals("true")){
            visibilidad.setDescripcion(true);
        }else{
            visibilidad.setDescripcion(false);
        }

        String keyTelefono  = preffy.getString("VisibilidadTelefono");
        if(keyTelefono.contentEquals("true")){
            visibilidad.setTelefono(true);
        }else{
            visibilidad.setTelefono(false);
        }

        String keyFoto  = preffy.getString("VisibilidadFoto");
        if(keyFoto.contentEquals("true")){
            visibilidad.setFoto(true);
        }else{
            visibilidad.setFoto(false);
        }

        String keyLinea  = preffy.getString("VisibilidadLinea");
        if(keyLinea.contentEquals("true")){
            visibilidad.setEnLinea(true);
        }else{
            visibilidad.setEnLinea(false);
        }
        return visibilidad;
    }

    public static Visibilidad corregirVisibilidadPerfilIniciarSesion(Context context,Usuario usuario){

        Visibilidad visibilidad = new Visibilidad();
        Preffy preffy = Preffy.getInstance(context);

        if(usuario.getVisibilidad().getUsuario()){
            preffy.putString("VisibilidadUsuario","true");
            visibilidad.setUsuario(true);
        }else{
            preffy.putString("VisibilidadUsuario","false");
            visibilidad.setUsuario(false);
        }

        if(usuario.getVisibilidad().getDescripcion()){
            preffy.putString("VisibilidadDescripcion","true");
            visibilidad.setDescripcion(true);
        }else{
            preffy.putString("VisibilidadDescripcion","false");
            visibilidad.setDescripcion(false);
        }


        if(usuario.getVisibilidad().getTelefono()){
            preffy.putString("VisibilidadTelefono","true");
            visibilidad.setTelefono(true);
        }else{
            preffy.putString("VisibilidadTelefono","false");
            visibilidad.setTelefono(false);
        }


        if(usuario.getVisibilidad().getFoto()){
            preffy.putString("VisibilidadFoto","true");
            visibilidad.setFoto(true);
        }else{
            preffy.putString("VisibilidadFoto","false");
            visibilidad.setFoto(false);
        }

        if(usuario.getVisibilidad().getEnLinea()){
            preffy.putString("VisibilidadLinea","true");
            visibilidad.setEnLinea(true);
        }else{
            preffy.putString("VisibilidadLinea","true");
            visibilidad.setEnLinea(false);
        }
        return visibilidad;
    }

    public static void eliminarPeticion(FirebaseUser firebaseUser,Usuario usuarioPeticion){

        if(usuarioPeticion != null && firebaseUser != null){
            String clavePeticion1 = usuarioPeticion.getId()+""+firebaseUser.getUid();
            String clavePeticion2 = firebaseUser.getUid()+""+usuarioPeticion.getId();
            Funciones.getPeticionesAmistadReference().child(clavePeticion1).removeValue();
            Funciones.getPeticionesAmistadReference().child(clavePeticion2).removeValue();
        }
    }

    public static void eliminarPeticion(String peticionKey){

        if(peticionKey != null){
            Funciones.getPeticionesAmistadReference().child(peticionKey).removeValue();
        }
    }

    public static void borrarAmigo(FirebaseUser firebaseUser, Usuario usuarioBorrar){

        if(usuarioBorrar != null && firebaseUser != null){
            String valorAmigoID = usuarioBorrar.getId();
            String valorUsuarioActual = firebaseUser.getUid();

            Funciones.getAmigosReference().child(firebaseUser.getUid()).child(valorAmigoID).removeValue();
            Funciones.getAmigosReference().child(usuarioBorrar.getId()).child(valorUsuarioActual).removeValue();
        }

    }


    public static SinchClient startSinchClient(Context context,FirebaseUser firebaseUser){

        final String APP_KEY = "b464b750-1044-4c6c-91f0-d35dde526b58";
        final String APP_SECRET = "go21mH/XrUSnK12FmjNtdA==";
        final String ENVIRONMENT = "clientapi.sinch.com";

        SinchClient sinchClient;

        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(firebaseUser.getUid())
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        return sinchClient;
    }

    //https://firebase.google.com/docs/database/android/offline-capabilities?hl=es
    public static void persistencia(Boolean value){
        FirebaseDatabase.getInstance().setPersistenceEnabled(value);
    }

    //Metodos para recoger instancias de Firebase.
    public static FirebaseAuth getAuthenticationInstance(){
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getDatabaseReference(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getUsersDatabaseReference(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getActualUserDatabaseReference(FirebaseUser firebaseUser){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getBlockUsersListDatabaseReference(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bloqueados");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getChatsDatabaseReference(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getEstadosDatabaseReference(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Estados");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getAmigosReference(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Amigos");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getPeticionesAmistadReference(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PeticionesAmistad");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getLlamadasReference(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Llamadas");
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getLlamadasReferenceFragment(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Llamadas");
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static FirebaseStorage getFirebaseStorage(){
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getFirebaseStorageReference(){
        return getFirebaseStorage().getReference();
    }


    public static PhoneAuthProvider getPhoneAuthProvider(){
        return PhoneAuthProvider.getInstance();
    }

    public static void borrarHistoria(Estados estado){
        String idEstado = estado.getKey();
        Funciones.getEstadosDatabaseReference().child(idEstado).removeValue();
    }


    public static int tiempoTranscurrido(Fecha fecha){

        Fecha fechaActual = new Fecha();

        LocalDateTime dateFechaIntroducida = LocalDateTime.of(fecha.getAnnoInteger(),fecha.getMesInteger(),fecha.getDiaInteger(),fecha.getHoraInteger(),fecha.getMinutosInteger());
        LocalDateTime dateFechaActual = LocalDateTime.of(fechaActual.getAnnoInteger(),fechaActual.getMesInteger(),fechaActual.getDiaInteger(),fechaActual.getHoraInteger(),fechaActual.getMinutosInteger());

        long segundos = ChronoUnit.MINUTES.between(dateFechaIntroducida, dateFechaActual);
        return (int) segundos;
    }

    public static List<ListaMensajesChat> ordernarChat(List<Usuario> listaUsuarios, List<Mensaje> listaMensajes){

        HashMap<String,Usuario> usuarioHashMap = new HashMap<>();
        List<ListaMensajesChat> lista = new ArrayList<>();

        if(listaUsuarios !=null && listaMensajes != null){

            for(Usuario usuario:listaUsuarios){

                for(Mensaje mensaje:listaMensajes){

                    if(!usuarioHashMap.containsKey(usuario.getId()) && (mensaje.getReceptor().contentEquals(usuario.getId()) || mensaje.getEmisor().contentEquals(usuario.getId()))){
                        usuarioHashMap.put(usuario.getId(),usuario);
                        lista.add(new ListaMensajesChat(usuario,mensaje));
                    }

                }
            }

            Log.d("Debug Funciones ordenarChat","Lista total mensajes "+lista.size());
        }
        return lista;
    }
}