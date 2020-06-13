package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Funciones {
	
	private static HashMap<String,Usuario> desconectarUsuarios = new HashMap<>();

	
	
	/**
     * Funcion estatica que devuelve los minutos transcurridos de una estado que se ha subido hasta la fecha actual.
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
    
    public static float obtenerDiasPasados(Usuario usuario) {

        Fecha fecha = new Fecha();
        String fechaUsuario = usuario.getFecha();
        fechaUsuario.replace(" ", "");

        int userDateDay = Integer.parseInt(fechaUsuario.substring(0, 2));
        int userDateMonth = Integer.parseInt(fechaUsuario.substring(3, 5));
        int userDateYear = Integer.parseInt(fechaUsuario.substring(6, 10));


        LocalDate dateBefore = LocalDate.of(userDateYear, userDateMonth, userDateDay);
        LocalDate dateAfter = LocalDate.of(Integer.parseInt(fecha.obtenerAnno()), Integer.parseInt(fecha.obtenerMes()), Integer.parseInt(fecha.obtenerDia()));
        long nDias = ChronoUnit.DAYS.between(dateBefore, dateAfter);

        return nDias;
    }
    
   public static int obtenerMinutosUltConexion(Usuario usuario) {
	   
       
       Fecha fechaUsuario = new Fecha();
       Fecha fechaActual = new Fecha();
       String fechaString = usuario.getHora();
       String fechaString2 = usuario.getFecha();
       
       int userDateDay = Integer.parseInt(fechaString2.replace(" ", "").substring(0, 2));
       int userDateMonth = Integer.parseInt(fechaString2.replace(" ", "").substring(3, 4));
       int userDateYear = Integer.parseInt(fechaString2.replace(" ", "").substring(4, 8));
       int dotIndex = fechaString.lastIndexOf(":");
       int userHour = Integer.parseInt(fechaString.substring(0, dotIndex));
	   int userMinute = Integer.parseInt(fechaString.substring(dotIndex+1, fechaString.length()));
	   //System.out.print("USUARIO DATOS FECHA ");
	   //System.out.print("AÑO "+userDateYear);
	   //System.out.print(" MES "+userDateMonth);
	   //System.out.print(" DIA "+userDateDay);
	   //System.out.print(" HORA "+userHour);
	   //System.out.println(" MINUTO "+userMinute);
       
       
	   fechaUsuario.setDiaInteger(userDateDay);
	   fechaUsuario.setMesInteger(userDateMonth);
	   fechaUsuario.setAnnoInteger(userDateYear);
	   fechaUsuario.setHoraInteger(userHour);
	   fechaUsuario.setMinutosInteger(userMinute);
       
       
       LocalDateTime dateBefore = LocalDateTime.of(fechaUsuario.getAnnoInteger(), fechaUsuario.getMesInteger(), fechaUsuario.getDiaInteger(), fechaUsuario.getHoraInteger(), fechaUsuario.getMinutosInteger());
       LocalDateTime dateAfter = LocalDateTime.of(fechaActual.getAnnoInteger(), fechaActual.getMesInteger(), fechaActual.getDiaInteger(), fechaActual.getHoraInteger(), fechaActual.getMinutosInteger());
       long minutos = ChronoUnit.MINUTES.between(dateBefore, dateAfter);
       int minutosTranscurridos = (int) minutos;
       //System.out.println("MINUTOS TRANSCURRIDOS "+minutosTranscurridos);
       //System.out.println("########################################################");
       return minutosTranscurridos;
	   
   }
   
   public static DatabaseReference getDatabaseReference() {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
       return databaseReference;
   }
   
   public static DatabaseReference getUsersDatabaseReference() {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
       return databaseReference;
   }
   
   public static DatabaseReference getEstadosDatabaseReference() {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Estados");
       return databaseReference;
   }
   
   public static void comprobarUsuarios(HashMap<String,Usuario> listaUsuariosMap) {
		//System.out.println("Lista Usuarios HASHMAP "+listaUsuariosMap.size());
		
		try {
			desconectarUsuarios.clear();
			Collection<Usuario> values = listaUsuariosMap.values();
			ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>(values);
			
			
			for(Usuario usuario:listaUsuarios) {
				  
				  //System.out.println(usuario.getUsuario());
				  int minutosTranscurridos = obtenerMinutosUltConexion(usuario);
				  int minutosDesconectar = 5;
				  
				  if(minutosTranscurridos >= minutosDesconectar) {
					  if((minutosTranscurridos >= minutosDesconectar) && usuario.getEstado().contentEquals("Online") || usuario.getEstado().contentEquals("En Linea")) {
					  		 if(usuario.getEstado().contentEquals("Offline")) {
								   usuario.setEstado("Offline");
							   }else {
								   usuario.setEstado("Desconectado");
							   }
					  		 desconectarUsuarios.put(usuario.getId(),usuario);
					  	 }
				  }
			}
			
		}catch(ConcurrentModificationException cme) {
			cme.printStackTrace();
			System.err.println("Error al iterar y cambiar los datos en ejecución");
		}
	
		desconectarUsuarios(desconectarUsuarios);
		
   }
  
   
   public static void desconectarUsuarios(HashMap<String,Usuario> listaUsuariosDesconectar) {
	   
	   Collection<Usuario> values = listaUsuariosDesconectar.values();
	   
	   ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>(values);
	   
	   for(Usuario usuario:listaUsuarios) {
		   
		   Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario, null);
		   System.out.println("Desconectado a "+usuario.getUsuario()+" por llevar mas de 5 minutos sin recibir datos");
	   }
   }
   
   public static void borrarEstado(Estados estado) {
	   
	   if(Funciones.obtenerMinutosSubida(estado) >= 1500) {
		   Funciones.getEstadosDatabaseReference().child(estado.getKey()).removeValue(null);
		   System.out.println("Borrando un estado por estar más de 24 h propiedad de "+estado.getUsuario().getUsuario());
	   }
   }
}
