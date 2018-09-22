package com.example.sonali.task3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Sonali on 08-09-2018.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="person_db";
    public static  final String TABLE_PERSONS="person";
    public static final String  COLUMN_ID="id";
    public static final String COLUMN_NAME="name";
    public static  final String COLUMN_LOCATION="location";
    public static final String COLUMN_ROLE="role";
    public static final String COLUMN_IMAGE="image";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE "+ TABLE_PERSONS +
                " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " VARCHAR(255) NOT NULL , " +
                COLUMN_LOCATION + " VARCHAR(255) NOT NULL ," +
                COLUMN_ROLE + " VARCHAR(225) NOT NULL, "+
                COLUMN_IMAGE + " BLOB NOT NULL " + ");" ;
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS);
        onCreate(db);
    }

    public void addPeople(String name,String location,String role, byte[]image)//value passed is object ot string
    {
        ContentValues values= new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LOCATION,location);
        values.put(COLUMN_ROLE,role);
        values.put(COLUMN_IMAGE,image);
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLE_PERSONS, COLUMN_ID, values);
        db.close();
    }

    public void delete(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String query="DELETE FROM " + TABLE_PERSONS + " WHERE " + COLUMN_NAME + "=\"" + id + "\";";
        db.execSQL(query);
    }

   public Cursor getAllPersons()
   {
       SQLiteDatabase db=this.getWritableDatabase();
       String[] columns={COLUMN_NAME, COLUMN_LOCATION, COLUMN_ROLE,COLUMN_IMAGE};
       return db.query(TABLE_PERSONS,columns,null,null,null,null,null);
   }

    //Recycler View
    /*public ArrayList<persons> getData(){
        String selectQuery ="SELECT * FROM " + TABLE_PERSONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        ArrayList<persons> dataModelArrayList = new ArrayList<persons>();
        StringBuffer stringBuffer = new StringBuffer();
        persons person = null;

        if (cursor != null)
            cursor.moveToFirst();
        while(cursor.moveToNext()){
               person =new persons();
                person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                String a=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
                person.setRole(a);
                person.setLoc(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                dataModelArrayList.add(person);
                stringBuffer.append(person);

            }*


        cursor.close();
        db.close();

        return dataModelArrayList;
    }*/

    //ListView
   /*public Cursor getAllRows()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String where=null;
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_PERSONS,null);
        return c;
    }*/
}

