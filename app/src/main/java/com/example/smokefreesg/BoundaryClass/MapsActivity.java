package com.example.smokefreesg.BoundaryClass;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.smokefreesg.ControllerClass.MapsController;
import com.example.smokefreesg.EntityClass.SmokingDSA;
import com.example.smokefreesg.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private GoogleMap mMap;
    private ArrayList<LatLng> locations = new ArrayList<LatLng>();
    private ArrayList<String> loc_info = new ArrayList<String>();

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button help;
    private Button back;
    private SearchView searchView;
    private Button pin_button;
    private FloatingActionButton currloc_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchView = findViewById(R.id.sv_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    MapsController.search(MapsActivity.this, mMap, addressList, location);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        help = findViewById(R.id.help_button);
        help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpDialog helpDialog = new HelpDialog();
                helpDialog.show(getSupportFragmentManager(), "ReportDialog");
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
//                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                            mMap.setMyLocationEnabled(true);
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                            mMap.setOnMarkerClickListener(this);
//                        }
                    }
                };
            }
        } else {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
            };
        }

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMarkerClickListener(this);
        }

        FloatingActionButton currloc_button = (FloatingActionButton) findViewById(R.id.currloc_button);
        currloc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                }
                else {
                    Location selfLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (selfLocation == null){
                        LatLng selfLoc= new LatLng(1.3521,103.8198);
                    }
                    LatLng selfLoc = new LatLng(selfLocation.getLatitude(), selfLocation.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(selfLoc, 15);
                    googleMap.moveCamera(update);
                }
            }
        });



        Button pin_button = (Button) findViewById(R.id.pin_button);
        pin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Click on the map to pin a location.", Toast.LENGTH_SHORT).show();
                mMap.setOnMapClickListener(MapsActivity.this);
            }
        });

        MapsController.loadOfficialDsa(MapsActivity.this, mMap, locations, loc_info);
        MapsController.loadUserDsa(mMap, db, locations, loc_info);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        BottomSheetModal bottomSheet = new BottomSheetModal();
        Bundle args = new Bundle();
        args.putString("loc_info", marker.getTitle());
        args.putDoubleArray("coordinates", new double[] {marker.getPosition().latitude, marker.getPosition().longitude});
        bottomSheet.setArguments(args);
        bottomSheet.show(getSupportFragmentManager(), "BottomSheetModal");
        return false;
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        final EditText inputLocName = new EditText(this);
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Give the location a name:")
                .setMessage("Please make sure that this is a legal location")
                .setView(inputLocName)
                .setPositiveButton("Pin!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(String.valueOf(inputLocName.getText())));
                        String verify = "no";
                        SmokingDSA smokingPoint = new SmokingDSA(latLng.latitude, latLng.longitude, String.valueOf(inputLocName.getText()),verify);
                        MapsController.writeToDb(db, smokingPoint.dsaToDb(), String.valueOf(inputLocName.getText()));
                    }
                })
                .setNegativeButton("Not legal", null)
                .show();

        mMap.setOnMapClickListener(null);
    }
}