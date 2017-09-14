package com.hopelesscoder.mapphotobook;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 13;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 14;
    private static final int MY_PERMISSIONS_RESTORE = 15;
    private static final int MY_PERMISSIONS_DELETE_DATABASE = 16;
    private int PICK_DB_REQUEST = 2;
    private GoogleMap mMap;
    private DbManager db = null;
    private DBhelper dbhelper;

    Dialog d;
    Button bOk;
    EditText dialogEditText;
    MenuItem backup;

    LatLng now;



    private static final String TAG = "MapsActivity";

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        //toolbar.setTitle("Map photo book");
        //this.setSupportActionBar(toolbar);

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

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    onOptionsItemSelected(backup);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_RESTORE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("*/*");
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select db file"), PICK_DB_REQUEST);


                }else{
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        backup = (MenuItem) menu.findItem(R.id.backup);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {

            case R.id.backup:
                //final String inFileName = "/data/data/<your.app.package>/databases/foo.db";
                final String inFileName = getDatabasePath(DBhelper.DBNAME).getPath();
                File dbFile = new File(inFileName);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(dbFile);

                    String directory = Environment.getExternalStorageDirectory()+"/mapphotobook";
                    File myDir = new File(directory);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.


                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                            //Toast.makeText(this, "Permission is needed to get your position", Toast.LENGTH_LONG).show();


                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }

                    }else if (!myDir.exists() && !myDir.isDirectory()){
                        if (myDir.mkdirs()){
                            //Toast.makeText(this,"dir created",Toast.LENGTH_LONG).show();
                            myDir.setReadable(true, false);
                            myDir.setWritable(true, false);
                            MediaScannerConnection.scanFile(this, new String[] {myDir.toString()}, null, null);


                            //String outFileName = Environment.getExternalStorageDirectory()+"/"+DBhelper.DBNAME+"_copy";
                            String outFileName = directory+"/"+DBhelper.DBNAME+"_copy";
                            MediaScannerConnection.scanFile(this, new String[] {outFileName}, null, null);

                            // Open the empty db as the output stream
                            OutputStream output = new FileOutputStream(outFileName);

                            // Transfer bytes from the inputfile to the outputfile
                            byte[] buffer = new byte[1024];
                            int length;


                            while ((length = fis.read(buffer))>0){
                                output.write(buffer, 0, length);
                            }

                            // Close the streams
                            output.flush();
                            output.close();
                            fis.close();
                        }else{
                            Toast.makeText(this,"unable to create dir, permission needed",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //Toast.makeText(this,"dir already exists",Toast.LENGTH_LONG).show();


                        //String outFileName = Environment.getExternalStorageDirectory()+"/"+DBhelper.DBNAME+"_copy";
                        String outFileName = directory+"/"+DBhelper.DBNAME+"_copy";
                        MediaScannerConnection.scanFile(this, new String[] {outFileName}, null, null);

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(outFileName);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;


                        while ((length = fis.read(buffer))>0){
                            output.write(buffer, 0, length);
                        }

                        // Close the streams
                        output.flush();
                        output.close();
                        fis.close();
                    }
                    //Toast.makeText(this,myDir.toString(),Toast.LENGTH_LONG).show();




                } catch (FileNotFoundException e) {
                    e.printStackTrace();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.restore:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.


                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_RESTORE);

                        //Toast.makeText(this, "Permission is needed to get your position", Toast.LENGTH_LONG).show();


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_RESTORE);
                    }

                }else {

                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("*/*");
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select db file"), PICK_DB_REQUEST);
                }
                return true;

            case R.id.delete_database:

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Delete database")
                        .setMessage("Are you sure you want to compeltely delete the database of all the positions?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String inFileName = getDatabasePath(DBhelper.DBNAME).getPath();
                                File dbFile = new File(inFileName);
                                if (dbFile.delete()){
                                    Toast.makeText(getApplicationContext(),"db deleted",Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                    finish();
                                    startActivity(i);
                                }else {
                                    Toast.makeText(getApplicationContext(),"Something goes wrong,db isn't deleted",Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DB_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            try {
                getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //final String inFileName = data.getData().getPath();
                //File dbFile = new File(inFileName);
                FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(data.getData());
                //fis = new FileInputStream(dbFile);

                String outFileName = getDatabasePath(DBhelper.DBNAME).getPath();
                File delDb = new File(outFileName);
                delDb.delete();

                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(outFileName);

                // Transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;


                while ((length = fis.read(buffer))>0){
                    output.write(buffer, 0, length);
                }

                // Close the streams
                output.flush();
                output.close();
                fis.close();

                Intent i = new Intent(this, MapsActivity.class);
                finish();
                startActivity(i);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
