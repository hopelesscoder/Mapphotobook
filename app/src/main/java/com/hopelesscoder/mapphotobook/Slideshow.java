package com.hopelesscoder.mapphotobook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hopelesscoder.mapphotobook.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Slideshow extends AppCompatActivity implements View.OnClickListener {

    ImageButton previous;
    ImageButton next;
    private int PICK_IMAGE_REQUEST = 1;
    List<Uri> myList;
    Uri imageNow = null;

    private DbManager db=null;
    //private CursorAdapter adapter;

    Bundle extra;
    String testo;
    double lat;
    double lng;

    private DBhelper dbhelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Calendar cal = Calendar.getInstance();
        //int month = cal.get(Calendar.MONTH)+1;
        //toolbar.setTitle(cal.get(Calendar.DAY_OF_MONTH)+"-"+ month+"-"+cal.get(Calendar.YEAR));
        extra = getIntent().getExtras();
        toolbar.setTitle(extra.getString("testo","null"));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        previous = (ImageButton) findViewById(R.id.imageButtonPrevious);
        previous.setOnClickListener(this);
        next = (ImageButton) findViewById(R.id.imageButtonNext);
        next.setOnClickListener(this);
        myList = new ArrayList<Uri>();
        //myList.size();



        db=new DbManager(this);



        testo = extra.getString("testo","null");
        lat = extra.getDouble("lat");
        lng = extra.getDouble("lng");



        dbhelper = new DBhelper(this);
        SQLiteDatabase db1 = dbhelper.getReadableDatabase();
        /*Cursor crs = db1.rawQuery("SELECT DISTINCT uri, lat, lng FROM Photobook WHERE lat = ? AND lng = ?",
                new String[]{String.valueOf(lat), String.valueOf(lng)});
        while (crs.moveToNext()) {
            if (crs != null && crs.getCount() > 0 && crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_URI)) != null) {
                Uri uri = Uri.parse(crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_URI)));
                int index;
                if (myList.size() > 0) {
                    index = myList.size();
                } else {
                    index = 0;
                }
                myList.add(index, uri);
                imageNow = uri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                    PhotoView photoView = (PhotoView) findViewById(R.id.photoView);
                    photoView.setImageBitmap(bitmap);
                    //salva();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        crs.close();
        */
        Cursor crs = db1.rawQuery("SELECT DISTINCT uri, lat, lng FROM Photobook WHERE lat LIKE ? AND lng LIKE ?",
                new String[]{String.valueOf(lat).substring(0,String.valueOf(lat).length()-5).concat("%"),
                        String.valueOf(lng).substring(0,String.valueOf(lng).length()-5).concat("%")});
        while (crs.moveToNext()) {
            if (crs != null && crs.getCount() > 0 && crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_URI)) != null) {
                Uri uri = Uri.parse(crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_URI)));
                int index;
                if (myList.size() > 0) {
                    index = myList.size();
                } else {
                    index = 0;
                }
                myList.add(index, uri);
                imageNow = uri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                    PhotoView photoView = (PhotoView) findViewById(R.id.photoView);
                    photoView.setImageBitmap(bitmap);
                    //salva();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        crs.close();

        if (myList.size()>1){
            previous.setClickable(true);
        }else {
            previous.setClickable(false);
        }
        next.setClickable(false);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageButtonPrevious){
            PhotoView image = (PhotoView) findViewById(R.id.photoView);
            //image.setImageResource(R.drawable.mail);
            //previous.setClickable(false);
            //next.setClickable(true);

            if(myList.indexOf(imageNow)>=1){
                Uri temp = myList.get(myList.indexOf(imageNow)-1);
                Bitmap bitmap = null;
                imageNow = temp;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            next.setClickable(true);
            if (myList.indexOf(imageNow)==0){
                previous.setClickable(false);
            }

            Cursor crs=db.query();
            //crs.moveToLast();
            //double lat1 = crs.getDouble(crs.getColumnIndex(DatabaseStrings.FIELD_LAT));
            //double lng1 = crs.getDouble(crs.getColumnIndex(DatabaseStrings.FIELD_LNG));

            //Toast.makeText(this,"Lat = "+lat+", lng = "+lng,Toast.LENGTH_LONG).show();
        }else{
            PhotoView image = (PhotoView) findViewById(R.id.photoView);
            //image.setImageResource(R.drawable.mail1);
            //previous.setClickable(true);
            //next.setClickable(false);

            if(myList.indexOf(imageNow)<myList.size()-1){
                Uri temp = myList.get(myList.indexOf(imageNow)+1);
                Bitmap bitmap = null;
                imageNow = temp;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            previous.setClickable(true);
            if (myList.indexOf(imageNow)==myList.size()-1){
                next.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_slideshow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PhotoView image = (PhotoView) findViewById(R.id.photoView);
        SQLiteDatabase db1 = dbhelper.getReadableDatabase();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_photo:
                Cursor crs = db1.rawQuery("SELECT DISTINCT _id, uri, lat, lng FROM Photobook WHERE lat LIKE ? AND lng LIKE ? " +
                                "AND uri = ?",
                        new String[]{String.valueOf(lat).substring(0,String.valueOf(lat).length()-5).concat("%"),
                                String.valueOf(lng).substring(0,String.valueOf(lng).length()-5).concat("%"), imageNow.toString()});
                while (crs.moveToNext()) {
                    if (crs != null && crs.getCount() > 0 && crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_URI)) != null) {
                        int id = crs.getInt(crs.getColumnIndex(DatabaseStrings.FIELD_ID));
                        db.delete(id);
                    }
                }
                crs.close();

                if(myList.indexOf(imageNow)>=1){
                    Uri temp = myList.get(myList.indexOf(imageNow)-1);
                    Bitmap bitmap = null;
                    myList.set(myList.indexOf(imageNow),null);
                    imageNow = temp;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                        image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (myList.indexOf(imageNow)==0){
                        previous.setClickable(false);
                    }
                }else {
                    image.setImageBitmap(null);
                }


                return true;
            case R.id.delete_all:

                Cursor crs1 = db1.rawQuery("SELECT DISTINCT _id, uri, lat, lng FROM Photobook WHERE lat LIKE ? AND lng LIKE ? ",
                        new String[]{String.valueOf(lat).substring(0,String.valueOf(lat).length()-5).concat("%"),
                                String.valueOf(lng).substring(0,String.valueOf(lng).length()-5).concat("%")});
                while (crs1.moveToNext()) {
                    if (crs1 != null && crs1.getCount() > 0 && crs1.getString(crs1.getColumnIndex(DatabaseStrings.FIELD_URI)) != null) {
                        int id = crs1.getInt(crs1.getColumnIndex(DatabaseStrings.FIELD_ID));
                        db.delete(id);
                    }
                }
                crs1.close();

                imageNow = null;
                image.setImageBitmap(null);
                myList.clear();
                previous.setClickable(false);
                next.setClickable(false);

                return true;
            case R.id.delete_point:

                Cursor crs2 = db1.rawQuery("SELECT DISTINCT _id, uri, lat, lng FROM Photobook WHERE lat LIKE ? AND lng LIKE ? ",
                        new String[]{String.valueOf(lat).substring(0,String.valueOf(lat).length()-5).concat("%"),
                                String.valueOf(lng).substring(0,String.valueOf(lng).length()-5).concat("%")});
                while (crs2.moveToNext()) {
                    if (crs2 != null && crs2.getCount() > 0) {
                        int id = crs2.getInt(crs2.getColumnIndex(DatabaseStrings.FIELD_ID));
                        db.delete(id);
                    }
                }
                crs2.close();

                Intent i = new Intent(this, MapsActivity.class);
                finish();
                startActivity(i);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            //this.grantUriPermission("com.example.dadda.mapphotobook", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(!myList.contains(uri)) {
                int index;
                if (myList.size() > 0) {
                    index = myList.size();
                } else {
                    index = 0;
                }
                myList.add(index, uri);
                imageNow = uri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                    PhotoView photoView = (PhotoView) findViewById(R.id.photoView);
                    photoView.setImageBitmap(bitmap);
                    salva();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this,"Picture already added",Toast.LENGTH_SHORT).show();
            }

            if (myList.size()>1){
                previous.setClickable(true);
            }else {
                previous.setClickable(false);
            }
            next.setClickable(false);
        }
    }

    public void salva()
    {
        /*Bundle extra = getIntent().getExtras();
        String testo = extra.getString("testo","null");
        double lat = extra.getDouble("lat");
        double lng = extra.getDouble("lng"); */
        db.save(testo,lat,lng,imageNow);


        /*
        EditText sub=(EditText) findViewById(R.id.oggetto);
        EditText txt=(EditText) findViewById(R.id.testo);
        EditText date=(EditText) findViewById(R.id.data);
        if (sub.length()>0 && date.length()>0)
        {
            db.save(sub.getEditableText().toString(), txt.getEditableText().toString(), date.getEditableText().toString());
            adapter.changeCursor(db.query());
       }
       */
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MapsActivity.class);
        finish();
        startActivity(i);


    }
}
