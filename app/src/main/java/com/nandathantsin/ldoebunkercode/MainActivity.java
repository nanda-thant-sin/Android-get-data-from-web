package com.nandathantsin.ldoebunkercode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private String TAG = MainActivity.class.getSimpleName();
    private String[] links = new String[]{
            "https://gist.githubusercontent.com/nanda-thant-sin/cdb3b31f25022a3f2d16dcb2f542880b/raw/06e2d26439c4b920e08fac13d93d96d917d40f0c/0.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/a89d3233266d94793d9d81fe73642f09/raw/9a5acab330b9a921fcaf19315fb7b689deb62f79/1.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/9643df8104fdc5d13bec14ed20abc8ea/raw/fb06dd67d9d229559143c0006766972dbf3c040a/2.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/e3089850c406b6f41a0d8c9a06672429/raw/087705db5b6a5686b0108a5922253261fe2e665b/3.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/276ab168dce838b938cd46d09991f1bf/raw/22974432a9ccd6648733216dff41970fd936fc14/4.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/fd3316932f2ffa805c0c375246aa7524/raw/775a361c550b2ecd46ad2eb52cc1cd8a5eeb9d6e/5.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/862021ff7e5f6f733ec0efc76bb1ddf2/raw/ac3e354082c45ed56dd6cb3e29533d19039025d9/6.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/d0d3c8798f94d6c334f0c87eb701dd01/raw/a8856a0260163c7e96626b467057f46f62d7d6bd/7.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/c217864adfd9b6778d0d62c3eae8a8dc/raw/0eee88679695677eaf0f5e045e9bf685e222aa3e/8.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/e7ca8f9eeb2de45b5dc8b2114ea2610d/raw/d2cbbc25acc0fb3e7ab34adc014897ec9d0a00cd/9.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/de35a22a6ab4ec30d163de995ce3f2fa/raw/83d4cb054239ffce906ba2cf0ea2b1020f6d4388/10.json",
            "https://gist.githubusercontent.com/nanda-thant-sin/2bb9f97a402355027de655edf6c00037/raw/9cce62e702673c62821ec5cc29284a843fb48214/11.json"};
    private int[] daysInMonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
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
        try{
            String str = year + "" + month;
            String savedString = prefs.getString("codes", "");
            String[] codes = savedString.split(",");
            if (codes[0].equals(str)) {
                code.setText(codes[day+1]);
                otherCode.setText(codes[day] + "\n" + codes[day+2]);
            } else {
                new InternetCheck().execute();
            }
        }
        catch (Exception e){
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
                new Alfa().execute(links[month]);
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
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = strings[0];
            String jsonStr = sh.makeServiceCall(url);
            StringBuilder sb = new StringBuilder();
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray codes = jsonObj.getJSONArray("codes");
                    for (int i = 0; i < codes.length(); i++) {
                        String c = codes.getString(i);
                        arrayList.add(c);
                    }


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    return null;

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                return null;
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            super.onPostExecute(arrayList);
            if (arrayList == null) {
                heading.setVisibility(View.GONE);
                code.setVisibility(View.GONE);
                twoMoreCode.setVisibility(View.GONE);
                btn_wrong_code.setVisibility(View.GONE);
                no_connection.setVisibility(View.VISIBLE);
                btn_retry.setVisibility(View.VISIBLE);
            } else {
                //code.setText(arrayList.get(month));
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                StringBuilder str = new StringBuilder(year + "" + month + ",");
                for (int i = 0; i < arrayList.size(); i++) {
                    str.append(arrayList.get(i)).append(",");
                }
                prefs.edit().putString("codes", str.toString()).commit();
                String savedString = prefs.getString("codes", "");
                String[] codes = savedString.split(",");
                code.setText(codes[day+1]);
                otherCode.setText(codes[day] + "\n" + codes[day+2]);
            }
            progressDialog.dismiss();
        }
    }
}
