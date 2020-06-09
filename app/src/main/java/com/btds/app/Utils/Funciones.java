package com.btds.app.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

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
    private static HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados;
    private static HashMap<String, String> listaAmigos;
    private static Fecha fecha;

    public Funciones() {

    }


    /**
     * Función estática para actualizar la última conexión y el estado del usuario. Recorro la base de datos hasta encontrar la referencia de nuestro usuario y asignarselo a un objeto, asignamos los datos y actualizamos.
     * Recorro la base de datos de todos lso objetos ya que no estoy creado ni borrando un valor, sino actualizandolo.
     *
     * @param estado  Se le inserta un String, en este caso valores de strings en values/strings.xml
     * @param usuario El Objeto usuario para actualizar sus datos y obtener el id del usuario local.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void actualizarConexion(@NonNull final String estado, Usuario usuario) {

        fecha = new Fecha();

        usuario.setEstado(estado);
        usuario.setHora(new Fecha().hora + ":" + new Fecha().minutos);
        usuario.setFecha(new Fecha().dia + " " + new Fecha().mes + " " + new Fecha().anno);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);

    }

    /**
     * Función estática para actualizar el autentificador de un usuario cuando inicia sesión.
     *
     * @param usuario El Objeto usuario para actualizar sus datos y obtener el id del usuario local.
     * @param value   valor que se le pasa a la funcion pudiendo ser true o false.
     */
    public static void actualizarT2A(boolean value, Usuario usuario) {

        usuario.setT2Aintroduced(value);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
    }

    /**
     * Función estática para actualizar la visibilidad de los datos de un ususario sobre otros usuarios
     *
     * @param usuario El Objeto usuario para actualizar sus datos y obtener el id del usuario local.
     */
    public static void VisibilidadUsuarioPublica(Usuario usuario) {
        Visibilidad visibilidad = new Visibilidad(true, true, true, true, true);
        usuario.setVisibilidad(visibilidad);
        Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
    }


    /**
     * Se obtiene su últ conexión y cálcular cuantos días han transcurrido devolviendo un número entero.
     *
     * @param usuarioChat objeto Usuario del usuario que estamos chateando
     * @return devuelve el número de dias en un int.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static float obtenerDiasPasados(Usuario usuarioChat) {

        fecha = new Fecha();
        String fechaUsuario = usuarioChat.getFecha();

        int userChatDateDay = Integer.parseInt(fechaUsuario.replace(" ", "").substring(0, 2));
        int userChatDateMonth = Integer.parseInt(fechaUsuario.replace(" ", "").substring(2, 4));
        int userChatDateYear = Integer.parseInt(fechaUsuario.replace(" ", "").substring(4, 8));

        LocalDate dateBefore = LocalDate.of(userChatDateYear, userChatDateMonth, userChatDateDay);
        LocalDate dateAfter = LocalDate.of(Integer.parseInt(fecha.obtenerAnno()), Integer.parseInt(fecha.obtenerMes()), Integer.parseInt(fecha.obtenerDia()));
        long nDias = ChronoUnit.DAYS.between(dateBefore, dateAfter);

        return nDias;
    }

    /**
     * @param n Parámetro la función que le insertamos para que nos devuelva una cadena aleatoria de String con esa longitud.
     * @return Devuelve la cadena aleatoria de String generada.
     */
    public static String getAlphaNumericString(int n) {


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
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    /**
     * Parámetro donde se le introduce un objeto Usuario dentro de la función a través de cualquier actividad que se use para borrarlo de la lista de bloqueados del usuario.
     *
     * @param usuarioChat Objeto Usuario con el que estamos teniendo la conversación.
     */

    public static void desbloquearUsuario(Usuario usuarioChat) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
        assert firebaseUser != null;
        refernceBloquedUsers.child(firebaseUser.getUid() + "" + usuarioChat.getId()).removeValue();

        getListaUsuariosBloqueados().remove(usuarioChat.getId());
    }

    /**
     * Parámetro donde se le introduce un objeto Usuario dentro de la función a través de cualquier actividad que se use para añadirlo a la lista de bloqueados del usuario.
     * Al bloquear el usuario se útiliza el ID del usuario que bloquea + ID del usuario bloqueado para que no exista duplicidad.
     *
     * @param usuarioChat objeto Usuario con el que estamos teniendo la conversación.
     */

    public static void bloquearUsuario(Usuario usuarioChat) {
        FirebaseUser firebaseUser = Funciones.getFirebaseUser();
        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
        UsuarioBloqueado usuarioBloqueadoObject = new UsuarioBloqueado();

        assert firebaseUser != null;
        usuarioBloqueadoObject.setKey(firebaseUser.getUid() + "" + usuarioChat.getId());
        usuarioBloqueadoObject.setUsuarioAccionBloquear(firebaseUser.getUid());
        usuarioBloqueadoObject.setUsuarioBloqueado(usuarioChat.getId());

        refernceBloquedUsers.child(usuarioBloqueadoObject.getKey()).setValue(usuarioBloqueadoObject).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
            }
        });

    }

    /**
     * Devuelve un HashMap donde el String es el id del usuario y su objeto Usuario para que sean únicos. Cada vez que se introduce o elimine un registro devolverá una lista nueva.
     *
     * @return devuelve el HashMap con los valores recorridos y obtenidos de la base de datos.
     */

    public static HashMap<String, UsuarioBloqueado> obtenerUsuariosBloqueados(FirebaseUser firebaseUser) {

        DatabaseReference refernceBloquedUsers = Funciones.getBlockUsersListDatabaseReference();
        listaUsuariosBloqueados = new HashMap<>();

        refernceBloquedUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuariosBloqueados.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UsuarioBloqueado usuarioBloqueado = snapshot.getValue(UsuarioBloqueado.class);
                    if (usuarioBloqueado != null) {
                        if (firebaseUser != null) {
                            if (usuarioBloqueado.getUsuarioAccionBloquear().contentEquals(firebaseUser.getUid())) {
                                listaUsuariosBloqueados.put(usuarioBloqueado.getUsuarioBloqueado(), usuarioBloqueado);
                            }

                            for (Map.Entry<String, UsuarioBloqueado> entry : listaUsuariosBloqueados.entrySet()) {
                                //System.out.println(entry.getKey());
                                UsuarioBloqueado value = entry.getValue();
                            }
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

    /**
     * Devuelve un HashMap donde el String es el id del usuario y su objeto Usuario para que sean únicos. Cada vez que se introduce o elimine un registro devolverá una lista nueva.
     *
     * @return devuelve el HashMap con los valores recorridos y obtenidos de la base de datos.
     */
    public static HashMap<String, String> obtenerListaAmigos(FirebaseUser firebaseUser) {

        listaAmigos = new HashMap<>();
        DatabaseReference refernceAmigos = Funciones.getAmigosReference();

        refernceAmigos.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAmigos.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String valorClaveAmigo = data.getValue(String.class);
                    listaAmigos.put(valorClaveAmigo, valorClaveAmigo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listaAmigos;
    }

    /**
     * No esta sujeto a cambios en tiempo real sobre la base de datos, devuelve la que contiene en la memoria actual.
     *
     * @return Devuelve un HashMap, el String corresponde el id del usuario y su objeto Usuario para que no existan duplicidad.
     */
    public static HashMap<String, UsuarioBloqueado> getListaUsuariosBloqueados() {
        return listaUsuariosBloqueados;
    }

    /**
     * Función estática que comprueba si el estado esa en Linea o Desconectado en una funcion devolviendo un booleano.
     *
     * @param contexto se le introduce el contexto de la actividad en la que se encuentra.
     * @param usuario  se le introduce el usuario para comprar los datos.
     * @return devuelve un valor booleano.
     */

    public static Boolean obtenerEstadoUsuario(Context contexto, Usuario usuario) {

        return usuario.getEstado().contentEquals(contexto.getResources().getString(R.string.online));

    }

    /**
     * Función estática que devuelve una cadena de string sobre la conexión del usuario en el MessageActivity
     *
     * @param contexto    se le introduce el contexto de la actividad en la que se encuentra.
     * @param diasPasados se le introduce un número entero de días que han pasado desde su última conexión.
     * @param usuarioChat se le introduce el usuario del chat actual para obtener los datos de su última conexión.
     * @return devuelve la cadena en String concatenada sobre su última conexión.
     */


    public static String obtenerEstadoUsuario(Context contexto, int diasPasados, Usuario usuarioChat) {
        String text = null;
        if (diasPasados == 0) {
            text = contexto.getResources().getString(R.string.hoy) + " " + usuarioChat.getHora();
        } else {
            if (diasPasados == 1) {
                text = contexto.getResources().getString(R.string.ayerAlas) + " " + usuarioChat.getHora();
            } else if (diasPasados > 1) {
                String fecha = usuarioChat.getFecha().replace(" ", "/");
                text = contexto.getResources().getString(R.string.ultavez1parte) + " " + fecha + " " + usuarioChat.getHora();
            }
        }

        if (text == null) {
            return contexto.getResources().getString(R.string.errorAlCargar);
        } else {
            return text;
        }
    }

    /**
     * Función estática que devuelve los minutos transcurridos de una estado que se ha subido hasta la fecha actual.
     *
     * @param estado se le introduce el estado que se quiere calcular cuantos minutos han transcurrido.
     * @return devuelve en integer los minutos transcurridos desde su subida.
     */

    public static int obtenerMinutosSubida(Estados estado) {

        Fecha fecha = new Fecha();
        LocalDateTime dateBefore = LocalDateTime.of(estado.fecha.getAnnoInteger(), estado.fecha.getMesInteger(), estado.fecha.getDiaInteger(), estado.fecha.getHoraInteger(), estado.fecha.getMinutosInteger());
        LocalDateTime dateAfter = LocalDateTime.of(fecha.getAnnoInteger(), fecha.getMesInteger(), fecha.getDiaInteger(), fecha.getHoraInteger(), fecha.getMinutosInteger());
        long minutos = ChronoUnit.MINUTES.between(dateBefore, dateAfter);
        int minutosTranscurridos = (int) minutos;
        return minutosTranscurridos;
    }

    /**
     * Función estática que devuelve los segundos transcurridos en la grabación de audio en MessageActivity dentro del MensajeAdapter
     *
     * @param milisegundos se le introduce en integer los milisegundos de duracion de MediaPlayer.
     * @return devuelve en String concatenado la duración del mensaje de audio al adaptador.
     */

    public static String calcularTiempo(int milisegundos) {
        String tiempo, hrsStr, minStr, segStr;
        int segundos = milisegundos / 1000;
        if (segundos < 60) {
            segStr = String.valueOf(segundos);
            tiempo = segStr + "s";
        } else if (segundos < 3600) {
            int min = segundos / 60;
            int seg = segundos - (min * 60);
            segStr = String.valueOf(seg);
            minStr = String.valueOf(min);
            tiempo = minStr + "m " + segStr + "s";
        } else {
            int hrs = segundos / 3600;
            int min = (segundos - (hrs / 3600)) / 60;
            int seg = segundos - (min * 60) + (hrs * 3600);

            segStr = String.valueOf(seg);
            minStr = String.valueOf(min);
            hrsStr = String.valueOf(hrs);
            tiempo = hrsStr + "h " + minStr + "m " + segStr + "s";
        }
        return tiempo;
    }

    /**
     * Función estática que devuelve los segundos transcurridos entre la creación y la finalización de una llamada.
     *
     * @param milisegundos se le introduce en integer los milisegundos de duracion de un chronometer.
     * @return devuelve en una Lista siendo la posicion 0 los segundos, 1 los minutos y 2 las horas. Puede devolver una lista entre 1 y 3 posiciones.
     */

    public static List<Integer> obtenerTiempoLlamada(int milisegundos) {
        List<Integer> tiempoTranscurrido = new ArrayList<>();
        int segundos = milisegundos / 1000;
        if (segundos < 60) {
            tiempoTranscurrido.add(segundos);
        } else if (segundos < 3600) {
            int min = segundos / 60;
            int seg = segundos - (min * 60);
            tiempoTranscurrido.add(seg);
            tiempoTranscurrido.add(min);
        } else {
            int hrs = segundos / 3600;
            int min = (segundos - (hrs / 3600)) / 60;
            int seg = segundos - (min * 60) + (hrs * 3600);

            tiempoTranscurrido.add(seg);
            tiempoTranscurrido.add(min);
            tiempoTranscurrido.add(hrs);
        }

        return tiempoTranscurrido;
    }

    /**
     * Función estática que devuelve los segundos transcurridos entre la fecha de creación de un mensaje y otro mensaje. Se utiliza en un compareTo dentro de ListaMensajesChat para ordenar en los chats los mensajes más recientes.
     *
     * @param mensaje1 el primer mensaje que se quiere calcular la fecha.
     * @param mensaje2 el segundo mensaje que se quiere calcular la fecha.
     * @return devulve en integer los segundos transcurridos entre un mensaje y otro. Si es negativo el mensaje1 es mas viejo que mensaje2 y positivo al reves. Devuelve
     */

    public static int obtenerTiempoPasadosMensajes(Mensaje mensaje1, Mensaje mensaje2) {

        LocalDateTime dateMensaje1 = LocalDateTime.of(mensaje1.getFecha().getAnnoInteger(), mensaje1.getFecha().getMesInteger(), mensaje1.getFecha().getDiaInteger(), mensaje1.getFecha().getHoraInteger(), mensaje1.getFecha().getMinutosInteger(), mensaje1.getFecha().getSegundosInteger());
        LocalDateTime dateMensaje2 = LocalDateTime.of(mensaje2.getFecha().getAnnoInteger(), mensaje2.getFecha().getMesInteger(), mensaje2.getFecha().getDiaInteger(), mensaje2.getFecha().getHoraInteger(), mensaje2.getFecha().getMinutosInteger(), mensaje2.getFecha().getSegundosInteger());

        long segundos = ChronoUnit.SECONDS.between(dateMensaje1, dateMensaje2);
        return (int) segundos;
    }

    /**
     * Función estática que devuelve en String el idioma del sistema en formato es-es o us-en. Para su posterior uso dinámico para registrar o verificar un número de teléfono.
     *
     * @return devulve en String en formato es-es o us-en el idioma del sistema.
     */

    public static String getSystemLanguage() {
        return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
    }

    /**
     * Función estática que devuelve en Language del idioma del sistema, para el idioma de CountryCodePicker.
     *
     * @return devulve en CountryCode.Language según el idioma del sistema.
     */

    public static CountryCodePicker.Language obtainLanguageContryPicker() {

        CountryCodePicker.Language language;

        String idioma = getSystemLanguage();

        switch (idioma) {
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

    /**
     * Función estática que devuelve en Intenger el formato del código de número según el idioma del sistema, para el CountryCodePicker.
     *
     * @return devulve en Integer el código +34, +1 según el idioma del sistema.
     */

    public static int getCountryCode() {
        int countryCode;

        switch (Funciones.getSystemLanguage()) {
            case "es":
                countryCode = 34;
                break;
            case "fr":
                countryCode = 33;
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

    /**
     * Función estática que devuelve los datos del usuario en los Logs de la actividad para comprobar que este correcto.
     *
     * @param usuario se le introduce el usuario al que se van a mostrar los datos.
     */

    public static void mostrarDatosUsuario(Usuario usuario) {

        if (usuario.getId() != null) {
        }
        if (usuario.getUsuario() != null) {
        }
        if (usuario.getDescripcion() != null) {
        }
        if (usuario.getEstado() != null) {
        }
        if (usuario.getFecha() != null) {
        }
        if (usuario.getHora() != null) {
        }
        if (usuario.getImagenURL() != null) {
        }
        if (usuario.getTelefono() != null) {
        }
    }

    /**
     * Función estática que devuelve un booleano indicando si el dispositivo tiene conectividad con Wifi o datos móviles 4G.
     *
     * @param context contexto sobre la actividad donde se ejecuta la función.
     * @return un booleano indicando si tiene conectividad o no.
     */

    @SuppressWarnings("deprecation")
    public static boolean conectividadDisponible(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Función estática que inserta en la base de datos de ambos usuarios el código del amigo con el firebaseID y eliminando la petición del usuario en cuestión.
     *
     * @param firebaseUser   se le introduce el objeto firebaseUser actual para conocer su id.
     * @param usuarioAceptar usuario que se introduce para conocer su código de firebase.
     */

    public static void aceptarAmigo(FirebaseUser firebaseUser, Usuario usuarioAceptar) {

        if (usuarioAceptar != null && firebaseUser != null) {
            String valorAmigoID = usuarioAceptar.getId();
            String valorUsuarioActual = firebaseUser.getUid();

            Funciones.getAmigosReference().child(firebaseUser.getUid()).child(valorAmigoID).setValue(valorAmigoID);
            Funciones.getAmigosReference().child(usuarioAceptar.getId()).child(valorUsuarioActual).setValue(valorUsuarioActual);
            Funciones.eliminarPeticion(firebaseUser, usuarioAceptar);
        }
    }

    /**
     * Función estática que corrige la visibilidad e un usuario cuando se desconecta, ya que el objeto usuario de MainActivity y AjustesActivity no son el mismo objeto y pueden guardar distintos valores. Compruebo con SharedPreferences su código actual con claves.
     *
     * @param context el contexto de la actividad donde se ejecuta la función.
     * @return devuelve un objeto Visibilidad para que el usuario obtenga los valores corregidos según las SharedPreferences.
     */

    public static Visibilidad corregirVisibilidad(Context context) {

        Visibilidad visibilidad = new Visibilidad();
        Preffy preffy = Preffy.getInstance(context);

        String keyUsuario = preffy.getString("VisibilidadUsuario");
        if (keyUsuario.contentEquals("true")) {
            visibilidad.setUsuario(true);
        } else {
            visibilidad.setUsuario(false);
        }

        String keyDescripcion = preffy.getString("VisibilidadDescripcion");
        if (keyDescripcion.contentEquals("true")) {
            visibilidad.setDescripcion(true);
        } else {
            visibilidad.setDescripcion(false);
        }

        String keyTelefono = preffy.getString("VisibilidadTelefono");
        if (keyTelefono.contentEquals("true")) {
            visibilidad.setTelefono(true);
        } else {
            visibilidad.setTelefono(false);
        }

        String keyFoto = preffy.getString("VisibilidadFoto");
        if (keyFoto.contentEquals("true")) {
            visibilidad.setFoto(true);
        } else {
            visibilidad.setFoto(false);
        }

        String keyLinea = preffy.getString("VisibilidadLinea");
        if (keyLinea.contentEquals("true")) {
            visibilidad.setEnLinea(true);
        } else {
            visibilidad.setEnLinea(false);
        }
        return visibilidad;
    }

    /**
     * Función estática que corrige la visibilidad e un usuario cuando inicia sesión, los valores almacenados en el dispositivo local pueden ser disintos a la base de datos.
     *
     * @param context el contexto de la actividad donde se ejecuta la función.
     * @param usuario el usuario que inicia sesión y se le corrige su visibilidad.
     * @return devuelve un objeto Visibilidad para que el usuario obtenga los valores corregidos según la base de datos.
     */

    public static Visibilidad corregirVisibilidadPerfilIniciarSesion(Context context, Usuario usuario) {

        Visibilidad visibilidad = new Visibilidad();
        Preffy preffy = Preffy.getInstance(context);

        if (usuario.getVisibilidad().getUsuario()) {
            preffy.putString("VisibilidadUsuario", "true");
            visibilidad.setUsuario(true);
        } else {
            preffy.putString("VisibilidadUsuario", "false");
            visibilidad.setUsuario(false);
        }

        if (usuario.getVisibilidad().getDescripcion()) {
            preffy.putString("VisibilidadDescripcion", "true");
            visibilidad.setDescripcion(true);
        } else {
            preffy.putString("VisibilidadDescripcion", "false");
            visibilidad.setDescripcion(false);
        }


        if (usuario.getVisibilidad().getTelefono()) {
            preffy.putString("VisibilidadTelefono", "true");
            visibilidad.setTelefono(true);
        } else {
            preffy.putString("VisibilidadTelefono", "false");
            visibilidad.setTelefono(false);
        }


        if (usuario.getVisibilidad().getFoto()) {
            preffy.putString("VisibilidadFoto", "true");
            visibilidad.setFoto(true);
        } else {
            preffy.putString("VisibilidadFoto", "false");
            visibilidad.setFoto(false);
        }

        if (usuario.getVisibilidad().getEnLinea()) {
            preffy.putString("VisibilidadLinea", "true");
            visibilidad.setEnLinea(true);
        } else {
            preffy.putString("VisibilidadLinea", "true");
            visibilidad.setEnLinea(false);
        }
        return visibilidad;
    }

    /**
     * Función estática que corrige elimina la petición de amistad cuando un usuario acepta ser amigo de otro usuario en PeticionesAdapter y en la función aceptarAmigos mediante la unión de las 2 claves de los usuarios.
     *
     * @param firebaseUser    el objeto FirebaseUser para obtener los datos actuales del usuario y su id.
     * @param usuarioPeticion el usuario que envió la petición y conocer su id para poder borrarla.
     */

    public static void eliminarPeticion(FirebaseUser firebaseUser, Usuario usuarioPeticion) {

        if (usuarioPeticion != null && firebaseUser != null) {
            String clavePeticion1 = usuarioPeticion.getId() + "" + firebaseUser.getUid();
            String clavePeticion2 = firebaseUser.getUid() + "" + usuarioPeticion.getId();
            Funciones.getPeticionesAmistadReference().child(clavePeticion1).removeValue();
            Funciones.getPeticionesAmistadReference().child(clavePeticion2).removeValue();
        }
    }

    /**
     * Función estática que corrige elimina la petición de amistad cuando un usuario acepta ser amigo de otro usuario en PeticionesAdapter, obtenerPeticiones().
     *
     * @param peticionKey la clave de la petición que va a ser eliminada.
     */

    public static void eliminarPeticion(String peticionKey) {

        if (peticionKey != null) {
            Funciones.getPeticionesAmistadReference().child(peticionKey).removeValue();
        }
    }

    /**
     * Función estática que borra de ambos usuarios su referencia de amigos
     *
     * @param firebaseUser  el objeto FirebaseUser para obtener los datos actuales del usuario y su id.
     * @param usuarioBorrar el usuario al que se va a borrar de la lista de amigos.
     */

    public static void borrarAmigo(FirebaseUser firebaseUser, Usuario usuarioBorrar) {

        if (usuarioBorrar != null && firebaseUser != null) {
            String valorAmigoID = usuarioBorrar.getId();
            String valorUsuarioActual = firebaseUser.getUid();

            Funciones.getAmigosReference().child(firebaseUser.getUid()).child(valorAmigoID).removeValue();
            Funciones.getAmigosReference().child(usuarioBorrar.getId()).child(valorUsuarioActual).removeValue();
        }

    }

    /**
     * Función estática que inicializa el SDK de Sinch y da de alta un cliente para poder recibir llamadas más adelante
     *
     * @param context      el contexto indica en que actividad se va a ejecutar la función.
     * @param firebaseUser el objeto FirebaseUser para obtener los datos actuales del usuario y su id.
     */

    public static SinchClient startSinchClient(Context context, FirebaseUser firebaseUser) {

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

    /**
     * Función estática que establece la persistencia de los datos del dispositivo cuando este sin conexión a Internet. Es la primera función en ser utilizada al ser iniciado la aplicación. No debe existir en su creación ninguna instancia de Fireabase y de DatabaseReference.
     *
     * @param value indica si se activa o desactivará la persistencia en el dispositivo
     */

    //https://firebase.google.com/docs/database/android/offline-capabilities?hl=es
    public static void persistencia(Boolean value) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(value);
    }

    /**
     * Función estática que devuelve una instancia de FirebaseAuth
     *
     * @return devuelve un objeto FirebaseAuth.
     */

    //Metodos para recoger instancias de Firebase.
    public static FirebaseAuth getAuthenticationInstance() {
        return FirebaseAuth.getInstance();
    }

    /**
     * Función estática que devuelve una instancia de FirebaseUser
     *
     * @return devuelve un objeto FirebaseUser
     */

    public static FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getDatabaseReference() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de todos usuarios la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getUsersDatabaseReference() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz del usuario actual con FirebaseUser la base de datos.
     *
     * @param firebaseUser Se le introduce un objeto FirebaseUser para conocer el id del usuario actual.
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getActualUserDatabaseReference(FirebaseUser firebaseUser) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de todos usuarios bloqueados la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getBlockUsersListDatabaseReference() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bloqueados");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de los chats la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getChatsDatabaseReference() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de los estados la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getEstadosDatabaseReference() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Estados");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de los amigos la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getAmigosReference() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Amigos");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de las peticiones de amistad de la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getPeticionesAmistadReference() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PeticionesAmistad");
        //Persitencia en Disco
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de DatabaseReference de la raíz de las llamadas realizadas la base de datos.
     *
     * @return devuelve un objeto DatabaseReference y indica que los datos que se descargen y estén sincronizados con el dispositivo aún este cuando no dispone de conexión a Internet.
     */

    public static DatabaseReference getLlamadasReference() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Llamadas");
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    public static DatabaseReference getLlamadasReferenceFragment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Llamadas");
        databaseReference.keepSynced(true);
        return databaseReference;
    }

    /**
     * Función estática que devuelve una instancia de FirebaseStorage de la raíz de la base de datos.
     *
     * @return devuelve un objeto FirebaseStorage.
     */

    public static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    /**
     * Función estática que devuelve una instancia de StorageReference de la raíz de la base de datos.
     *
     * @return devuelve un objeto StorageReference.
     */

    public static StorageReference getFirebaseStorageReference() {
        return getFirebaseStorage().getReference();
    }

    /**
     * Función estática que devuelve una instancia de PhoneAuthProvider.
     *
     * @return devuelve un objeto PhoneAuthProvider.
     */

    public static PhoneAuthProvider getPhoneAuthProvider() {
        return PhoneAuthProvider.getInstance();
    }

    /**
     * Función estática que borra un estado de un usuario.
     *
     * @param estado el estado que se quiere borrar y recoger su id.
     */

    public static void borrarHistoria(Estados estado) {
        String idEstado = estado.getKey();
        Funciones.getEstadosDatabaseReference().child(idEstado).removeValue();
    }

    /**
     * Función estática que calcula el tiempo transcurridos en minutos insertando en la función la fecha de un objeto y la instancia de la fecha actual
     *
     * @param fecha fecha del objeto que se le introduce para conocer el tiempo transcurrido.
     * @return el tiempo transcurrido en minutos entre las 2 fechas.
     */

    public static int tiempoTranscurrido(Fecha fecha) {

        Fecha fechaActual = new Fecha();

        LocalDateTime dateFechaIntroducida = LocalDateTime.of(fecha.getAnnoInteger(), fecha.getMesInteger(), fecha.getDiaInteger(), fecha.getHoraInteger(), fecha.getMinutosInteger());
        LocalDateTime dateFechaActual = LocalDateTime.of(fechaActual.getAnnoInteger(), fechaActual.getMesInteger(), fechaActual.getDiaInteger(), fechaActual.getHoraInteger(), fechaActual.getMinutosInteger());

        long segundos = ChronoUnit.MINUTES.between(dateFechaIntroducida, dateFechaActual);
        return (int) segundos;
    }

    /**
     * Función estática que ordena el chat en ChatFragment, recoge el primer mensaje de cada usuario con el que se ha chateado y su mensaje y se crea un objeto nuevo ListaMensajesChat
     *
     * @param listaUsuarios lista de usuarios con los que se han chateado.
     * @param listaMensajes lista de todos los mensajes recibidos.
     * @return devuelve una lista de ListaMensajesChat sin duplicidad de usuarios y mensajes donde el objeto ListaMensajesChat contiene el usuario y el mensaje más reciente.
     */

    public static List<ListaMensajesChat> ordernarChat(List<Usuario> listaUsuarios, List<Mensaje> listaMensajes) {

        HashMap<String, Usuario> usuarioHashMap = new HashMap<>();
        List<ListaMensajesChat> lista = new ArrayList<>();

        if (listaUsuarios != null && listaMensajes != null) {

            for (Usuario usuario : listaUsuarios) {

                for (Mensaje mensaje : listaMensajes) {

                    if (!usuarioHashMap.containsKey(usuario.getId()) && (mensaje.getReceptor().contentEquals(usuario.getId()) || mensaje.getEmisor().contentEquals(usuario.getId()))) {
                        usuarioHashMap.put(usuario.getId(), usuario);
                        lista.add(new ListaMensajesChat(usuario, mensaje));
                    }

                }
            }
        }
        return lista;
    }
}