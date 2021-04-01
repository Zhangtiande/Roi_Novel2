package com.example.roinovel_2.Novel;

import com.example.roinovel_2.DetailActivity;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadThread implements Runnable{

    private final int id;
    private final String url;
    public String con;
    private DetailActivity.NovelDownload novelDownload;


    public DownloadThread(int id, String url, DetailActivity.NovelDownload download) {
        this.id = id;
        this.url = url;
        this.novelDownload = download;
    }

    @Override
    public void run() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML" +
                            ", like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.54")
                    .get()
                    .build();
            Response response;
            response = client.newCall(request).execute();
            if (response.code() != 200)
            {
                response.close();
            }
            String resp = null;
            resp = Objects.requireNonNull(response.body()).string();
            String pattern = "&nbsp;&nbsp;&nbsp;&nbsp;(.*)\\n?";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(resp);
            while (m.find())
            {
                String temp = m.group(1);
                int i;
                if (temp != null && (i = temp.indexOf("<br />")) != -1) {
                    temp = temp.substring(0, i);
                }
                con = String.format("%s%s\n", con, temp);
            }
            con = con + "\n\n";
            DetailActivity.getContent(this.id,this.con);
            this.novelDownload.Progress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
