package com.example.mymapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int mapStyle;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private static final long MIN_TIME_UPD = 1000 * 5;
    private static final float MIN_DISTANCE_UPD = 0.0f;
    private Location myLocation;
    private double latitude, longitude;
    private static final int MY_LOC_ZOOM = 17;
    private boolean isTracking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mapStyle = mMap.MAP_TYPE_NORMAL;

        // Add a marker in Sydney and move the camera
        LatLng birth = new LatLng(32.7157, -117.1611);
        mMap.addMarker(new MarkerOptions().position(birth).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birth));
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
        mMap.setMyLocationEnabled(true);
    }

    public void changeView(View v) {
        if (mapStyle == 1) {
            mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            mapStyle = mMap.MAP_TYPE_SATELLITE;
        } else {
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            mapStyle = mMap.MAP_TYPE_NORMAL;
        }


    }

    public void track(View v) {
        //call getLocation if not tracking || turn off tracking if currently enabled - remove updates for both listeners
        if (isTracking) {
            isTracking = false;
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);
        } else {
            isTracking = true;
        }

        if (isTracking) {
            getLocation();
        }
    }

    public void clearMarkers(View v) {
        mMap.clear();
        LatLng birth = new LatLng(32.7157, -117.1611);
        mMap.addMarker(new MarkerOptions().position(birth).title("Marker in Sydney"));
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            dropAmarker(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPD, MIN_DISTANCE_UPD, this);
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

    private final LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            dropAmarker(LocationManager.GPS_PROVIDER);
            locationManager.removeUpdates(locationListenerNetwork);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            switch (status) {
                case LocationProvider.AVAILABLE:
                    Toast.makeText(MapsActivity.this, "locationListenerGPS: AVAIL", Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPD, MIN_DISTANCE_UPD, locationListenerNetwork);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPD, MIN_DISTANCE_UPD, locationListenerNetwork);
                    break;
                    /*case default:
                        break;*/
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void getLocation() {


        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
                Log.d("MyMaps", "getLocation: GPS is enabled");
            }

            //get network status (cell tower + wifi) - look for network provider in locmanage

            //update isNetworkEnabled ----- log.d

            if (!isGPSEnabled && !isNetworkEnabled) {
                //no Provider is enabled
                Log.d("MyMaps", "getLocation: no provider enabled");
            } else {
                if (isNetworkEnabled) {
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
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPD, MIN_DISTANCE_UPD, locationListenerNetwork);
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPD, MIN_DISTANCE_UPD, locationListenerGps);
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void dropAmarker(String provider) {
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
        myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null)
        {
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
        }
        LatLng userLocation = null;

        if (myLocation == null)
        {
            Toast.makeText(this, "dropAmarker is null - can't drop marker", Toast.LENGTH_SHORT);
        }
        else
        {
            userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM);

            if (provider == LocationManager.GPS_PROVIDER)
            {
                //add circles for marker
                Circle c = mMap.addCircle(new CircleOptions().center(userLocation).radius(1.5).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.RED));
                Circle c1 = mMap.addCircle(new CircleOptions().center(userLocation).radius(3.5).strokeColor(Color.RED).strokeWidth(2));
                Circle c2 = mMap.addCircle(new CircleOptions().center(userLocation).radius(5.5).strokeColor(Color.RED).strokeWidth(2));

            }
            else
            {
                Circle c = mMap.addCircle(new CircleOptions().center(userLocation).radius(1.5).strokeColor(Color.BLUE).strokeWidth(2).fillColor(Color.BLUE));
                Circle c1 = mMap.addCircle(new CircleOptions().center(userLocation).radius(3.5).strokeColor(Color.BLUE).strokeWidth(2));
                Circle c2 = mMap.addCircle(new CircleOptions().center(userLocation).radius(5.5).strokeColor(Color.BLUE).strokeWidth(2));
            }

            mMap.animateCamera(update);
        }

    }


}
