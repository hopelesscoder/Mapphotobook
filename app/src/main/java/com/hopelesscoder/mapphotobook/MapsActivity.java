package com.hopelesscoder.mapphotobook;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hopelesscoder.mapphotobook.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 13;
    private GoogleMap mMap;
    private DbManager db = null;
    private DBhelper dbhelper;

    Dialog d;
    Button bOk;
    EditText dialogEditText;

    LatLng now;



    private static final String TAG = "MapsActivity";

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = new DbManager(this);
        dbhelper = new DBhelper(this);



        MobileAds.initialize(this, "ca-app-pub-6010001093429697~8762816921");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setPadding(0,0,0,100);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                //Toast.makeText(this, "Permission is needed to get your position", Toast.LENGTH_LONG).show();


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }


        }else {
            mMap.setMyLocationEnabled(true);
        }

        //mMap.setOnMyLocationButtonClickListener(this);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapLongClickListener(this);

        SQLiteDatabase db1 = dbhelper.getReadableDatabase();
        //Cursor crs = db1.rawQuery("SELECT lat, lng FROM Photobook",null);
        String[] columns = {DatabaseStrings.FIELD_TEXT, DatabaseStrings.FIELD_LAT, DatabaseStrings.FIELD_LNG};
        Cursor crs = db1.query(true, DatabaseStrings.TBL_NAME, columns, null, null, null, null, null, null);
        while (crs.moveToNext()) {
            if (crs != null && crs.getCount() != 0) {
                Double tempLat = crs.getDouble(crs.getColumnIndex(DatabaseStrings.FIELD_LAT));
                Double tempLng = crs.getDouble(crs.getColumnIndex(DatabaseStrings.FIELD_LNG));
                String title = crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_TEXT));
                LatLng tempLatLng = new LatLng(tempLat, tempLng);
                mMap.addMarker(new MarkerOptions().position(tempLatLng).title(title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        //.showInfoWindow();
                mMap.setOnInfoWindowClickListener(this);
            }
        }
        crs.close();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;

        now = latLng;
        d = new Dialog(this);
        d.setTitle("Select a title");
        d.setCancelable(false);
        d.setContentView(R.layout.dialog);
        bOk = (Button) d.findViewById(R.id.buttonOk);
        bOk.setOnClickListener(this);
        d.findViewById(R.id.buttonBack).setOnClickListener(this);
        dialogEditText = (EditText) d.findViewById(R.id.dialogEditText);
        dialogEditText.setText(cal.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + cal.get(Calendar.YEAR));
        d.show();


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(this, Slideshow.class);
        i.putExtra("lat", marker.getPosition().latitude);
        i.putExtra("lng", marker.getPosition().longitude);
        i.putExtra("testo", marker.getTitle());
        finish();
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonOk) {
            //Calendar cal = Calendar.getInstance();
            //int month = cal.get(Calendar.MONTH) + 1;

            mMap.addMarker(new MarkerOptions().position(now).title(dialogEditText.getText().toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    .showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            mMap.setOnInfoWindowClickListener(this);

            //SQLiteDatabase db1 = dbhelper.getReadableDatabase();
            //Cursor crs = db1.rawQuery("SELECT lat, lng FROM Photobook WHERE lat = "+latLng.latitude+"& lng ="+latLng.latitude,null);
            //if(crs.getCount() == 0){
            //String testo = cal.get(Calendar.DAY_OF_MONTH)+"-"+ month+"-"+cal.get(Calendar.YEAR);
            //db.save(testo,latLng.latitude,latLng.longitude,null);
            db.save(dialogEditText.getText().toString(), now.latitude, now.longitude, null);
            //}else{
            //    Toast.makeText(this,"marker already added",Toast.LENGTH_LONG).show();
            //}

            d.dismiss();
        } else if (v.getId() == R.id.buttonBack) {
            d.dismiss();
        }
    }

   /* @Override
    public boolean onMyLocationButtonClick() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                now = new LatLng(location.getLatitude(),location.getLongitude());
                onMapLongClick(now);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



        return false;
    }
    */


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }
}
