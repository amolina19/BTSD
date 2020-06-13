import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class Main {

	static File fileJson;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		 String canonicalPath = new File(".").getCanonicalPath();
         System.out.println("Current directory path using canonical path method :- " + canonicalPath);
		
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.contains("windows")) {
			fileJson = new File("T:\\Proyectos\\Eclipse\\FirebaseBackEnd\\src\\btsd-firebase.json");
		} else {
			System.out.println(OS);
			try {
				fileJson = new File(canonicalPath+"/"+args[0]);
				
			}catch(ArrayIndexOutOfBoundsException aioobe) {
				aioobe.printStackTrace();
				System.out.println("No filename as first argument inserted, selecting default firebase-key.json in the same canonical directory");
				String directoryPath = canonicalPath+"/firebase-key.json";
				File jsonFileKey = new File(directoryPath);
				if(jsonFileKey.exists()) {
					System.out.println(directoryPath);
					fileJson = jsonFileKey;
				}
			}
		}
		
		

		FileInputStream serviceAccount = null;
		try {
			serviceAccount = new FileInputStream(fileJson);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://btsd-andstudio.firebaseio.com").build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FirebaseApp.initializeApp(options);
		
		EstadosThread estadoThread = new EstadosThread(fileJson);
		UsuariosThread usuariosThread = new UsuariosThread(fileJson);

		
		// Worker worker = new Worker(fileJson);
		Thread threadEstado = new Thread(estadoThread);
		Thread threadUsuario = new Thread(usuariosThread);
		threadUsuario.setDaemon(false);
		threadEstado.setDaemon(false);

		threadUsuario.start();
		threadEstado.start();

		threadEstado.join();
		threadUsuario.join();
		System.out.println("Starting Threads");
		
		//test(userIDFirebase);
		
		
	}
	/*
	
	public static void test(String userIDFirebase) {
		
		 DatabaseReference databaseReferenceUsuarios;
		 String fechaTest = "05 09 2020";
		 databaseReferenceUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios").child(userIDFirebase);
		 System.out.println("probando");
		 databaseReferenceUsuarios.child("fecha").push().setValue(fechaTest, null);
		 System.out.println("hecho");
		 
		 
	}
	*/

}

