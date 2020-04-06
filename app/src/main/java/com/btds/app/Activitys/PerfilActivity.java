package com.btds.app.Activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class PerfilActivity extends BasicActivity {


    ImageView imagen_perfil;
    TextView usuario;
    EditText descripcion;
    Usuario usuarioObject;
    Button imageButton;
    Button guardarButton;
    Button cambiarContraseña;
    Toolbar toolbar;

    FirebaseUser firebaseUser;
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;


    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    private Bitmap currentImage;
    private final int PICK_IMAGE_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());


        //INDISPENSABLE
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuarioCampoPerfil);
        descripcion = findViewById(R.id.descripcionCampoPerfil);

        imageButton = findViewById(R.id.imagenButton);
        //Superpongo un boton encima de la imagen del perfil, al ser pulsao salta a la  actividad para insertar una nueva imagen
        imageButton.setBackgroundColor(Color.TRANSPARENT);

        guardarButton = findViewById(R.id.guardarButton);
        cambiarContraseña = findViewById(R.id.cambiarContraseña);


        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, getApplicationContext());



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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SelectImage();

                /*
                Toast.makeText(PerfilActivity.this, "HAS SELECCIONADO LA IMAGEN", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                */

            }
        });



        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();

                if(descripcion.getText().length() > 80){
                    Toast.makeText(PerfilActivity.this, getResources().getString(R.string.descripcionGrandeError), Toast.LENGTH_LONG).show();
                }else{
                    referenceUserDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                usuarioObject = snapshot.getValue(Usuario.class);
                                if (usuario != null) {
                                    if (usuarioObject.getId().equals(firebaseUser.getUid())) {
                                        usuarioObject.setDescripcion(descripcion.getText().toString());
                                        referenceUserDataBase.child(firebaseUser.getUid()).setValue(usuarioObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(PerfilActivity.this, getResources().getString(R.string.cambiosGuardados), Toast.LENGTH_SHORT).show();
                                            }
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
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /*
    private void SelectImage() {
        Intent takeImageIntent = ImagePicker.getPickImageIntent(this);
        if (takeImageIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takeImageIntent, 5);
        }
    }
    */


    /*
    // Select Image method
    private void SelectImage()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 5);


        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,PICK_IMAGE_REQUEST );


    }
     */


    // Override onActivityResult method

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 5) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    System.out.println("TEST imagen");
                    System.out.println("RUTA IMAGEN: "+photoUri.getPath());
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    imagen_perfil.setImageBitmap(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("NO ESTA ENTRANDO");
            }
        }
    }
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        if(data !=null){
            Bitmap bitmap = ImagePicker.getBitmapFromResult(this, resultCode, data);
            if (null != bitmap && resultCode == 5) {

                imagen_perfil.setImageBitmap(bitmap);

            }
        }

         */

    }


    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(PerfilActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(PerfilActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }



        /*
        Imagen codigo storage Ref
    private static final java.util.UUID UUID = null;

    {

        FirebaseStorage storage;
        StorageReference storageReference = null;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StorageReference ref = storageReference.child("Imagenes/"
                                + UUID.randomUUID().toString());

    }
    */

}
