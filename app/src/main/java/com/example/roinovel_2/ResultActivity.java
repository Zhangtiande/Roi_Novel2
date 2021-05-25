package com.example.roinovel_2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roinovel_2.Novel.Novel;
import com.example.roinovel_2.Novel.NovelAdapter;
import com.example.roinovel_2.database.ResultBaseHelper;
import com.example.roinovel_2.database.ResultsDbSchema;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private final ArrayList<Novel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getArrayList();
        RecyclerView recyclerView = findViewById(R.id.list_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        NovelAdapter novelAdapter = new NovelAdapter(arrayList);
        recyclerView.setAdapter(novelAdapter);
    }


    private void getArrayList() {
        SQLiteDatabase db = new ResultBaseHelper(this).getWritableDatabase();
        Cursor cursor = db.query(ResultsDbSchema.ResultTable.TABLENAME, null, null
                , null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Novel novel = new Novel();
                novel.setName(cursor.getString(cursor.getColumnIndex("NovelName")));
                novel.setMainUrl(cursor.getString(cursor.getColumnIndex("MainUrl")));
                novel.setAuthor(cursor.getString(cursor.getColumnIndex("Author")));
                novel.setLastUpdate(cursor.getString(cursor.getColumnIndex("LastUpdate")));
                arrayList.add(novel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.execSQL("DELETE FROM " + ResultsDbSchema.ResultTable.TABLENAME);
        Log.d(TAG, "getArrayList: Get Successs!");
    }


}