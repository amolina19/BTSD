package com.btds.app.Activitys;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * @author Alejandro Molina Louchnikov
 */
public class PerfilActivity extends BasicActivity implements EasyPermissions.PermissionCallbacks {

    CircleImageView imagen_perfil;
    TextView usuario;
    EditText descripcion;
    TextView nTelefono;
    TextView verificado;
    //SwitchMaterial T2Aoption;
    Usuario usuarioObject;
    Button imageButton;
    Button guardarButton;
    Toolbar toolbar;

    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceUserDataBase;
    DatabaseReference mainDatabasePath;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Log.d("DEBUG ","PerfilActivity Created");

        //INDISPENSABLE
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        imagen_perfil = findViewById(R.id.imagen_perfil);
        usuario = findViewById(R.id.usuarioCampoPerfil);
        descripcion = findViewById(R.id.descripcionCampoPerfil);
        nTelefono = findViewById(R.id.usuarioTelefonoperfil);
        verificado = findViewById(R.id.usuarioVerificado);

        //T2Aoption = findViewById(R.id.perfilT2Aoption);

        imageButton = findViewById(R.id.imagenButton);
        //Superpongo un boton encima de la imagen del perfil, al ser pulsao salta a la  actividad para insertar una nueva imagen
        imageButton.setBackgroundColor(Color.TRANSPARENT);

        guardarButton = findViewById(R.id.guardarButton);
        //cambiarPassword = findViewById(R.id.cambiarContraseña);


        Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser);
        referenceUserDataBase = Funciones.getUsersDatabaseReference();
        mainDatabasePath = Funciones.getDatabaseReference();

        //storage = FirebaseStorage.getInstance();
        storageReference = Funciones.getFirebaseStorageReference();

        assert firebaseUser != null;
        referenceUserDataBase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);

                if(usuarioObject !=null){
                    usuario.setText(usuarioObject.getUsuario());

                    if(usuarioObject.getImagenURL().equals("default")){
                        //Se cambiara de manera a un resource mas adelante
                        //String URLdefault = Constantes.default_image_profile;
                        Glide.with(getApplicationContext()).load(R.drawable.default_user_picture).into(imagen_perfil);
                    }else{
                        Glide.with(getApplicationContext()).load(usuarioObject.getImagenURL()).into(imagen_perfil);
                    }

                    descripcion.setText(usuarioObject.getDescripcion());

                    if(usuarioObject.getTelefono().isEmpty()){
                        nTelefono.setText(getResources().getString(R.string.noVerificado));
                        verificado.setText(getResources().getString(R.string.noVerificado));
                        //T2Aoption.setActivated(false);
                    }else{
                        nTelefono.setText(usuarioObject.getTelefono());
                        //T2Aoption.setActivated(true);
                        verificado.setGravity(Gravity.NO_GRAVITY);
                        if(usuarioObject.getTwoAunthenticatorFactor()){
                            verificado.setText(getResources().getString(R.string.VerificadoConT2A));
                            //T2Aoption.setChecked(true);
                        }else{
                            verificado.setText(getResources().getString(R.string.VerificadoSinT2A));
                            //T2Aoption.setChecked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButton.setOnClickListener(v -> abrirCamara());

        /*
        cambiarPassword.setOnClickListener(v -> {
            //cambiar contraseña
            System.out.println("PASSWORD CHANGE");
        });

         */

        /*
        T2Aoption.setOnClickListener(v -> {

            if(T2Aoption.isChecked()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("AUTENTICADOR T2A");
                builder.setMessage("Deseas activar el T2A");
                builder.setNegativeButton(getResources().getString(R.string.Cancelar),(dialog, which) -> {
                    T2Aoption.setChecked(true);
                });
                builder.setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, which) -> {
                    DesignerToast.Success(PerfilActivity.this,"HAS ACTIVADO EL AUTENTICADOR EN 2 FACTORES", Gravity.CENTER, Toast.LENGTH_SHORT);

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("AUTENTICADOR T2A");
                builder.setMessage("Deseas desactivar el T2A");
                builder.setNegativeButton(getResources().getString(R.string.Cancelar),(dialog, which) -> {
                    T2Aoption.setChecked(false);
                });
                builder.setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, which) -> {

                    DesignerToast.Success(PerfilActivity.this,"HAS DESACTIVADO EL AUTENTICADOR EN 2 FACTORES", Gravity.CENTER, Toast.LENGTH_SHORT);

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

         */
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

    @AfterPermissionGranted(1)
    public void abrirCamara(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(EasyPermissions.hasPermissions(this,perms)){
            ImagePicker.create(PerfilActivity.this).returnMode(ReturnMode.GALLERY_ONLY).single().start();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoAbrirCamara),1,perms);
        }
        //
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                // or get a single image only
                Image image = ImagePicker.getFirstImageOrNull(data);
                Log.d("DEBUG PerfilActivity","IMAGEN PATH "+image.getPath());
                Toast.makeText(this, getResources().getString(R.string.actualizandoImagenPerfil), Toast.LENGTH_SHORT).show();
                subirImagen(image);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void subirImagen(Image image){

        Uri file = Uri.fromFile(new File(image.getPath()));

        //FirebaseStorage storage;
        //StorageReference storageReference;

        //storage = FirebaseStorage.getInstance();
        storageReference = Funciones.getFirebaseStorageReference();

        StorageReference storageUserProfileRef = storageReference.child("Imagenes/Perfil/"+usuarioObject.getId());
        UploadTask uploadTask = storageUserProfileRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("DEBUG PerfilActivity","LA IMAGEN NO SE HA SUBIDO");
            Toast.makeText(PerfilActivity.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            storageUserProfileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
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

    @Override
    public void onBackPressed() {
        //Funciones.setBackPressed();
        super.onBackPressed();
        Intent backToMainActivity = new Intent(PerfilActivity.this,MainActivity.class);
        startActivity(backToMainActivity);
        finish();
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
