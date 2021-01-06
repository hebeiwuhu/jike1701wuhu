package com.example.schedule_he.ui.kecheng;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table courses(" +
                "id integer primary key autoincrement," +
                "course_name text," +
                "teacher text," +
                "class_room text," +
                "day integer," +
                "class_start integer," +
                "class_end integer," +
                "week text)");
    }
        @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table students(" +
                "id integer primary key autoincrement," +
                "student_name text," +
                "student_id text," +
                "class_room text," +
                "grade integer," +
                "sex integer," +
                "pe integer," +
                "height text," +
                "parents_phone_num text," +
                "birthday text," +
                "emergent_call_num text," +
        
                "weight integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
