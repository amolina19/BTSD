package com.btds.app.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.btds.app.Modelos.LatLng;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.R;
import com.btds.app.Utils.Fecha;
import com.btds.app.Utils.Funciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap googleMap;
    private com.google.android.gms.maps.model.LatLng actualLocation;
    private String usuarioID;
    private LatLng ubicacionCompartida;
    private Button compartirUbicacion;
    private DatabaseReference chatsReference;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment googleMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert googleMapFragment != null;
        googleMapFragment.getMapAsync(this);


        Intent intent = getIntent();

        compartirUbicacion = findViewById(R.id.compartirUbicacionButton);
        compartirUbicacion.setVisibility(View.GONE);
        compartirUbicacion.setOnClickListener(v -> {
            enviarUbicacion(firebaseUser.getUid(), usuarioID);
            onBackPressed();
        });
        preguntarPermisosLocalizacion();

        assert usuarioID != null;
        usuarioID = intent.getStringExtra("userID");
        //ubicacionStr = intent.getStringExtra("Ubicacion");
        //getIntent().getSerializableExtra("Ubicacion");
        Bundle bundle = intent.getExtras();
        ubicacionCompartida = bundle.getParcelable("Ubicacion");

    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ubicacionCompartida != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(ubicacionCompartida.getLatitude(),ubicacionCompartida.getLongitude())).title(getResources().getString(R.string.compartido)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(ubicacionCompartida.getLatitude(),ubicacionCompartida.getLongitude()), 16.0f));
            compartirUbicacion.setVisibility(View.GONE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setMinZoomPreference(1);
        googleMap.setMaxZoomPreference(20);
        Log.d("DEBUG MapsActivity", "Mapa cargado");
        //googleMap.clear();
        //googleMap.addMarker(new MarkerOptions().position(actualLocation).title(getResources().getString(R.string.ahoraMismo)));
        //googleMap.addMarker(new MarkerOptions().position(actualLocation).title(getResources().getString(R.string.ahoraMismo)));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(actualLocation));

        if(ubicacionCompartida == null){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    actualLocation = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(actualLocation.latitude,actualLocation.longitude) , 18.0f));
                    //googleMap.clear();
                    //googleMap.addMarker(new MarkerOptions().position(actualLocation).title(getResources().getString(R.string.ahoraMismo)));
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(actualLocation));
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(actualLocation));
                    //googleMap

                    compartirUbicacion.setVisibility(View.VISIBLE);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            ubicacionActual();
        }else{
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(ubicacionCompartida.getLatitude(),ubicacionCompartida.getLongitude())).title(getResources().getString(R.string.compartido)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(ubicacionCompartida.getLatitude(),ubicacionCompartida.getLongitude()), 16.0f));
            compartirUbicacion.setVisibility(View.GONE);
        }

    }

    public void ubicacionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation != null){
            actualLocation = new com.google.android.gms.maps.model.LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            Log.d("DEBUG MapsActivity","Ultima localizacion Latitud "+lastLocation.getLatitude()+" Longitud "+lastLocation.getLongitude());
            googleMap.clear();
            //googleMap.addMarker(new MarkerOptions().position(actualLocation).title(getResources().getString(R.string.ahoraMismo)));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(actualLocation.latitude,actualLocation.longitude) , 18.0f));
            compartirUbicacion.setVisibility(View.VISIBLE);
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(actualLocation));
        }

    }

    public void statusCheckGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertaNoGPS();
        }else{
            compartirUbicacion.setVisibility(View.VISIBLE);
        }
    }

    private void alertaNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.GPSAdvertencia))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Aceptar), (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(getResources().getString(R.string.Cancelar), (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarUbicacion(String emisor, String receptor){

        //Luego instanciar la fecha actual del dispositivo.
        //Fecha fecha = new Fecha();
        String id = new Fecha().obtenerFechaTotal();
        LatLng ubicacion = new LatLng(actualLocation.latitude,actualLocation.longitude);
        Mensaje mensajeObject = new Mensaje(id,emisor,receptor,ubicacion,false,new Fecha());
        chatsReference = Funciones.getChatsDatabaseReference();

        chatsReference.child(mensajeObject.getId()).setValue(mensajeObject).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("DEBUG Mensaje","Se ha enviado la localización");
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentChat = new Intent(MapsActivity.this, MessageActivity.class);
        intentChat.putExtra("userID",usuarioID);
        startActivity(intentChat);
        finish();
    }



    @AfterPermissionGranted(4)
    public void preguntarPermisosLocalizacion() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (EasyPermissions.hasPermissions(this, perms)) {
            statusCheckGPS();
        }else{
            EasyPermissions.requestPermissions(this,getResources().getString(R.string.permisoLocalizacion),4,perms);
        }
        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*
    Escalas de Zoom en metros
        20 : 1128.497220
        19 : 2256.994440
        18 : 4513.988880
        17 : 9027.977761
        16 : 18055.955520
        15 : 36111.911040
        14 : 72223.822090
        13 : 144447.644200
        12 : 288895.288400
        11 : 577790.576700
        10 : 1155581.153000
        9 : 2311162.307000
        8 : 4622324.614000
        7 : 9244649.227000
        6 : 18489298.450000
        5 : 36978596.910000
        4 : 73957193.820000
        3 : 147914387.600000
        2 : 295828775.300000
        1 : 591657550.500000
     */
}