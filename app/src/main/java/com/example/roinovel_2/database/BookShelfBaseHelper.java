package com.example.roinovel_2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.roinovel_2.Novel.Novel;

import static com.example.roinovel_2.database.BookShelfDbSchema.AUTHOR;
import static com.example.roinovel_2.database.BookShelfDbSchema.LASTUPDATE;
import static com.example.roinovel_2.database.BookShelfDbSchema.LASTUPDATEDATE;
import static com.example.roinovel_2.database.BookShelfDbSchema.MAINURL;
import static com.example.roinovel_2.database.BookShelfDbSchema.NAME;
import static com.example.roinovel_2.database.BookShelfDbSchema.SHELFID;
import static com.example.roinovel_2.database.BookShelfDbSchema.TABLENAME;

public class BookShelfBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "BookShelf.db";

    public BookShelfBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create TABLE " + TABLENAME + "(" +
                SHELFID + "  integer primary key , " +
                NAME + ", " + AUTHOR + ", " + LASTUPDATE + ", " + MAINURL + ", " + LASTUPDATEDATE +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void Insert(Novel novel)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,novel.getName());
        contentValues.put(AUTHOR,novel.getAuthor());
        contentValues.put(LASTUPDATE,novel.getLastUpdate());
        contentValues.put(MAINURL,novel.getMainUrl());
        contentValues.put(LASTUPDATEDATE,novel.getLASTUPDATEDATE());
        contentValues.put(SHELFID,novel.getSHELFID());
        this.getWritableDatabase().insert(TABLENAME,null,contentValues);
    }

    public void Delete(int id)
    {
        this.getWritableDatabase().execSQL("DELETE FROM " + TABLENAME + " WHERE Id=" +  id);
    }
}
