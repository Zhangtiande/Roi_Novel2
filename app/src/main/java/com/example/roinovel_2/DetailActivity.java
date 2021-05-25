package com.example.roinovel_2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.roinovel_2.Novel.DownloadThread;
import com.example.roinovel_2.Novel.Novel;
import com.example.roinovel_2.dummy.DummyContent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private static final HashMap<Integer, String> Content = new HashMap<>();
    private static final String TAG = "DetailActivity";
    public static DummyContent dummyContent;
    private static Novel novel;
    public ProgressBar progressBar;
    public TextView textView;

    public synchronized static void getContent(int id, String content) {
        content = novel.getChapterName().get(id) + '\n' + content;
        Content.put(id, content);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        novel = (Novel) intent.getSerializableExtra("DETAIL");
        ((TextView) findViewById(R.id.detailauthor)).setText("作者：" + novel.getAuthor());
        ((TextView) findViewById(R.id.detailname)).setText("书名：" + novel.getName());
        new NovelLoad().execute(novel);
        findViewById(R.id.Look).setOnClickListener(v -> {
            Intent intent1 = new Intent(DetailActivity.this, ChapterActivity.class);
            intent1.putExtra("DetailActivity", novel);
            startActivity(intent1);
        });
        findViewById(R.id.Look).setEnabled(false);
        findViewById(R.id.Down).setOnClickListener(v -> new NovelDownload().execute(novel));
        progressBar = findViewById(R.id.progressBar3);
        textView = findViewById(R.id.textView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                ParcelFileDescriptor pfd;
                try {
                    pfd = getContentResolver().openFileDescriptor(uri, "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    for (Integer i : Content.keySet()) {
                        fileOutputStream.write(Objects.requireNonNull(Content.get(i)).getBytes());
                    }
                    fileOutputStream.close();
                    pfd.close();
                    Toast.makeText(this, "文件写入成功！", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: File writing error");
                }

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class NovelLoad extends AsyncTask<Novel, Integer, Boolean> {
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
                String resp = Objects.requireNonNull(response.body()).string();
                p = "<dd><a href=\"(.*)\">(.*)</a></dd>";   //正则表达式获取章节网址与章节名字
                pattern = Pattern.compile(p);
                m = pattern.matcher(resp);
                int i = 0;
                while (m.find()) {
                    if (i < 12) {
                        i++;
                        continue;
                    }
                    novel.getChapterName().put(i - 12, m.group(2)); //放入章节名称
                    novel.getChapterUrlList().put((i - 12), pre_url + m.group(1));  //放入章节网址
                    i++;
                }
                pattern = Pattern.compile(img);
                m = pattern.matcher(resp);
                while (m.find()) {
                    ImgUrl = m.group(1);        //获取小说封面
                }
                int start, end;
                start = ImgUrl.indexOf("h");
                end = ImgUrl.indexOf("\"", start + 1);
                ImgUrl = ImgUrl.substring(start, end);
                pattern = Pattern.compile("<meta property=\"og:description\" content=\"(.*)\" /> \n");      //获取小说简介
                m = pattern.matcher(resp);
                while (m.find()) {
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
            if (aBoolean) {
                ImageView imageView = findViewById(R.id.detailimg);
                ((TextView) findViewById(R.id.detaildes)).setText("简介:\n" + des);
                DetailActivity.novel = this.novel;
                Log.d(TAG, "onPostExecute: ImgUrl:" + ImgUrl);
                Glide.with(DetailActivity.this).load(ImgUrl).into(imageView);
                Toast.makeText(DetailActivity.this, "目录获取成功！", Toast.LENGTH_SHORT)
                        .show();
                ArrayList<String> arrayList = new ArrayList<>();
                for (Integer i : novel.getChapterName().keySet()) {
                    arrayList.add(novel.getChapterName().get(i));
                }
                DetailActivity.dummyContent = new DummyContent(arrayList);
                findViewById(R.id.Look).setEnabled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    public class NovelDownload extends AsyncTask<Novel, Integer, Boolean> {
        private final ExecutorService fixedThreadPoll = Executors.newFixedThreadPool(9);
        private final ArrayList<DownloadThread> threads = new ArrayList<>();
        public int all;
        public int hasFinished = 0;
        private Novel novel;

        @Override
        protected Boolean doInBackground(Novel... novels) {
            novel = novels[0];
            HashMap<Integer, String> map = (HashMap<Integer, String>) novel.getChapterUrlList();
            for (Integer i : map.keySet()) {
                threads.add(new DownloadThread(i, map.get(i), this));
            }
            all = map.size();
            for (DownloadThread d : threads) {
                this.fixedThreadPoll.execute(d);
            }
            this.fixedThreadPoll.shutdown();
            while (!fixedThreadPoll.isTerminated()) {
                publishProgress(hasFinished);
            }
            return true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            textView.setText("下载进度：" + values[0] + '/' + all);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/txt");
                intent.putExtra(Intent.EXTRA_TITLE, novel.getName() + ".txt");

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when your app creates the document.
                DetailActivity.this.startActivityForResult(intent, 1);
            }
        }

        public synchronized void Progress() {
            hasFinished++;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int size = DetailActivity.novel.getChapterUrlList().size();
            progressBar.setMax(size);
        }
    }

}