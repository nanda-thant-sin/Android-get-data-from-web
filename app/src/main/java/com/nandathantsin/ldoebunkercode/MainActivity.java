package com.nandathantsin.ldoebunkercode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String alfa_link = "https://last-day-on-earth-survival.fandom.com/wiki/Bunker_Alfa";
    private int day;
    private int month;
    TextView code;
    TextView heading;
    TextView no_connection;
    Button btn_retry;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        day = cal.get(Calendar.DAY_OF_MONTH); // 17
        month = cal.get(Calendar.MONTH); // 5
        code = findViewById(R.id.code);
        heading = findViewById(R.id.heading);
        no_connection = findViewById(R.id.no_connection);
        btn_retry = findViewById(R.id.button_retry);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InternetCheck().execute();
            }
        });
        new InternetCheck().execute();
    }

    class InternetCheck extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.wait_a_second));
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean internet) {
            progressDialog.dismiss();
            if (internet) {
                heading.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
                no_connection.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
                new Alfa().execute(alfa_link);
            } else {
                heading.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
                no_connection.setVisibility(View.VISIBLE);
                btn_retry.setVisibility(View.VISIBLE);
            }
        }
    }

    class Alfa extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.wait_a_second));
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements table = document.getElementsByClass("article-table");
                Elements tr = table.get(1).select("tr");
                Elements td = tr.get(day).select("td");
                for (Element cell : td) {
                    arrayList.add(cell.text());
                    //System.out.println(cell.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            code.setText(arrayList.get(month));
            progressDialog.dismiss();
        }
    }
}
