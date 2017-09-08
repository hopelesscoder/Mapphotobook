package com.hopelesscoder.mapphotobook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dadda on 21/08/2017.
 */

public class DBhelper extends SQLiteOpenHelper{
    public static final String DBNAME="MAPPHOTOBOOK";

    public DBhelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String q="CREATE TABLE "+DatabaseStrings.TBL_NAME+
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseStrings.FIELD_TEXT+" TEXT," +
                DatabaseStrings.FIELD_LAT+" DOUBLE," +
                DatabaseStrings.FIELD_LNG+" DOUBLE," +
                DatabaseStrings.FIELD_URI+" TEXT,"+
                "UNIQUE ("+DatabaseStrings.FIELD_LAT+", "+DatabaseStrings.FIELD_LNG+", "+DatabaseStrings.FIELD_URI+"))";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {  }
}
