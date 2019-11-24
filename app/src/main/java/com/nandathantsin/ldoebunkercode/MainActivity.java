package com.nandathantsin.ldoebunkercode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private String alfa_link = "https://last-day-on-earth-survival.fandom.com/wiki/Bunker_Alfa";
    private int[] daysInMonth = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
    private int day;
    private int month;
    private int year;
    TextView code;
    TextView heading;
    TextView no_connection;
    TextView otherCode;
    Button btn_retry;
    Button btn_wrong_code;
    ProgressDialog progressDialog;
    LinearLayout twoMoreCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        twoMoreCode = findViewById(R.id.twoMoreCode);
        otherCode = findViewById(R.id.otherCode);
        day = cal.get(Calendar.DAY_OF_MONTH); // 17
        month = cal.get(Calendar.MONTH); // 5
        year = cal.get(Calendar.YEAR);
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
        btn_wrong_code = findViewById(R.id.wrong_code);
        btn_wrong_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InternetCheck().execute();
            }
        });
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String str = year+""+month;
        String savedString = prefs.getString("codes", "");
        String[] codes = savedString.split(",");
        if(codes[0].equals(str)){
            code.setText(codes[day]);
            String yesterday;
            String tomorrow;
            if(day==1){
                yesterday = prefs.getString("yesterday","");
                tomorrow = codes[day+1];
            }
            else if(day==daysInMonth[month]){
                yesterday = codes[day-1];
                tomorrow = prefs.getString("tomorrow","");
            }
            else{
                yesterday = codes[day-1];
                tomorrow = codes[day+1];
            }
            otherCode.setText(yesterday+"\n"+tomorrow);
        }
        else{
            new InternetCheck().execute();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                return false;
            case android.R.id.home:

                super.onBackPressed();
                return false;
        }


        return super.onOptionsItemSelected(item);
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
                twoMoreCode.setVisibility(View.VISIBLE);
                btn_wrong_code.setVisibility(View.VISIBLE);
                no_connection.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
                new Alfa().execute(alfa_link);
            } else {
                heading.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
                twoMoreCode.setVisibility(View.GONE);
                btn_wrong_code.setVisibility(View.GONE);
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
                String yesterday = "Not found";
                String tomorrow = "Not found";
                int count = 0;
                int daysInLastMonth = daysInMonth[month-1];
                if(month==1 && new GregorianCalendar().isLeapYear(year)){
                    daysInLastMonth++;
                }
                for(Iterator<Element> iter = tr.iterator(); iter.hasNext(); ) {
                    Elements td = iter.next().getElementsByTag("td");
                    if(td.size()!=0)
                        arrayList.add(td.get(month).text());
                    if(month!=11 && count==1){
                        tomorrow = td.get(month+1).text();
                    }
                    else if(month!=0 && count==daysInLastMonth){
                        yesterday = td.get(month-1).text();
                    }
                    count++;
                }
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                prefs.edit().putString("yesterday", yesterday).commit();
                prefs.edit().putString("tomorrow", tomorrow).commit();
//                Elements td = tr.get(day).select("td");
//                for (Element cell : td) {
//                    arrayList.add(cell.text());
//                    //System.out.println(cell.text());
//                }
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }

            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            if(arrayList==null){
                heading.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
                twoMoreCode.setVisibility(View.GONE);
                btn_wrong_code.setVisibility(View.GONE);
                no_connection.setVisibility(View.VISIBLE);
                btn_retry.setVisibility(View.VISIBLE);
            }
            else {
                //code.setText(arrayList.get(month));
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                StringBuilder str = new StringBuilder(year+""+month+",");
                for (int i = 0; i < arrayList.size(); i++) {
                    str.append(arrayList.get(i)).append(",");
                }
                prefs.edit().putString("codes", str.toString()).commit();
                String savedString = prefs.getString("codes", "");
                String[] codes = savedString.split(",");
                code.setText(arrayList.get(day));
                String yesterday;
                String tomorrow;
                if(day==1){
                    yesterday = prefs.getString("yesterday","");
                    tomorrow = codes[day+1];
                }
                else if(day==daysInMonth[month]){
                    yesterday = codes[day-1];
                    tomorrow = prefs.getString("tomorrow","");
                }
                else{
                    yesterday = codes[day-1];
                    tomorrow = codes[day+1];
                }
                otherCode.setText(yesterday+"\n"+tomorrow);
            }
            progressDialog.dismiss();
        }
    }
}
