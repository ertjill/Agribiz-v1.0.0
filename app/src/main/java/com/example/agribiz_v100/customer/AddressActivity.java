package com.example.agribiz_v100.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.agribiz_v100.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddressActivity extends AppCompatActivity {
    final String page1_cities = "https://ph-locations-api.buonzz.com/v1/cities?page=1";
    final String page2_cities = "https://ph-locations-api.buonzz.com/v1/cities?page=2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        String page1_cities_result,page2_cities_result;

        @Override
        protected Void doInBackground(Void... voids) {
            URL url, url1;
            try {
                url = new URL(page1_cities);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                page1_cities_result = string;

                url1 = new URL(page2_cities);
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(url1.openStream()));
                String stringBuffer1;
                String string1 = "";
                while ((stringBuffer1 = bufferedReader1.readLine()) != null) {
                    string1 = String.format("%s%s", string1, stringBuffer1);
                }
                bufferedReader.close();
                page2_cities_result = string1;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject mainObject = new JSONObject(page1_cities_result);
                JSONObject mainObject1 = new JSONObject(page2_cities_result);
                String data = mainObject.getString("data");
                String data1 = mainObject1.getString("data");
                JSONArray array = new JSONArray(data);
                List<JSONObject> cities= new  ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    if (object.getString("province_code").equals("0722"))
                        cities.add(object);
                }
                JSONArray array1 = new JSONArray(data1);
                for (int i = 0; i < array1.length(); i++) {
                    JSONObject object = array1.getJSONObject(i);
                    if (object.getString("province_code").equals("0722"))
                        cities.add(object);
                }
                Log.i("tag" ,cities.size()+" Cities");
                for (JSONObject ob: cities) {
                    Log.i("tag" ,ob.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tag",e.getMessage());
            }

        }
    }

}