import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.NonNull;

import models.Funciones;
import models.Usuario;
import models.Visibilidad;

public class UsuariosThread implements Runnable {

	private static DatabaseReference databaseReferenceUsuarios;
	private HashMap<String,Usuario> usuarios = new HashMap<>();
	File fileJson;
	DatabaseReference connectedRef;
	int vecesIterado = 0;

	public UsuariosThread() {

	}

	public UsuariosThread(File file) {
		this.fileJson = file;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("User Thread started");
		while(true) {
			getUsers();
			Funciones.comprobarUsuarios(usuarios);
			//System.out.println("Lista Usuarios "+usuarios.size());
			//vecesIterado++;
			//System.out.println("Usuarios iterado "+vecesIterado);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void getUsers() {
		databaseReferenceUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

		CountDownLatch latch = new CountDownLatch(10);
		databaseReferenceUsuarios.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				usuarios.clear();
				
				//System.out.println("WORK PLS");
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					
					//System.out.println(snapshot.getChildrenCount());
					
					String id = (String) snapshot.child("id").getValue();
					String usuarioStr = (String) snapshot.child("usuario").getValue();
					String descripcion = (String) snapshot.child("descripcion").getValue();
					String estado = (String) snapshot.child("estado").getValue();
					String hora = (String) snapshot.child("hora").getValue();
					String fecha = (String) snapshot.child("fecha").getValue();
					String telefono = (String) snapshot.child("telefono").getValue();
					String imagenURL = (String) snapshot.child("imagenURL").getValue();
					Boolean T2A = (Boolean) snapshot.child("t2Aintroduced").getValue();
					Boolean twoAuntenticator = (Boolean) snapshot.child("twoAunthenticatorFactor").getValue();
					
					Visibilidad visibilidad = new Visibilidad();
					Boolean visibilidadDescripcion = (Boolean) snapshot.child("visibilidad").child("descripcion").getValue();
					Boolean visibilidadEnLinea = (Boolean) snapshot.child("visibilidad").child("enLinea").getValue();
					Boolean visibilidadFoto = (Boolean) snapshot.child("visibilidad").child("foto").getValue();
					Boolean visibilidadTelefono = (Boolean) snapshot.child("visibilidad").child("telefono").getValue();
					Boolean visibilidadUsuario = (Boolean) snapshot.child("visibilidad").child("usuario").getValue();
					
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
					
					usuarios.put(id, usuario);
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
		//System.out.println("Lista Usuarios "+usuarios.size());
	}

}
