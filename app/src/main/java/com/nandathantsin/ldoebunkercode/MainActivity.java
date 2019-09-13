package com.nandathantsin.ldoebunkercode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String alfa_link = "https://last-day-on-earth-survival.fandom.com/wiki/Bunker_Alfa";
    private int day;
    private int month;
    TextView code;
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
        new Alfa().execute(alfa_link);
    }

    class Alfa extends AsyncTask<String,Void, ArrayList<String>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Wait a second");
            progressDialog.setMessage("Loading...");
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
                for(Element cell: td){
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
