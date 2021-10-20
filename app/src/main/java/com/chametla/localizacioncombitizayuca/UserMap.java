package com.chametla.localizacioncombitizayuca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.chametla.localizacioncombitizayuca.providers.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.security.AuthProvider;
import java.util.ArrayList;
import java.util.List;

public class UserMap extends AppCompatActivity implements OnMapReadyCallback {

    Button botonCerrar;
    FirebaseAuth mAtuh;
    FirebaseUser mFirebaseUser;

    GoogleMap mMap;
    SupportMapFragment mMapFragment;

    private GeofireProvider mGeofireProvider;

    private LatLng mCurrentLatLng;

    private List<Marker> mChoferMarker = new ArrayList<>();

    private boolean mIsFirstTime = true;

    com.google.android.gms.location.LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocation;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    //Callback lines
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //Obtener locación en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                    if(mIsFirstTime){
                        mIsFirstTime = false;
                        choferesActivos();
                    }
                }
            }
        }
    };
    //Callback lines ends
///////////////////////////////ON CREATE////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this::onMapReady);

        //Toolbar Instancia
        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        */



        //Instanciar propiedades de Firebase
        mAtuh = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mGeofireProvider = new GeofireProvider();
        //Instancias boton cerrar
        botonCerrar = findViewById(R.id.bCerrar);
        //Instancia Fused Location
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


        ////////////////////Metodo de boton

        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            } //Boton cerrar sesion

            private void cerrarSesion() {
                mAtuh.signOut(); // Cierra sesión en Firebase
                Intent intent = new Intent(UserMap.this, SelectOptionLogin.class);
                startActivity(intent);
            }
        });
        ///////////////////////Fin metodo boton
    }


    ///////////////////////////////ON CREATE////////////////////////////////////////////////
    ////Revisar ubicación de combis///////
    private void choferesActivos(){
        mGeofireProvider.choferesActivos(mCurrentLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // Añadir marcador conductor
                for (Marker marker: mChoferMarker){
                    if(marker.getTag() != null){
                        if(marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }
                LatLng choferLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(choferLatLng).title("¡Combi en camino!").icon(BitmapDescriptorFactory.fromResource(R.drawable.combimark)));
                marker.setTag(key);
                mChoferMarker.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                ////Conductor desconectado
                for (Marker marker: mChoferMarker){
                    if(marker.getTag() != null){
                        if(marker.getTag().equals(key)) {
                            marker.remove();
                            mChoferMarker.remove(marker);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                ///////Actualizar posicion combi
                for (Marker marker: mChoferMarker){
                    if(marker.getTag() != null){
                        if(marker.getTag().equals(key)) {
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    ////Revisar ubicación de combis///////

    //////////////////////////7Metodo OnMap sobreescrito
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.QUALITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startlocation();

    }
    //Fin metodo OnMap/////////////////////////////////////////////////


    ///////////////////////////Cerrar Sesión
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            cerrarSesion();
        }
        return super.onOptionsItemSelected(item);
    }
    private void cerrarSesion() {
        mAtuh.signOut(); // Cierra sesión en Firebase
        Intent intent = new Intent(UserMap.this, SelectOptionLogin.class);
        startActivity(intent);
    }
    */
    ///////////////////////////////Fin Cerrar Sesión



    //Permisos ubicacion
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                    }
                }
            else {
                    checkLocationPermission();
                }
        } else {
                checkLocationPermission();
                }
    }


    /*Revisar GPS activo

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivated()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else{
            showNoGps();
        }
    }

    private void showNoGps(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activar ubicación para continuar")
                .setPositiveButton("Configuarción", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE );
                    }
                })
                .create()
                .show();
    }


    private boolean gpsActivated(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true;
        }
            return isActive;
    }*/
//FIn Permisos activos

    private void startlocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
            }
            else {
                checkLocationPermission();
            }
        }
        else{
                mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
        }

    }



    private void  checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Proporciona los permisos para continuar.").setMessage("Se requiere los permisos de ubicación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(UserMap.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();

            }
            else{
                ActivityCompat.requestPermissions(UserMap.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }

        }
    }
    //Fin codigo para permisos
}