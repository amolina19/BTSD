package com.btds.app.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;


/**
 * @author Alejandro Molina Louchnikov
 */
public class PerfilActivity extends BasicActivity {


    CircleImageView imagen_perfil;
    TextView usuario;
    EditText descripcion;
    Usuario usuarioObject;
    Button imageButton;
    Button guardarButton;
    Button cambiarPassword;
    Toolbar toolbar;

    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;


    FirebaseStorage storage;
    StorageReference storageReference;

    //private Uri filePath;
    //private Bitmap currentImage;
    //private final int PICK_IMAGE_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());


        //INDISPENSABLE
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());


        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuarioCampoPerfil);
        descripcion = findViewById(R.id.descripcionCampoPerfil);

        imageButton = findViewById(R.id.imagenButton);
        //Superpongo un boton encima de la imagen del perfil, al ser pulsao salta a la  actividad para insertar una nueva imagen
        imageButton.setBackgroundColor(Color.TRANSPARENT);

        guardarButton = findViewById(R.id.guardarButton);
        cambiarPassword = findViewById(R.id.cambiarContraseña);


        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUserDataBase = getInstance().getReference("Usuarios");
        mainDatabasePath = getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        referenceUserDataBase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);

                if(usuarioObject !=null){
                    usuario.setText(usuarioObject.getUsuario());

                    if(usuarioObject.getImagenURL().equals("default")){
                        imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        Glide.with(getApplicationContext()).load(usuarioObject.getImagenURL()).into(imagen_perfil);
                    }

                    descripcion.setText(usuarioObject.getDescripcion());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imageButton.setOnClickListener(v -> ImagePicker.create(PerfilActivity.this).returnMode(ReturnMode.GALLERY_ONLY).single().start());
        cambiarPassword.setOnClickListener(v -> {
            //cambiar contraseña
            System.out.println("PASSWORD CHANGE");
        });
        guardarButton.setOnClickListener(v -> {

            if(descripcion.getText().length() > 80){
                Toast.makeText(PerfilActivity.this, getResources().getString(R.string.descripcionGrandeError), Toast.LENGTH_LONG).show();
            }else{
                referenceUserDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            usuarioObject = snapshot.getValue(Usuario.class);
                            if (usuario != null) {
                                assert usuarioObject != null;
                                if (usuarioObject.getId().equals(firebaseUser.getUid())) {
                                    usuarioObject.setDescripcion(descripcion.getText().toString());
                                    referenceUserDataBase.child(firebaseUser.getUid()).setValue(usuarioObject).addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PerfilActivity.this, getResources().getString(R.string.cambiosGuardados), Toast.LENGTH_SHORT).show();
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
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Log.d("DEBUG PerfilActivity","IMAGEN PATH "+image.getPath());
            Toast.makeText(this, getResources().getString(R.string.actualizandoImagenPerfil), Toast.LENGTH_SHORT).show();
            subirImagen(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void subirImagen(Image image){

        Uri file = Uri.fromFile(new File(image.getPath()));

        FirebaseStorage storage;
        StorageReference storageReference;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StorageReference storageRef = storageReference.child("Imagenes/Perfil/"+usuarioObject.getId());
        UploadTask uploadTask = storageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("DEBUG PerfilActivity","LA IMAGEN NO SE HA SUBIDO");
            Toast.makeText(PerfilActivity.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                // getting image uri and converting into string
                usuarioObject.setImagenURL(downloadUrl.toString());
                referenceUserDataBase.child(usuarioObject.getId()).setValue(usuarioObject).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("DEBUG PerfilActivity","Se ha Actualizado la foto de perfil");
                        Toast.makeText(PerfilActivity.this, getResources().getString(R.string.exitoSubirImagenPerfil), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            Log.d("DEBUG PerfilActivity","La imagen se ha subido al perfil de "+usuarioObject.getUsuario());
        });
    }
}
