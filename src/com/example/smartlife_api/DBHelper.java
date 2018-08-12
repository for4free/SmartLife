package com.example.smartlife_api;

import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
  
public class DBHelper extends SQLiteOpenHelper {  
      
    private static final int VERSION = 1;  
      
    public DBHelper(Context context, String name, CursorFactory factory,  
            int version) {  
        super(context, name, factory, version);  
        // TODO Auto-generated constructor stub  
    }  
    public DBHelper(Context context, String name) {  
        this(context, name, VERSION);  
        // TODO Auto-generated constructor stub  
    }  
    public DBHelper(Context context, String name, int version) {  
        this(context, name, null, version);  
        // TODO Auto-generated constructor stub  
    }  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        // TODO Auto-generated method stub  
        System.out.println("create a database");  
        db.execSQL("create table user(id int, name varchar(20))");  
    }  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // TODO Auto-generated method stub  
        System.out.println("update a database");  
    }  
      
  
}  