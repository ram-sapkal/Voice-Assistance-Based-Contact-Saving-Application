package com.example.contactlist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.contactlist.EditContact;
import com.example.contactlist.model.Contact;
import com.example.contactlist.model.GetName;
import com.example.contactlist.parameters.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    public MyDBHandler(Context context){
        super(context, Parameters.DB_NAME,null,Parameters.BD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE " + Parameters.TABLE_NAME + "("
                + Parameters.KEY_ID + " INTEGER PRIMARY KEY," + Parameters.KEY_NAME
                + " TEXT, " + Parameters.KEY_PHONE + " TEXT, " + Parameters.KEY_EMAIL + " TEXT, " + Parameters.KEY_PICTURE + " TEXT " + ")";
        Log.d("ram","creste table query running "+create);
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addContacts(Contact contact){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Parameters.KEY_NAME,contact.getName());
        values.put(Parameters.KEY_PHONE,contact.getPhoneNumber());
        values.put(Parameters.KEY_EMAIL,contact.getEmail());
        values.put(Parameters.KEY_PICTURE,contact.getPicture());

        db.insert(Parameters.TABLE_NAME,null,values);
        Log.d("ram"," successfully inserted ");
        db.close();
    }

    public void updateContact1(Contact contact){
        try{
/*            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Parameters.KEY_NAME, contact.getName());
            values.put(Parameters.KEY_PHONE, contact.getPhoneNumber());
            values.put(Parameters.KEY_EMAIL, contact.getEmail());
            db.update(Parameters.TABLE_NAME, values, "phone_number=?", new String[]{String.valueOf(contact.getPhoneNumber())});
            db.close();*/

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Parameters.KEY_NAME, contact.getName());
            values.put(Parameters.KEY_PHONE, contact.getPhoneNumber());
            values.put(Parameters.KEY_EMAIL, contact.getEmail());
            db.update(Parameters.TABLE_NAME, values, Parameters.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
            db.close();
        }catch(Exception e){
            Log.d("ram",e.getMessage());
        }

    }
    public void updateContact2(Contact contact){
        try{

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Parameters.KEY_PICTURE, contact.getPicture());
            db.update(Parameters.TABLE_NAME, values, Parameters.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
            db.close();
        }catch(Exception e){
            Log.d("ram",e.getMessage());
        }

    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Parameters.TABLE_NAME, Parameters.KEY_ID + "=" + id, null);
    }

    public List<Contact> getAllContactsNames(String name){
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        name=name+"%";
        String select = "SELECT * FROM " +Parameters.TABLE_NAME+ " WHERE "+Parameters.KEY_NAME+ " Like '"+name+"'";
        Cursor cursor = db.rawQuery(select, null);

        //Loop through now
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setPicture(cursor.getString(4));
                contactList.add(contact);
            }while(cursor.moveToNext());
        }
        return contactList;
    }

    public List<Contact> getAllContacts(){
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Generate the query to read from the database
        //String select = "SELECT * FROM " +Parameters.TABLE_NAME;
        String select = "SELECT * FROM " +Parameters.TABLE_NAME+ " ORDER BY "+Parameters.KEY_NAME+ " ASC";
        Cursor cursor = db.rawQuery(select, null);

        //Loop through now
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setPicture(cursor.getString(4));
                contactList.add(contact);
            }while(cursor.moveToNext());
        }
        return contactList;
    }
}
