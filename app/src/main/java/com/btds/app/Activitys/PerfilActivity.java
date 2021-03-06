package com.btds.app.Activitys;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    EditText descripcion;
    TextView usuario, nTelefono, verificado, email;
    //SwitchMaterial T2Aoption;
    Usuario usuarioActual;
    Button imageButton;
    Toolbar toolbar;
    Button masAjustesButton;
    ImageButton imageButtonCheck;

    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference referenceUserDataBase, mainDatabasePath;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

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
        email = findViewById(R.id.usuarioemail);
        masAjustesButton = findViewById(R.id.ajustes_button);
        imageButtonCheck = findViewById(R.id.check_azul);

        //T2Aoption = findViewById(R.id.perfilT2Aoption);

        imageButton = findViewById(R.id.imagenButton);
        //Superpongo un boton encima de la imagen del perfil, al ser pulsao salta a la  actividad para insertar una nueva imagen
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        //cambiarPassword = findViewById(R.id.cambiarContraseña);

        referenceUserDataBase = Funciones.getUsersDatabaseReference();
        mainDatabasePath = Funciones.getDatabaseReference();

        //storage = FirebaseStorage.getInstance();
        storageReference = Funciones.getFirebaseStorageReference();

        imageButtonCheck.setOnClickListener(v -> {
            usuarioActual.setDescripcion(descripcion.getText().toString());
            Funciones.getUsersDatabaseReference().child(usuarioActual.getId()).setValue(usuarioActual);
            Toast.makeText(PerfilActivity.this, getResources().getString(R.string.cambiosGuardados), Toast.LENGTH_LONG).show();
            descripcion.clearFocus();
            imageButtonCheck.setClickable(false);
            imageButtonCheck.setVisibility(View.GONE);
        });

        masAjustesButton.setOnClickListener(v -> {
            Intent intentAjustes = new Intent(PerfilActivity.this, AjustesActivity.class);
            intentAjustes.putExtra("usuarioAjustes", usuarioActual);
            startActivity(intentAjustes);
        });

        setOnFocusChangeListener(descripcion);

        assert firebaseUser != null;
        referenceUserDataBase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioActual = dataSnapshot.getValue(Usuario.class);

                if(usuarioActual !=null){
                    usuario.setText(usuarioActual.getUsuario());

                    if(usuarioActual.getImagenURL().equals("default")){
                        //Se cambiara de manera a un resource mas adelante
                        //String URLdefault = Constantes.default_image_profile;
                        Glide.with(getApplicationContext()).load(R.drawable.default_user_picture).into(imagen_perfil);
                    }else{
                        Glide.with(getApplicationContext()).load(usuarioActual.getImagenURL()).into(imagen_perfil);
                    }

                    descripcion.setText(usuarioActual.getDescripcion());

                    if(usuarioActual.getTelefono().isEmpty()){
                        nTelefono.setText(getResources().getString(R.string.noVerificado));
                        verificado.setText(getResources().getString(R.string.noVerificado));
                        //T2Aoption.setActivated(false);
                    }else{
                        nTelefono.setText(usuarioActual.getTelefono());
                        //T2Aoption.setActivated(true);
                        verificado.setGravity(Gravity.NO_GRAVITY);
                        if(usuarioActual.getTwoAunthenticatorFactor()){
                            verificado.setText(getResources().getString(R.string.VerificadoConT2A));
                            //T2Aoption.setChecked(true);
                        }else{
                            verificado.setText(getResources().getString(R.string.VerificadoSinT2A));
                            //T2Aoption.setChecked(false);
                        }
                    }
                    email.setText(firebaseUser.getEmail());

                    Funciones.actualizarConexion(getResources().getString(R.string.online), usuarioActual);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButton.setOnClickListener(v -> abrirCamara());

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
        storageReference = Funciones.getFirebaseStorageReference();

        StorageReference storageUserProfileRef = storageReference.child("Imagenes/Perfil/"+ usuarioActual.getId());
        UploadTask uploadTask = storageUserProfileRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(PerfilActivity.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> storageUserProfileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            // getting image uri and converting into string
            usuarioActual.setImagenURL(downloadUrl.toString());
            referenceUserDataBase.child(usuarioActual.getId()).setValue(usuarioActual).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(PerfilActivity.this, getResources().getString(R.string.exitoSubirImagenPerfil), Toast.LENGTH_SHORT).show();
                }
            });
        }));
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

    private void setOnFocusChangeListener(TextView textView){

        try{
            textView.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){

                    descripcion.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            imageButtonCheck.setVisibility(View.VISIBLE);
                            imageButtonCheck.setClickable(true);
                        }
                    });
                }else{
                    imageButtonCheck.setVisibility(View.GONE);
                    imageButtonCheck.setClickable(false);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioActual != null){
            Funciones.actualizarConexion(getResources().getString(R.string.online),usuarioActual);
        }
    }
}
