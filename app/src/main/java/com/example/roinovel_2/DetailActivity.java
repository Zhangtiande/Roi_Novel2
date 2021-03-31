package com.example.roinovel_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.roinovel_2.Novel.Novel;
import com.example.roinovel_2.dummy.DummyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private static Novel novel;
    public static DummyContent dummyContent ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        novel = (Novel) intent.getSerializableExtra("DETAIL");
        ((TextView)findViewById(R.id.detailauthor)).setText("作者:" + novel.getAuthor());
        ((TextView)findViewById(R.id.detailname)).setText("书名：" + novel.getName());
        new NovelLoad().execute(novel);
        findViewById(R.id.Look).setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailActivity.this,ChapterActivity.class);
            intent1.putExtra("DetailActivity",novel);
            startActivity(intent1);
        });
        findViewById(R.id.Look).setEnabled(false);
    }




    @SuppressLint("StaticFieldLeak")
    class NovelLoad extends AsyncTask<Novel,Integer,Boolean> {
        public ArrayList<Novel> NovelInfo = new ArrayList<Novel>();
        private static final String TAG = "NovelSearch";
        private String ImgUrl;
        private String des;
        private Novel novel;

        public NovelLoad() {
            super();
        }

        @Override
        protected Boolean doInBackground(Novel... novels) {
            String img = "img src=\"(.*)\"";
            novel = novels[0];
            String p = "(.*)index.html";
            Pattern pattern = Pattern.compile(p);
            Matcher m = pattern.matcher(novel.getMainUrl());
            String pre_url = null;
            if (m.find())
                pre_url = m.group(1);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(novel.getMainUrl())
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML" +
                            ", like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String resp = response.body().string();
                p = "<dd><a href=\"(.*)\">(.*)</a></dd>";
                pattern = Pattern.compile(p);
                m = pattern.matcher(resp);
                int i = 0;
                while (m.find()) {
                    if (i < 12) {
                        i++;
                        continue;
                    }
                    novel.getChapterName().put(i - 12, m.group(2));
                    novel.getChapterUrlList().put((i - 12), pre_url + m.group(1));
                    i++;
                }
                pattern = Pattern.compile(img);
                m = pattern.matcher(resp);
                while (m.find())
                {
                    ImgUrl = m.group(1);
                }
                int start,end;
                start = ImgUrl.indexOf("h");
                end = ImgUrl.indexOf("\"",start+1);
                ImgUrl = ImgUrl.substring(start,end);
                pattern = Pattern.compile("<meta property=\"og:description\" content=\"(.*)\" /> \n");
                m = pattern.matcher(resp);
                while (m.find())
                {
                    des = m.group(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         * Invoked directly by {@link #execute} or {@link #executeOnExecutor}.
         * The default version does nothing.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * To better support testing frameworks, it is recommended that this be
         * written to tolerate direct execution as part of the execute() call.
         * The default version does nothing.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param aBoolean The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         */
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
            {
                ImageView imageView = (ImageView) findViewById(R.id.detailimg);
                ((TextView)findViewById(R.id.detaildes)).setText("简介:\n" + des);
                DetailActivity.novel = this.novel;
                Log.d(TAG, "onPostExecute: ImgUrl:" + ImgUrl);
                Glide.with(DetailActivity.this).load(ImgUrl).into(imageView);
                Toast.makeText(DetailActivity.this,"目录获取成功！",Toast.LENGTH_SHORT)
                        .show();
                ArrayList<String> arrayList = new ArrayList<>();
                for (Integer i:novel.getChapterName().keySet())
                {
                    arrayList.add(novel.getChapterName().get(i));
                }
                DetailActivity.dummyContent = new DummyContent(arrayList);
                findViewById(R.id.Look).setEnabled(true);
            }
        }
    }
}