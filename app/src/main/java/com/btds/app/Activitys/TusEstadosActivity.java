package com.btds.app.Activitys;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.btds.app.Adaptadores.EstadosUsuarioAdapter;
import com.btds.app.Modelos.Estados;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Alejandro Molina Louchnikov
 */

public class TusEstadosActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private List<Estados> listaTusEstados;
    GridView gridViewEstados;
    Toolbar toolbar;
    Usuario usuarioObject;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_estados);
        Log.d("DEBUG ","TusEstadosActivity Created");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tus Estados");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        assert firebaseUser != null;
        Funciones.getActualUserDatabaseReference(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioObject = dataSnapshot.getValue(Usuario.class);
                assert usuarioObject != null;
                //Funciones.mostrarDatosUsuario(usuarioObject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listaTusEstados = new ArrayList<>();
        Funciones.getEstadosDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTusEstados.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Estados estado = data.getValue(Estados.class);
                    assert estado != null;
                    if(estado.getUsuario().getId().contentEquals(firebaseUser.getUid())){
                        listaTusEstados.add(estado);
                    }
                }

                EstadosUsuarioAdapter estadosUsuarioAdapter = new EstadosUsuarioAdapter(TusEstadosActivity.this,listaTusEstados);
                gridViewEstados.setAdapter(estadosUsuarioAdapter);
                estadosUsuarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //System.out.println("TAMAÑO LISTA IMAGENES "+listaTusEstados.size());

        gridViewEstados = findViewById(R.id.gridview_tus_estados);
        gridViewEstados.setVerticalSpacing(1);
        gridViewEstados.setHorizontalSpacing(1);

        gridViewEstados.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(TusEstadosActivity.this, "HAS SELECCIONADO LA POSICION "+position, Toast.LENGTH_SHORT).show());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.estados_menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.añadir_estado_item_menu_toolbar) {
            abrirCamara();
            return true;
        }
        return false;
    }

    /*
    private List<String> cargarPrueba(){

        List<String> listImagesUrl = new ArrayList<>();
        listImagesUrl.add("https://d24jx5gocr6em0.cloudfront.net/wp-content/uploads/2020/04/03160041/negros-con-ataud-520x350.jpg");
        listImagesUrl.add("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Spain.svg/1200px-Flag_of_Spain.svg.png");
        return listImagesUrl;
    }

     */



    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Log.d("DEBUG TusEstadosActivity","IMAGEN PATH "+image.getPath());
            //Toast.makeText(this, getResources().getString(R.string.actualizandoImagenPerfil), Toast.LENGTH_SHORT).show();
            subirImagen(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void subirImagen(Image image){

        Uri file = Uri.fromFile(new File(image.getPath()));

        //FirebaseStorage storage;
        StorageReference storageReference;

        //storage = FirebaseStorage.getInstance();
        storageReference = Funciones.getFirebaseStorageReference();

        //StorageReference storageUserEstadosRef = storageReference.child("Imagenes/Perfil/"+usuarioObject.getId());

        String URLstring = Funciones.getAlphaNumericString(16);
        Log.d("DEBUG TusEstadosActivity","URL "+ URLstring);
        StorageReference storageUserEstadosRef = storageReference.child("Estados/"+usuarioObject.getId()+"/"+URLstring);
        UploadTask uploadTask = storageUserEstadosRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.d("DEBUG PerfilActivity","El Estado no se ha subido");
            //Toast.makeText(TusEstadosActivity.this, getResources().getString(R.string.errorSubirImagenPerfil), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "ERROR al subir el estado", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            storageUserEstadosRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                // getting image uri and converting into string
                //usuarioObject.setImagenURL(downloadUrl.toString());
                //Fecha fecha = new Fecha();

                Estados estado = new Estados(URLstring,downloadUrl.toString(),usuarioObject,new Fecha());
                Log.d("DEBUG TusEstadosActivity ","FECHA SUBIDA "+estado.fecha.toString());

                assert firebaseUser != null;
                Funciones.getEstadosDatabaseReference().child(new Fecha().obtenerFechaTotal()+""+Funciones.getAlphaNumericString(8)).setValue(estado).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("DEBUG TusEstadosActivity","Referencia Estado Registrado en la base de datos");
                    }
                });

                /*
                referenceUserDataBase.child(usuarioObject.getId()).setValue(usuarioObject).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("DEBUG PerfilActivity","Se ha Actualizado la foto de perfil");
                        Toast.makeText(TusEstadosActivity.this, getResources().getString(R.string.exitoSubirImagenPerfil), Toast.LENGTH_SHORT).show();
                    }
                });

                 */
            });
            Log.d("DEBUG TusEstadosActivity","El estado se ha subido "+usuarioObject.getUsuario());
        });
    }

    @AfterPermissionGranted(1)
    public void abrirCamara(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(EasyPermissions.hasPermissions(this,perms)){
            ImagePicker.create(TusEstadosActivity.this).returnMode(ReturnMode.GALLERY_ONLY).single().start();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoAbrirCamara),1,perms);
        }
        //
    }

    @Override
    public void onBackPressed() {
        //Funciones.setBackPressed();
        super.onBackPressed();
        backPressed = true;
        Intent backToMainActivity = new Intent(TusEstadosActivity.this,MainActivity.class);
        startActivity(backToMainActivity);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
