package com.example.roinovel_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.roinovel_2.Novel.Novel;

import java.util.ArrayList;

public class ChapterActivity extends AppCompatActivity {

    private static Novel novel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        Intent intent = getIntent();
        novel = (Novel) intent.getSerializableExtra("DetailActivity");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment);

        if (fragment == null)
        {
            fragment = new ItemFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment,fragment).commit();
        }
    }
}