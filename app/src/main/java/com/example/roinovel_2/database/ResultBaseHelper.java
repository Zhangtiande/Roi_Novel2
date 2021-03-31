package com.example.roinovel_2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.AUTHOR;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.LASTUPDATE;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.MAINURL;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.NAME;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.TABLENAME;

public class ResultBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Novel.db";


    public ResultBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create TABLE " + TABLENAME + "(" +
                " id integer primary key autoincrement, " +
                NAME + ", " + AUTHOR + ", " + LASTUPDATE + ", " + MAINURL + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
