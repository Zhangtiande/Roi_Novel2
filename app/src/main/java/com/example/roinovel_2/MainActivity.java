package com.example.roinovel_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roinovel_2.Novel.Novel;
import com.example.roinovel_2.database.ResultBaseHelper;
import com.example.roinovel_2.database.ResultsDbSchema;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.AUTHOR;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.LASTUPDATE;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.MAINURL;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.NAME;
import static com.example.roinovel_2.database.ResultsDbSchema.ResultTable.TABLENAME;

public class MainActivity extends AppCompatActivity {
    private EditText searchEdit;
    private static final String EXTRA_LIST = "MAINACTIVITY.RESULTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = findViewById(R.id.SearchButton);
        searchEdit = findViewById(R.id.editSearchName);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NovelSearch().execute(searchEdit.getText().toString());
            }
        });

    }


    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    class NovelSearch extends AsyncTask<String, Context,Boolean> {
        public ArrayList<Novel> NovelInfo = new ArrayList<Novel>();
        private static final String TAG = "NovelSearch";



        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(String... strings) {

            String NovelName = strings[0];
            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "doInBackground: NovelName Get!");
            URL url = null;
            try {
                url = new URL("http://www.shuquge.com/search.php");
                RequestBody fromBody = new FormBody.Builder()
                        .add("searchkey", NovelName)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML" +
                                ", like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                        .post(fromBody)
                        .build();
                Response response ;
                response = client.newCall(request).execute();
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string());
                Elements Headlines = doc.getElementsByClass("bookbox");
                for (Element s: Headlines)
                {
                    Novel temp = new Novel();
                    Elements elements = s.getElementsByClass("bookname");
                    temp.setName(elements.get(0).text());
                    String pre_url = "http://www.shuquge.com";
                    temp.setMainUrl(pre_url + s.getElementsByTag("a").get(0).attr("href"));
                    temp.setLastUpdate(s.getElementsByTag("a").get(1).text());
                    elements = s.getElementsByClass("author");
                    temp.setAuthor(elements.get(0).text().substring(3));
                    NovelInfo.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: Search Error!" );
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
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
            {
                Toast.makeText((Context) MainActivity.this,"搜索成功！",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onPostExecute: Success!");
                Intent intent = newIntent(MainActivity.this,NovelInfo.size());
                SQLiteDatabase mDataBase = new ResultBaseHelper(MainActivity.this).getWritableDatabase();
                if (!tabbleIsExist(TABLENAME))
                {
                    mDataBase.execSQL("create TABLE " + TABLENAME + "(" +
                            " id integer primary key autoincrement, " +
                            NAME + ", " + AUTHOR + ", " + LASTUPDATE + ", " + MAINURL + ")");
                }
                for (Novel n:NovelInfo)
                {
                    mDataBase.insert(ResultsDbSchema.ResultTable.TABLENAME,null,getContentValues(n));
                }
                startActivity(intent);
            }
        }
    }


    public static Intent newIntent(Context context,int len)
    {
        Intent intent = new Intent(context,ResultActivity.class);
        intent.putExtra(EXTRA_LIST,len);
        return intent;
    }


    public static ContentValues getContentValues(Novel novel){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ResultsDbSchema.ResultTable.NAME,novel.getName());
        contentValues.put(ResultsDbSchema.ResultTable.AUTHOR,novel.getAuthor());
        contentValues.put(ResultsDbSchema.ResultTable.LASTUPDATE,novel.getLastUpdate());
        contentValues.put(ResultsDbSchema.ResultTable.MAINURL,novel.getMainUrl());
        return contentValues;
    }

    public boolean tabbleIsExist(String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = new ResultBaseHelper(MainActivity.this).getReadableDatabase();
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    cursor.close();
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        assert cursor != null;
        cursor.close();
        return result;
    }


}