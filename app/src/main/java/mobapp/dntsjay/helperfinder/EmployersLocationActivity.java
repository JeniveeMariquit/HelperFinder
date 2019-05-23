package mobapp.dntsjay.helperfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.FirebaseAppHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.lang.Double.parseDouble;

public class EmployersLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GoogleMap mMap;
    public GoogleApiClient client;
    public LocationRequest request;
    public LatLng mylocation;
    public FusedLocationProviderClient fclient;

    clsUserLocation userLocation;

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employers_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                startActivity(i);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestPermission();
        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            client.connect();
        }
        if (client != null) {
            client.connect();
//            Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();
        }
        userLocation = new clsUserLocation();
        fclient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase.child("user-location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    userLocation = ds.getValue(clsUserLocation.class);
                    Double lat = parseDouble(userLocation.UserLatitude);
                    Double lng = parseDouble(userLocation.UserLongitude);
                    mylocation = new LatLng(lat,lng);
                    mMap.addMarker(new MarkerOptions().position(mylocation).title(userLocation.Uid));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
//                            Toast.makeText(EmployersLocationActivity.this, String.valueOf(marker.getTitle()),Toast.LENGTH_LONG).show();
                            Intent s = new Intent(EmployersLocationActivity.this, ApplicantProfileActivity.class);
                            s.putExtra("applicantID", String.valueOf(marker.getTitle()));
                            startActivity(s);
                            return true;
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        mylocation = new LatLng(10.3157, 123.8854);
//                    insertUserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),mylocation.toString());
//                    mMap.addMarker(new MarkerOptions().position(mylocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,14.0f));
        mMap.setMyLocationEnabled(true);
        mMap.getMaxZoomLevel();
        mMap.getUiSettings();
        mMap.getCameraPosition();
        mMap.setBuildingsEnabled(true);
        mMap.setTrafficEnabled(true);


    }


    @Override
    public void onConnected(Bundle bundle) {
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
        fclient.getLastLocation().addOnSuccessListener(EmployersLocationActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
//                    Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
                    mylocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    insertUserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),mylocation.toString());
//                    mMap.addMarker(new MarkerOptions().position(mylocation).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,14.0f));


                }
                else{
                    Toast.makeText(getApplicationContext(), "PLEASE CHECK LOCATION SETTINGS", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String [] {ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
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
        fclient.getLastLocation().addOnSuccessListener(EmployersLocationActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
//                    Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
                    mylocation = new LatLng(location.getLatitude(), location.getLongitude());
//                    insertUserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),mylocation.toString());
//                    mMap.addMarker(new MarkerOptions().position(mylocation).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                }
                else{
                    Toast.makeText(getApplicationContext(), "PLEASE CHECK LOCATION SETTINGS", Toast.LENGTH_LONG).show();
                }

            }
        });
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

    public void insertUserLocation(final String userLatitude, final String userLongitude, final String latLng){

        UserLocation ul = new UserLocation(userLatitude, userLongitude, latLng, user.getUid());
        mDatabase.child("user-location").child(user.getUid()).setValue(ul).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public static class UserLocation{
        public String UserLatitude;
        public String UserLongitude;
        public String LatLng;
        public String Uid;

        public UserLocation(){

        }

        public UserLocation(String userLatitude, String userLongitude, String latLng, String uid){
            this.UserLatitude = userLatitude;
            this.UserLongitude = userLongitude;
            this.LatLng = latLng;
            this.Uid = uid;
        }
    }


}
