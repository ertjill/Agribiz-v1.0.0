package com.example.agribiz_v100.location;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CityManagement {
    private static final String TAG = "CityManagement";
    final String cities_url = "https://psgc.gitlab.io/api/provinces/072200000/cities-municipalities/";
    public List<JSONObject> citiesList;
    private List<String> citiesNames;
    private AsyncResponse delegate = null;

    public CityManagement(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
        citiesList = new ArrayList<>();
        citiesNames = new ArrayList<>();
        new MyCities().execute();
    }

    public List<JSONObject> getCitiesList() {
        return citiesList;
    }

    public List<String> getCitiesNames() {
        return citiesNames;
    }

    public class MyCities extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... provinceCode) {
            getCitiesList(getCitiesString(cities_url));
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            delegate.processFinish(citiesList, citiesNames);
            for (JSONObject city : citiesList
            ) {
                try {
                    Log.d(TAG, city.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public String getCitiesString(String cities) {
        String string = "";
        try {
            URL url = new URL(cities);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String stringBuffer;
            while ((stringBuffer = bufferedReader.readLine()) != null) {
                string = String.format("%s%s", string, stringBuffer);
            }
            bufferedReader.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return string;
    }

    public void getCitiesList(String jsonString) {
        try {
            JSONArray cities = new JSONArray(jsonString);
            for (int i = 0; i < cities.length(); i++) {
                citiesList.add(cities.getJSONObject(i));
                citiesNames.add(cities.getJSONObject(i).get("name").toString());
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public interface AsyncResponse {
        void processFinish(List<JSONObject> citiesList, List<String> citiesNames);
    }

}
