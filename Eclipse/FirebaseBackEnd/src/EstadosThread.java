import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;

import models.Estados;
import models.Fecha;
import models.Funciones;
import models.Usuario;
import models.Visibilidad;

public class EstadosThread implements Runnable {

	private static DatabaseReference databaseReferenceEstados;
	private HashMap<String,Estados> estados = new HashMap<>();
	File fileJson;
	static FirebaseOptions options;
	int vecesIterado = 0;

	DatabaseReference connectedRef;

	public EstadosThread() {

	}

	public EstadosThread(File file) {
		this.fileJson = file;
	}

	@Override
	public void run() {
		
		System.out.println("Estados Thread started");
		while(true) {
			getEstados();
			//vecesIterado++;
			//System.out.println("Estados iterado "+vecesIterado);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getEstados() {
		databaseReferenceEstados = FirebaseDatabase.getInstance().getReference("Estados");

		CountDownLatch latch = new CountDownLatch(5);
		databaseReferenceEstados.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				estados.clear();
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					
					Estados estadoObject = new Estados();
					
					
					String annoEstado = (String) snapshot.child("fecha").child("anno").getValue();
					String mesEstado = (String) snapshot.child("fecha").child("mes").getValue();
					String diaEstado = (String) snapshot.child("fecha").child("dia").getValue();
					String horaEstado = (String) snapshot.child("fecha").child("hora").getValue();
					String minutosEstado = (String) snapshot.child("fecha").child("minutos").getValue();
					String segundosEstado = (String) snapshot.child("fecha").child("segundos").getValue();
					String milisegundosEstado = (String) snapshot.child("fecha").child("milisegundos").getValue();
					
					Fecha fechaEstado = new Fecha(annoEstado,mesEstado,diaEstado,horaEstado,minutosEstado,segundosEstado,milisegundosEstado);
					
					//System.out.println(fechaEstado.toString());
					estadoObject.setFecha(fechaEstado);
					
					
					String key = (String) snapshot.child("key").getValue();
					String imagenURLEstado = (String) snapshot.child("estadoURL").getValue();
					//System.out.println(key);
					//System.out.println(imagenURLEstado);					
					estadoObject.setKey(key);
					estadoObject.setEstadoURL(imagenURLEstado);
					
					
					
					String id = (String) snapshot.child("usuario").child("id").getValue();
					String usuarioStr = (String) snapshot.child("usuario").child("usuario").getValue();
					String descripcion = (String) snapshot.child("usuario").child("descripcion").getValue();
					String estado = (String) snapshot.child("usuario").child("estado").getValue();
					String hora = (String) snapshot.child("usuario").child("hora").getValue();
					String fecha = (String) snapshot.child("usuario").child("fecha").getValue();
					String telefono = (String) snapshot.child("usuario").child("telefono").getValue();
					String imagenURL = (String) snapshot.child("usuario").child("imagenURL").getValue();
					Boolean T2A = (Boolean) snapshot.child("usuario").child("t2Aintroduced").getValue();
					Boolean twoAuntenticator = (Boolean) snapshot.child("usuario").child("twoAunthenticatorFactor").getValue();
					
					
					
					Visibilidad visibilidad = new Visibilidad();
					Boolean visibilidadDescripcion = (Boolean) snapshot.child("usuario").child("visibilidad").child("descripcion").getValue();
					Boolean visibilidadEnLinea = (Boolean) snapshot.child("usuario").child("visibilidad").child("enLinea").getValue();
					Boolean visibilidadFoto = (Boolean) snapshot.child("usuario").child("visibilidad").child("foto").getValue();
					Boolean visibilidadTelefono = (Boolean) snapshot.child("usuario").child("visibilidad").child("telefono").getValue();
					Boolean visibilidadUsuario = (Boolean) snapshot.child("usuario").child("visibilidad").child("usuario").getValue();
					
					visibilidad.setDescripcion(visibilidadDescripcion);
					visibilidad.setEnLinea(visibilidadEnLinea);
					visibilidad.setFoto(visibilidadFoto);
					visibilidad.setUsuario(visibilidadUsuario);
					visibilidad.setTelefono(visibilidadTelefono);
					
					Usuario usuario = new Usuario();
					usuario.setId(id);
					usuario.setUsuario(usuarioStr);
					usuario.setDescripcion(descripcion);
					usuario.setEstado(estado);
					usuario.setHora(hora);
					usuario.setFecha(fecha);
					usuario.setTelefono(telefono);
					usuario.setImagenURL(imagenURL);
					usuario.setT2Aintroduced(T2A);
					usuario.setTwoAunthenticatorFactor(twoAuntenticator);
					usuario.setVisibilidad(visibilidad);
					
					estadoObject.setUsuario(usuario);
					
					Funciones.borrarEstado(estadoObject);
					//System.out.println(estadoObject.toString());
					estados.put(key, estadoObject);
					//
					latch.countDown();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
		try {
			latch.await(10, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Lista Estados "+estados.size());
	}

}
